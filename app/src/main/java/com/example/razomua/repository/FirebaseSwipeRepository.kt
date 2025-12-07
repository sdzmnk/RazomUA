//package com.example.razomua.repository
//
//import android.util.Log
//import com.example.razomua.model.*
//import com.google.firebase.auth.FirebaseAuth
//import com.google.firebase.database.*
//import kotlinx.coroutines.channels.awaitClose
//import kotlinx.coroutines.flow.Flow
//import kotlinx.coroutines.flow.callbackFlow
//import kotlinx.coroutines.tasks.await
//
//class FirebaseSwipeRepository {
//    private val database = FirebaseDatabase.getInstance()
//    private val swipesRef = database.getReference("swipes")
//    private val matchesRef = database.getReference("matches")
//    private val usersRef = database.getReference("users")
//    private val chatsRef = database.getReference("chats")
//    private val auth = FirebaseAuth.getInstance()
//
//    fun getCurrentUserId(): String? = auth.currentUser?.uid
//
//    suspend fun sendSwipe(toUserId: String, action: SwipeAction): Result<Match?> {
//        return try {
//            val fromUserId = getCurrentUserId() ?: return Result.failure(Exception("User not authenticated"))
//
//            val swipeId = "$fromUserId-$toUserId"
//            val swipe = Swipe(
//                id = swipeId,
//                fromUserId = fromUserId,
//                toUserId = toUserId,
//                action = action.name,
//                timestamp = System.currentTimeMillis()
//            )
//
//            // Зберегти свайп
//            swipesRef.child(fromUserId).child(toUserId).setValue(swipe).await()
//            Log.d("FirebaseSwipe", "Swipe saved: $fromUserId -> $toUserId (${action.name})")
//
//            // Перевірити на match (тільки якщо LIKE)
//            if (action == SwipeAction.LIKE) {
//                val match = checkForMatch(fromUserId, toUserId)
//                if (match != null) {
//                    Log.d("FirebaseSwipe", "🎉 MATCH found!")
//                    return Result.success(match)
//                }
//            }
//
//            Result.success(null)
//        } catch (e: Exception) {
//            Log.e("FirebaseSwipe", "Error sending swipe", e)
//            Result.failure(e)
//        }
//    }
//
//    private suspend fun checkForMatch(user1Id: String, user2Id: String): Match? {
//        return try {
//            // Перевірити чи user2 лайкнув user1
//            val reverseSwipe = swipesRef
//                .child(user2Id)
//                .child(user1Id)
//                .get()
//                .await()
//
//            val swipeData = reverseSwipe.getValue(Swipe::class.java)
//
//            if (swipeData != null && swipeData.action == "LIKE") {
//                // Є взаємний лайк! Створити match та чат
//                createMatch(user1Id, user2Id)
//            } else {
//                null
//            }
//        } catch (e: Exception) {
//            Log.e("FirebaseSwipe", "Error checking match", e)
//            null
//        }
//    }
//
//    private suspend fun createMatch(user1Id: String, user2Id: String): Match {
//        // Створити унікальний chatId (меншій ID завжди перший)
//        val sortedIds = listOf(user1Id, user2Id).sorted()
//        val chatId = "${sortedIds[0]}_${sortedIds[1]}"
//
//        val matchId = matchesRef.push().key ?: throw Exception("Failed to generate match ID")
//
//        val match = Match(
//            id = matchId,
//            user1Id = user1Id,
//            user2Id = user2Id,
//            chatId = chatId,
//            timestamp = System.currentTimeMillis(),
//            isNew = true
//        )
//
//        // Зберегти match для обох користувачів
//        matchesRef.child(user1Id).child(matchId).setValue(match).await()
//        matchesRef.child(user2Id).child(matchId).setValue(match).await()
//
//        // Створити чат з привітальним повідомленням
//        createChatForMatch(chatId, user1Id, user2Id)
//
//        Log.d("FirebaseSwipe", "Match created: $matchId, chat: $chatId")
//        return match
//    }
//
//    private suspend fun createChatForMatch(chatId: String, user1Id: String, user2Id: String) {
//        try {
//            // Створити чат запис
//            val chatData = mapOf(
//                "id" to chatId,
//                "user1Id" to user1Id,
//                "user2Id" to user2Id,
//                "createdAt" to System.currentTimeMillis(),
//                "lastMessage" to "Почніть спілкування! 💬"
//            )
//            chatsRef.child(chatId).setValue(chatData).await()
//
//            // Отримати імена користувачів
//            val user1Name = usersRef.child(user1Id).child("name").get().await().getValue(String::class.java) ?: "Користувач"
//            val user2Name = usersRef.child(user2Id).child("name").get().await().getValue(String::class.java) ?: "Користувач"
//
//            // Оновити профілі користувачів з інформацією про новий чат
//            val user1ChatInfo = mapOf(
//                "id" to user2Id,
//                "name" to user2Name,
//                "lastMessage" to "У вас новий match! 🎉",
//                "isOnline" to false,
//                "lastSeen" to System.currentTimeMillis()
//            )
//
//            val user2ChatInfo = mapOf(
//                "id" to user1Id,
//                "name" to user1Name,
//                "lastMessage" to "У вас новий match! 🎉",
//                "isOnline" to false,
//                "lastSeen" to System.currentTimeMillis()
//            )
//
//            // Це додасть нового користувача в список чатів
//            usersRef.child(user2Id).updateChildren(user2ChatInfo).await()
//            usersRef.child(user1Id).updateChildren(user1ChatInfo).await()
//
//            Log.d("FirebaseSwipe", "Chat created for match: $chatId")
//        } catch (e: Exception) {
//            Log.e("FirebaseSwipe", "Error creating chat", e)
//        }
//    }
//
//    fun getUserMatches(): Flow<List<Match>> = callbackFlow {
//        val userId = getCurrentUserId()
//        if (userId == null) {
//            trySend(emptyList())
//            close()
//            return@callbackFlow
//        }
//
//        val listener = object : ValueEventListener {
//            override fun onDataChange(snapshot: DataSnapshot) {
//                val matches = mutableListOf<Match>()
//                for (matchSnapshot in snapshot.children) {
//                    val match = matchSnapshot.getValue(Match::class.java)
//                    match?.let { matches.add(it) }
//                }
//                trySend(matches.sortedByDescending { it.timestamp })
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//                Log.e("FirebaseSwipe", "Error loading matches", error.toException())
//                close(error.toException())
//            }
//        }
//
//        matchesRef.child(userId).addValueEventListener(listener)
//
//        awaitClose {
//            matchesRef.child(userId).removeEventListener(listener)
//        }
//    }
//
//    suspend fun markMatchAsViewed(matchId: String): Result<Unit> {
//        return try {
//            val userId = getCurrentUserId() ?: return Result.failure(Exception("User not authenticated"))
//            matchesRef.child(userId).child(matchId).child("isNew").setValue(false).await()
//            Result.success(Unit)
//        } catch (e: Exception) {
//            Result.failure(e)
//        }
//    }
//
//    suspend fun getUserSwipes(): Result<List<Swipe>> {
//        return try {
//            val userId = getCurrentUserId() ?: return Result.failure(Exception("User not authenticated"))
//            val snapshot = swipesRef.child(userId).get().await()
//
//            val swipes = mutableListOf<Swipe>()
//            for (swipeSnapshot in snapshot.children) {
//                val swipe = swipeSnapshot.getValue(Swipe::class.java)
//                swipe?.let { swipes.add(it) }
//            }
//
//            Result.success(swipes)
//        } catch (e: Exception) {
//            Result.failure(e)
//        }
//    }
//
//    // Отримати користувачів для свайпу (які ще не були свайпнуті)
//    suspend fun getAvailableUsers(): Result<List<ChatUser>> {
//        return try {
//            val currentUserId = getCurrentUserId() ?: return Result.failure(Exception("User not authenticated"))
//
//            // Отримати всіх користувачів
//            val usersSnapshot = usersRef.get().await()
//            val allUsers = mutableListOf<ChatUser>()
//
//            for (userSnapshot in usersSnapshot.children) {
//                val user = userSnapshot.getValue(ChatUser::class.java)
//                if (user != null && user.id != currentUserId) {
//                    allUsers.add(user)
//                }
//            }
//
//            // Отримати вже свайпнутих
//            val swipesSnapshot = swipesRef.child(currentUserId).get().await()
//            val swipedUserIds = swipesSnapshot.children.mapNotNull { it.key }.toSet()
//
//            // Фільтрувати тільки не свайпнутих
//            val availableUsers = allUsers.filter { it.id !in swipedUserIds }
//
//            Log.d("FirebaseSwipe", "Available users: ${availableUsers.size}")
//            Result.success(availableUsers)
//        } catch (e: Exception) {
//            Log.e("FirebaseSwipe", "Error getting available users", e)
//            Result.failure(e)
//        }
//    }
//}

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

            // 1. Сохранить свайп
            swipesRef.child(fromUserId).child(toUserId).child("action").setValue(action.name).await()
            swipesRef.child(fromUserId).child(toUserId).child("timestamp").setValue(System.currentTimeMillis()).await()
            Log.d("FirebaseSwipe", "Swipe saved: $fromUserId -> $toUserId (${action.name})")

            // 2. Проверить на match (только если LIKE)
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
            // Проверить, лайкнул ли user2 user1 (обратный свайп)
            val reverseSwipeSnapshot = swipesRef.child(user2Id).child(user1Id).child("action").get().await()
            val reverseAction = reverseSwipeSnapshot.getValue(String::class.java)

            if (reverseAction == SwipeAction.LIKE.name) {
                // Есть взаимный лайк!
                return createMatchAndChat(user1Id, user2Id)
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e("FirebaseSwipe", "Error checking match", e)
            null
        }
    }

    private suspend fun createMatchAndChat(user1Id: String, user2Id: String): Match {
        // Створити унікальний chatId (менший ID завжди перший)
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

        // 1. Сохранить match для обоих пользователей
        matchesRef.child(user1Id).child(matchId).setValue(match).await()
        matchesRef.child(user2Id).child(matchId).setValue(match).await()

        // 2. Обновить список чатов у обоих пользователей
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

            // ВАЖЛИВО: зберігаємо chatId у кожного користувача
            val user1ChatInfo = mapOf<String, Any>(
                "id" to user2Id,
                "name" to user2Name,
                "lastMessage" to welcomeMessage,
                "isOnline" to false,
                "lastSeen" to currentTime,
                "chatId" to chatId  // ДОДАТИ
            )
            usersRef.child(user1Id).child("chats").child(user2Id).updateChildren(user1ChatInfo).await()

            val user2ChatInfo = mapOf<String, Any>(
                "id" to user1Id,
                "name" to user1Name,
                "lastMessage" to welcomeMessage,
                "isOnline" to false,
                "lastSeen" to currentTime,
                "chatId" to chatId  // ДОДАТИ
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
        // Предполагаем, что вам нужен этот класс, хотя модель Swipe не была предоставлена.
        // Возвращаем пустой список для компиляции, если модель не найдена.
        return Result.success(emptyList())
    }

    // Отримати користувачів для свайпу (які ще не були свайпнуті)
    suspend fun getAvailableUsers(): Result<List<ChatUser>> {
        return try {
            val currentUserId = getCurrentUserId() ?: return Result.failure(Exception("User not authenticated"))

            // 1. Отримати всіх користувачів
            val usersSnapshot = usersRef.get().await()
            val allUsers = mutableListOf<ChatUser>()

            for (userSnapshot in usersSnapshot.children) {
                val user = userSnapshot.getValue(ChatUser::class.java)
                if (user != null && user.id != currentUserId) {
                    allUsers.add(user)
                }
            }

            // 2. Отримати вже свайпнутих
            val swipesSnapshot = swipesRef.child(currentUserId).get().await()
            val swipedUserIds = swipesSnapshot.children.mapNotNull { it.key }.toSet()

            // 3. Фільтрувати тільки не свайпнутих
            val availableUsers = allUsers.filter { it.id !in swipedUserIds }

            Log.d("FirebaseSwipe", "Available users: ${availableUsers.size}")
            Result.success(availableUsers)
        } catch (e: Exception) {
            Log.e("FirebaseSwipe", "Error getting available users", e)
            Result.failure(e)
        }
    }
}