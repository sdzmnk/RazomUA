package com.example.razomua.repository

import com.example.razomua.model.ChatUser
import com.example.razomua.model.Message
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import android.util.Log

class FirebaseChatRepository {
    private val database = FirebaseDatabase.getInstance()
    private val messagesRef = database.getReference("messages")
    private val usersRef = database.getReference("users")
    private val auth = FirebaseAuth.getInstance()

    fun getCurrentUserId(): String? = auth.currentUser?.uid
    fun getCurrentUserName(): String? = auth.currentUser?.displayName ?: auth.currentUser?.email?.substringBefore("@")

    suspend fun sendMessage(chatId: String, message: Message): Result<Unit> {
        return try {
            val currentUserId = getCurrentUserId() ?: return Result.failure(Exception("User not authenticated"))

            Log.d("FirebaseChat", "=== SENDING MESSAGE ===")
            Log.d("FirebaseChat", "ChatId: $chatId")

            val messageId = messagesRef.child(chatId).push().key
                ?: return Result.failure(Exception("Failed to generate message ID"))

            val messageWithId = message.copy(
                id = messageId,
                timestamp = 0
            )

            val messageData = mapOf(
                "id" to messageWithId.id,
                "senderId" to messageWithId.senderId,
                "text" to messageWithId.text,
                "timestamp" to ServerValue.TIMESTAMP,
                "isCurrentUser" to messageWithId.isCurrentUser
            )

            messagesRef.child(chatId).child(messageId).setValue(messageData).await()

            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("FirebaseChat", "Error sending message", e)
            Result.failure(e)
        }
    }
    fun getMessages(chatId: String): Flow<List<Message>> = callbackFlow {
        val currentUserId = getCurrentUserId()

        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val messages = mutableListOf<Message>()
                for (messageSnapshot in snapshot.children) {
                    val message = messageSnapshot.getValue(Message::class.java)
                    message?.let {
                        messages.add(it.copy(isCurrentUser = it.senderId == currentUserId))
                    }
                }
                trySend(messages.sortedWith(compareBy({ it.timestamp }, { it.id })))
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }

        messagesRef.child(chatId)
            .orderByChild("timestamp")
            .addValueEventListener(listener)

