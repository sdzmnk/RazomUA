//package com.example.razomua.repository
//
//import com.example.razomua.model.ChatUser
//import com.example.razomua.model.Message
//import com.google.firebase.auth.FirebaseAuth
//import com.google.firebase.database.*
//import kotlinx.coroutines.channels.awaitClose
//import kotlinx.coroutines.flow.Flow
//import kotlinx.coroutines.flow.callbackFlow
//import kotlinx.coroutines.tasks.await
//
//class FirebaseChatRepository {
//    private val database = FirebaseDatabase.getInstance()
//    private val messagesRef = database.getReference("messages")
//    private val usersRef = database.getReference("users")
//    private val auth = FirebaseAuth.getInstance()
//
//    fun getCurrentUserId(): String? = auth.currentUser?.uid
//    fun getCurrentUserName(): String? = auth.currentUser?.displayName ?: auth.currentUser?.email?.substringBefore("@")
//
//    suspend fun sendMessage(chatId: String, message: Message): Result<Unit> {
//        return try {
//            val currentUserId = getCurrentUserId() ?: return Result.failure(Exception("User not authenticated"))
//
//            val messageId = messagesRef.child(chatId).push().key ?: return Result.failure(Exception("Failed to generate message ID"))
//            val messageWithId = message.copy(id = messageId)
//            messagesRef.child(chatId).child(messageId).setValue(messageWithId).await()
//
//            // 1. ВИЛУЧИТИ ID КОРИСТУВАЧІВ З CHAT ID
//            // Припускаємо, що chatId має формат: UserId1_UserId2
//            val userIds = chatId.split("_")
//
//            // Запобігаємо помилці, якщо формат chatId неправильний
//            if (userIds.size != 2) {
//                // Можна логувати помилку або просто повернути успіх, якщо повідомлення збережено
//                return Result.success(Unit)
//            }
//
//            val lastMessageUpdate = mapOf<String, Any>(
//                "lastMessage" to message.text,
//                "lastSeen" to System.currentTimeMillis()
//            )
//
//            // 2. ОНОВИТИ ВУЗЛИ КОЖНОГО КОРИСТУВАЧА ОКРЕМО
//            userIds.forEach { userId ->
//                usersRef.child(userId).updateChildren(lastMessageUpdate).await()
//            }
//
//            // Старий помилковий код був тут:
//            // usersRef.child(chatId).child("lastMessage").setValue(message.text).await()
//            // usersRef.child(chatId).child("lastSeen").setValue(System.currentTimeMillis()).await()
//
//            Result.success(Unit)
//        } catch (e: Exception) {
//            Result.failure(e)
//        }
//    }
//
//    fun getMessages(chatId: String): Flow<List<Message>> = callbackFlow {
//        val currentUserId = getCurrentUserId()
//
//        val listener = object : ValueEventListener {
//            override fun onDataChange(snapshot: DataSnapshot) {
//                val messages = mutableListOf<Message>()
//                for (messageSnapshot in snapshot.children) {
//                    val message = messageSnapshot.getValue(Message::class.java)
//                    message?.let {
//                        messages.add(it.copy(isCurrentUser = it.senderId == currentUserId))
//                    }
//                }
//                trySend(messages.sortedBy { it.timestamp })
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//                close(error.toException())
//            }
//        }
//
//        messagesRef.child(chatId).addValueEventListener(listener)
//
//        awaitClose {
//            messagesRef.child(chatId).removeEventListener(listener)
//        }
//    }
//
//    fun getAllUsers(): Flow<List<ChatUser>> = callbackFlow {
//        val currentUserId = getCurrentUserId()
//
//        val listener = object : ValueEventListener {
//            override fun onDataChange(snapshot: DataSnapshot) {
//                val users = mutableListOf<ChatUser>()
//                for (userSnapshot in snapshot.children) {
//                    val user = userSnapshot.getValue(ChatUser::class.java)
//                    if (user != null && user.id != currentUserId) {
//                        users.add(user)
//                    }
//                }
//                trySend(users.sortedByDescending { it.lastSeen })
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//                close(error.toException())
//            }
//        }
//
//        usersRef.addValueEventListener(listener)
//
//        awaitClose {
//            usersRef.removeEventListener(listener)
//        }
//    }
//
//    suspend fun updateUserStatus(isOnline: Boolean): Result<Unit> {
//        return try {
//            val userId = getCurrentUserId() ?: return Result.failure(Exception("User not authenticated"))
//            val updates = mapOf<String, Any>(
//                "isOnline" to isOnline,
//                "lastSeen" to System.currentTimeMillis()
//            )
//            usersRef.child(userId).updateChildren(updates).await()
//            Result.success(Unit)
//        } catch (e: Exception) {
//            Result.failure(e)
//        }
//    }
//
//    suspend fun initializeCurrentUser(): Result<Unit> {
//        return try {
//            val userId = getCurrentUserId() ?: return Result.failure(Exception("User not authenticated"))
//            val userName = getCurrentUserName() ?: "Користувач"
//
//            val userData = mapOf<String, Any>(
//                "id" to userId,
//                "name" to userName,
//                "isOnline" to true,
//                "lastSeen" to System.currentTimeMillis(),
//                "lastMessage" to ""
//            )
//
//            usersRef.child(userId).updateChildren(userData).await()
//            Result.success(Unit)
//        } catch (e: Exception) {
//            Result.failure(e)
//        }
//    }
//
//    suspend fun createTestUsers() {
//        val testUsers = listOf(
//            ChatUser(
//                id = "test_user_1",
//                name = "Андрій",
//                lastMessage = "Привіт, як справи?",
//                isOnline = true,
//                lastSeen = System.currentTimeMillis()
//            ),
//            ChatUser(
//                id = "test_user_2",
//                name = "Славік",
//                lastMessage = "Буде зустріч завтра?",
//                isOnline = false,
//                lastSeen = System.currentTimeMillis() - 3600000
//            )
//        )
//
//        testUsers.forEach { user ->
//            usersRef.child(user.id).setValue(user).await()
//        }
//    }
//}

