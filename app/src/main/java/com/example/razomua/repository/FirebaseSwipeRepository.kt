package com.example.razomua.repository

import android.util.Log
import com.example.razomua.model.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class FirebaseSwipeRepository {
    private val database = FirebaseDatabase.getInstance()
    private val swipesRef = database.getReference("swipes")
    private val matchesRef = database.getReference("matches")
    private val usersRef = database.getReference("users")
    private val auth = FirebaseAuth.getInstance()

    fun getCurrentUserId(): String? = auth.currentUser?.uid

    private suspend fun getUserName(userId: String): String {
        return try {
            usersRef.child(userId).child("name").get().await().getValue(String::class.java) ?: "Користувач"
        } catch (e: Exception) {
            "Користувач"
        }
    }

    suspend fun sendSwipe(toUserId: String, action: SwipeAction): Result<Match?> {
        return try {
            val fromUserId = getCurrentUserId() ?: return Result.failure(Exception("User not authenticated"))


            swipesRef.child(fromUserId).child(toUserId).child("action").setValue(action.name).await()
            swipesRef.child(fromUserId).child(toUserId).child("timestamp").setValue(System.currentTimeMillis()).await()
            Log.d("FirebaseSwipe", "Swipe saved: $fromUserId -> $toUserId (${action.name})")


            if (action == SwipeAction.LIKE) {
                val match = checkForMatch(fromUserId, toUserId)
                if (match != null) {
                    Log.d("FirebaseSwipe", "🎉 MATCH found!")
                    return Result.success(match)
                }
            }

            Result.success(null)
        } catch (e: Exception) {
            Log.e("FirebaseSwipe", "Error sending swipe", e)
            Result.failure(e)
        }
    }

    private suspend fun checkForMatch(user1Id: String, user2Id: String): Match? {
        return try {

            val reverseSwipeSnapshot = swipesRef.child(user2Id).child(user1Id).child("action").get().await()
            val reverseAction = reverseSwipeSnapshot.getValue(String::class.java)

            if (reverseAction == SwipeAction.LIKE.name) {

                return createMatchAndChat(user1Id, user2Id)
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e("FirebaseSwipe", "Error checking match", e)
            null
        }
    }
    fun getLikesReceivedCountFlow(): Flow<Int> = callbackFlow {
        val currentUserId = getCurrentUserId()
        if (currentUserId == null) {
            trySend(0)
            close()
            return@callbackFlow
        }

        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var count = 0

                for (fromUserSnapshot in snapshot.children) {
                    val toUserSnapshot = fromUserSnapshot.child(currentUserId)

                    val action = toUserSnapshot.child("action").getValue(String::class.java)

                    if (action == "LIKE") {
                        count++
                    }
                }

                trySend(count)
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }

        swipesRef.addValueEventListener(listener)

        awaitClose {
            swipesRef.removeEventListener(listener)
        }
    }

    private suspend fun createMatchAndChat(user1Id: String, user2Id: String): Match {
        val sortedIds = listOf(user1Id, user2Id).sorted()
        val chatId = "${sortedIds[0]}_${sortedIds[1]}"

        val matchId = matchesRef.push().key ?: throw Exception("Failed to generate match ID")

        val match = Match(
            id = matchId,
            user1Id = user1Id,
            user2Id = user2Id,
            chatId = chatId,
            timestamp = System.currentTimeMillis(),
            isNew = true
        )

        matchesRef.child(user1Id).child(matchId).setValue(match).await()
        matchesRef.child(user2Id).child(matchId).setValue(match).await()


        updateUserChatLists(user1Id, user2Id, chatId)

        Log.d("FirebaseSwipe", "Match created: $matchId, chat: $chatId")
        return match
    }

    private suspend fun updateUserChatLists(user1Id: String, user2Id: String, chatId: String) {
        try {
            val user1Name = getUserName(user1Id)
            val user2Name = getUserName(user2Id)

            val currentTime = System.currentTimeMillis()
            val welcomeMessage = "У вас новий match! 🎉"


            val user1ChatInfo = mapOf<String, Any>(
                "id" to user2Id,
                "name" to user2Name,
                "lastMessage" to welcomeMessage,
                "isOnline" to false,
                "lastSeen" to currentTime,
                "chatId" to chatId
            )
            usersRef.child(user1Id).child("chats").child(user2Id).updateChildren(user1ChatInfo).await()

            val user2ChatInfo = mapOf<String, Any>(
                "id" to user1Id,
                "name" to user1Name,
                "lastMessage" to welcomeMessage,
                "isOnline" to false,
                "lastSeen" to currentTime,
                "chatId" to chatId
            )
            usersRef.child(user2Id).child("chats").child(user1Id).updateChildren(user2ChatInfo).await()

            Log.d("FirebaseSwipe", "User chat lists updated with chatId: $chatId")
        } catch (e: Exception) {
            Log.e("FirebaseSwipe", "Error updating chat lists", e)
        }
    }


    fun getUserMatches(): Flow<List<Match>> = callbackFlow {
        val userId = getCurrentUserId()
        if (userId == null) {
            trySend(emptyList())
            close()
            return@callbackFlow
        }

        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val matches = mutableListOf<Match>()
                for (matchSnapshot in snapshot.children) {
                    val match = matchSnapshot.getValue(Match::class.java)
                    match?.let { matches.add(it) }
                }
                trySend(matches.sortedByDescending { it.timestamp })
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("FirebaseSwipe", "Error loading matches", error.toException())
                close(error.toException())
            }
        }

        matchesRef.child(userId).addValueEventListener(listener)

        awaitClose {
            matchesRef.child(userId).removeEventListener(listener)
        }
    }
    fun getUserMatchesCountFlow(): Flow<Int> = callbackFlow {
        val userId = getCurrentUserId()
        if (userId == null) {
            trySend(0)
            close()
            return@callbackFlow
        }

        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val count = snapshot.children.count()
                trySend(count)
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }

        matchesRef.child(userId).addValueEventListener(listener)

        awaitClose {
            matchesRef.child(userId).removeEventListener(listener)
        }
    }

    suspend fun markMatchAsViewed(matchId: String): Result<Unit> {
        return try {
            val userId = getCurrentUserId() ?: return Result.failure(Exception("User not authenticated"))
            matchesRef.child(userId).child(matchId).child("isNew").setValue(false).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getUserSwipes(): Result<List<Swipe>> {

        return Result.success(emptyList())
    }


    suspend fun getAvailableUsers(): Result<List<ChatUser>> {
        return try {
            val currentUserId = getCurrentUserId() ?: return Result.failure(Exception("User not authenticated"))


            val usersSnapshot = usersRef.get().await()
            val allUsers = mutableListOf<ChatUser>()

            for (userSnapshot in usersSnapshot.children) {
                val user = userSnapshot.getValue(ChatUser::class.java)
                if (user != null && user.id != currentUserId) {
                    allUsers.add(user)
                }
            }


            val swipesSnapshot = swipesRef.child(currentUserId).get().await()
            val swipedUserIds = swipesSnapshot.children.mapNotNull { it.key }.toSet()


            val availableUsers = allUsers.filter { it.id !in swipedUserIds }

            Log.d("FirebaseSwipe", "Available users: ${availableUsers.size}")
            Result.success(availableUsers)
        } catch (e: Exception) {
            Log.e("FirebaseSwipe", "Error getting available users", e)
            Result.failure(e)
        }
    }
}