        awaitClose {
            messagesRef.child(chatId).removeEventListener(listener)
        }
    }
    fun getUserMessagesCountFlow(): Flow<Int> = callbackFlow {
        val currentUserId = getCurrentUserId()
        if (currentUserId == null) {
            trySend(0)
            close()
            return@callbackFlow
        }

        val userChatsRef = usersRef.child(currentUserId).child("chats")

        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val chatUsers = snapshot.children.toList()

                if (chatUsers.isEmpty()) {
                    trySend(0)
                    return
                }

                var totalMessages = 0
                var processed = 0
                val totalChats = chatUsers.size

                chatUsers.forEach { chatSnapshot ->
                    val otherUserId = chatSnapshot.key ?: run {
                        processed++
                        return@forEach
                    }

                    val chatId = if (currentUserId < otherUserId) {
                        "${currentUserId}_${otherUserId}"
                    } else {
                        "${otherUserId}_${currentUserId}"
                    }

                    messagesRef.child(chatId).get()
                        .addOnSuccessListener { messagesSnapshot ->
                            totalMessages += messagesSnapshot.childrenCount.toInt()
                            processed++
                            if (processed == totalChats) {
                                trySend(totalMessages)
                            }
                        }
                        .addOnFailureListener {
                            processed++
                            if (processed == totalChats) {
                                trySend(totalMessages)
                            }
                        }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }

        userChatsRef.addValueEventListener(listener)

        awaitClose {
            userChatsRef.removeEventListener(listener)
        }
    }

    fun getMyChats(): Flow<List<ChatUser>> = callbackFlow {
        val currentUserId = getCurrentUserId()

        if (currentUserId == null) {
            trySend(emptyList())
            close()
            return@callbackFlow
        }

        val userChatsRef = usersRef.child(currentUserId).child("chats")

        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val chatsList = mutableListOf<ChatUser>()
                val userIds = mutableListOf<String>()


                snapshot.children.forEach { chatSnapshot ->
                    val chatUser = chatSnapshot.getValue(ChatUser::class.java)
                    if (chatUser != null) {
                        chatsList.add(chatUser)
                        userIds.add(chatUser.id)
                    }
                }

                if (chatsList.isEmpty()) {
                    trySend(emptyList())
                    return
                }

                var processedCount = 0
                val updatedChats = mutableListOf<ChatUser>()

                chatsList.forEach { chat ->
                    usersRef.child(chat.id).get()
                        .addOnSuccessListener { userSnapshot ->
                            val photoUrl = userSnapshot.child("photoUrl").getValue(String::class.java) ?: ""
                            val isOnline = userSnapshot.child("isOnline").getValue(Boolean::class.java) ?: false

                            val updatedChat = chat.copy(
                                photoUrl = photoUrl,
                                isOnline = isOnline
                            )

                            updatedChats.add(updatedChat)
                            processedCount++

                            Log.d("FirebaseChat", "Loaded chat: ${chat.name}, photoUrl: $photoUrl, isOnline: $isOnline")

                            if (processedCount == chatsList.size) {
                                trySend(updatedChats.sortedByDescending { it.lastSeen })
                            }
                        }
                        .addOnFailureListener { error ->
                            Log.e("FirebaseChat", "Failed to load user data for ${chat.id}", error)

                            updatedChats.add(chat)
                            processedCount++

                            if (processedCount == chatsList.size) {
                                trySend(updatedChats.sortedByDescending { it.lastSeen })
                            }
                        }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("FirebaseChat", "Error loading chats", error.toException())
                close(error.toException())
            }
        }

        userChatsRef.addValueEventListener(listener)

        awaitClose {
            userChatsRef.removeEventListener(listener)
        }
    }

    suspend fun updateUserStatus(isOnline: Boolean): Result<Unit> {
        return try {
            val userId = getCurrentUserId() ?: return Result.failure(Exception("User not authenticated"))
            val updates = mapOf<String, Any>(
                "isOnline" to isOnline,
                "lastSeen" to System.currentTimeMillis()
            )

            usersRef.child(userId).updateChildren(updates).await()

            val chatsSnapshot = usersRef.child(userId).child("chats").get().await()
            chatsSnapshot.children.forEach { chatSnapshot ->
                val otherUserId = chatSnapshot.key ?: return@forEach

                usersRef.child(otherUserId).child("chats").child(userId)
                    .child("isOnline").setValue(isOnline).await()
            }

            Log.d("FirebaseChat", "User status updated to online=$isOnline")
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun initializeCurrentUser(photoUrl: String = ""): Result<Unit> {
        return try {
            val userId = getCurrentUserId() ?: return Result.failure(Exception("User not authenticated"))
            val userName = getCurrentUserName() ?: "Користувач"

            val userData = mapOf<String, Any>(
                "id" to userId,
                "name" to userName,
                "photoUrl" to photoUrl,
                "isOnline" to true,
                "lastSeen" to System.currentTimeMillis()
            )

            usersRef.child(userId).updateChildren(userData).await()
            Log.d("FirebaseChat", "Current user initialized: $userName ($userId) with photo: $photoUrl")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("FirebaseChat", "Error initializing user", e)
            Result.failure(e)
        }
    }

    fun saveUserData(uid: String, data: Map<String, Any>) =
        usersRef.child(uid).updateChildren(data)


    fun setupPresence(): Result<Unit> {
        return try {
            val userId = getCurrentUserId() ?: return Result.failure(Exception("Not authenticated"))

            val userStatusRef = usersRef.child(userId)
            val connectedRef = database.getReference(".info/connected")

            connectedRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val connected = snapshot.getValue(Boolean::class.java) ?: false

                    if (connected) {
                        Log.d("FirebaseChat", "User connected to Firebase")

                        userStatusRef.updateChildren(mapOf(
                            "isOnline" to true,
                            "lastSeen" to com.google.firebase.database.ServerValue.TIMESTAMP
                        ))

                        userStatusRef.onDisconnect().updateChildren(mapOf(
                            "isOnline" to false,
                            "lastSeen" to com.google.firebase.database.ServerValue.TIMESTAMP
                        ))

                        Log.d("FirebaseChat", "Presence configured for user $userId")
                    } else {
                        Log.d("FirebaseChat", "User disconnected from Firebase")
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("FirebaseChat", "Failed to setup presence", error.toException())
                }
            })

            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("FirebaseChat", "Error setting up presence", e)
            Result.failure(e)
        }
    }


    suspend fun createOrUpdateChat(currentUserId: String, otherUserId: String): Result<Unit> {
        return try {
            val otherUserSnapshot = usersRef.child(otherUserId).get().await()
            val otherUserName = otherUserSnapshot.child("name").getValue(String::class.java)
                ?: "Користувач"
            val otherUserPhotoUrl = otherUserSnapshot.child("photoUrl").getValue(String::class.java)
                ?: ""
            val otherUserIsOnline = otherUserSnapshot.child("isOnline").getValue(Boolean::class.java)
                ?: false

            val currentUserSnapshot = usersRef.child(currentUserId).get().await()
            val currentUserName = currentUserSnapshot.child("name").getValue(String::class.java)
                ?: getCurrentUserName() ?: "Користувач"
            val currentUserPhotoUrl = currentUserSnapshot.child("photoUrl").getValue(String::class.java)
                ?: ""
            val currentUserIsOnline = currentUserSnapshot.child("isOnline").getValue(Boolean::class.java)
                ?: false

            val chatForCurrentUser = mapOf<String, Any>(
                "id" to otherUserId,
                "name" to otherUserName,
                "photoUrl" to otherUserPhotoUrl,
                "isOnline" to otherUserIsOnline,
                "lastMessage" to "",
                "lastSeen" to System.currentTimeMillis()
            )
            usersRef.child(currentUserId).child("chats").child(otherUserId)
                .updateChildren(chatForCurrentUser).await()

            val chatForOtherUser = mapOf<String, Any>(
                "id" to currentUserId,
                "name" to currentUserName,
                "photoUrl" to currentUserPhotoUrl,
                "isOnline" to currentUserIsOnline,
                "lastMessage" to "",
                "lastSeen" to System.currentTimeMillis()
            )
            usersRef.child(otherUserId).child("chats").child(currentUserId)
                .updateChildren(chatForOtherUser).await()

            Log.d("FirebaseChat", "Chat created/updated between $currentUserId and $otherUserId")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("FirebaseChat", "Error creating/updating chat", e)
            Result.failure(e)
        }
    }
}