package com.example.razomua.repository

import com.example.razomua.model.ChatUser
import com.example.razomua.model.Message
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import android.util.Log // Добавьте для логирования

class FirebaseChatRepository {
    private val database = FirebaseDatabase.getInstance()
    private val messagesRef = database.getReference("messages")
    private val usersRef = database.getReference("users") // Основной узел для списка чатов (пользователей)
    private val auth = FirebaseAuth.getInstance()

    fun getCurrentUserId(): String? = auth.currentUser?.uid
    fun getCurrentUserName(): String? = auth.currentUser?.displayName ?: auth.currentUser?.email?.substringBefore("@")

    suspend fun sendMessage(chatId: String, message: Message): Result<Unit> {
        return try {
            val currentUserId = getCurrentUserId() ?: return Result.failure(Exception("User not authenticated"))

            Log.d("FirebaseChat", "=== SENDING MESSAGE ===")
            Log.d("FirebaseChat", "ChatId: $chatId")
            Log.d("FirebaseChat", "Current user: $currentUserId")
            Log.d("FirebaseChat", "Message: ${message.text}")

            val messageId = messagesRef.child(chatId).push().key ?: return Result.failure(Exception("Failed to generate message ID"))
            val messageWithId = message.copy(id = messageId)
            messagesRef.child(chatId).child(messageId).setValue(messageWithId).await()

            Log.d("FirebaseChat", "Message saved at: messages/$chatId/$messageId")

            val userIds = chatId.split("_")
            if (userIds.size != 2) {
                Log.e("FirebaseChat", "Invalid chatId format: $chatId")
                return Result.success(Unit)
            }

            val otherUserId = userIds.first { it != currentUserId }
            Log.d("FirebaseChat", "Other user: $otherUserId")

            val otherUserName = usersRef.child(otherUserId).child("name").get().await().getValue(String::class.java) ?: "Співрозмовник"
            val currentUserName = getCurrentUserName() ?: "Ви"

            val currentUserUpdate = mapOf<String, Any>(
                "id" to otherUserId,
                "name" to otherUserName,
                "lastMessage" to "Ви: ${message.text}",
                "lastSeen" to System.currentTimeMillis()
            )
            usersRef.child(currentUserId).child("chats").child(otherUserId).updateChildren(currentUserUpdate).await()
            Log.d("FirebaseChat", "Updated sender's chat list")

            val otherUserUpdate = mapOf<String, Any>(
                "id" to currentUserId,
                "name" to currentUserName,
                "lastMessage" to "${currentUserName}: ${message.text}",
                "lastSeen" to System.currentTimeMillis()
            )
            usersRef.child(otherUserId).child("chats").child(currentUserId).updateChildren(otherUserUpdate).await()
            Log.d("FirebaseChat", "Updated receiver's chat list")

            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("FirebaseChat", "Error sending message", e)
            Result.failure(e)
        }
    }

    // Изменено: теперь мы слушаем сообщения только для конкретного chatId
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
                trySend(messages.sortedBy { it.timestamp })
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }

        messagesRef.child(chatId).addValueEventListener(listener)

        awaitClose {
            messagesRef.child(chatId).removeEventListener(listener)
        }
    }

    // Изменено: теперь получаем список *чатов*, а не всех пользователей
    fun getMyChats(): Flow<List<ChatUser>> = callbackFlow {
        val currentUserId = getCurrentUserId()

        if (currentUserId == null) {
            trySend(emptyList())
            close()
            return@callbackFlow
        }

        // Слушаем узел "users/<currentUserId>/chats"
        val userChatsRef = usersRef.child(currentUserId).child("chats")

        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val chats = mutableListOf<ChatUser>()
                for (chatSnapshot in snapshot.children) {
                    // ChatUser используется здесь для хранения инфо о собеседнике в списке чатов
                    val chatUser = chatSnapshot.getValue(ChatUser::class.java)
                    if (chatUser != null) {
                        chats.add(chatUser)
                    }
                }
                // Сортировка по последнему сообщению
                trySend(chats.sortedByDescending { it.lastSeen })
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
            // Обновляем статус в корневом узле пользователя
            usersRef.child(userId).updateChildren(updates).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Новая функция для получения ChatUser по ID
    suspend fun getChatUserById(userId: String): ChatUser? {
        return try {
            usersRef.child(userId).get().await().getValue(ChatUser::class.java)
        } catch (e: Exception) {
            null
        }
    }

    // Обновлено: убрана логика создания тестовых пользователей
    suspend fun initializeCurrentUser(): Result<Unit> {
        return try {
            val userId = getCurrentUserId() ?: return Result.failure(Exception("User not authenticated"))
            val userName = getCurrentUserName() ?: "Користувач"

            // Устанавливаем только основные данные пользователя
            val userData = mapOf<String, Any>(
                "id" to userId,
                "name" to userName,
                "isOnline" to true,
                "lastSeen" to System.currentTimeMillis()
            )

            // Используем updateChildren, чтобы не удалять существующие узлы, такие как "chats"
            usersRef.child(userId).updateChildren(userData).await()
            Log.d("FirebaseChat", "Current user initialized: $userName ($userId)")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("FirebaseChat", "Error initializing user", e)
            Result.failure(e)
        }
    }
}