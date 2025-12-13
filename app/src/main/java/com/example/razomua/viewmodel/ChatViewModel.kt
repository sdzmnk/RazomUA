package com.example.razomua.viewmodel


import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.razomua.model.ChatUser
import com.example.razomua.model.Message
import com.example.razomua.repository.FirebaseChatRepository
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class ChatViewModel(
    private val repository: FirebaseChatRepository = FirebaseChatRepository()
) : ViewModel() {

    private val _users = MutableStateFlow<List<ChatUser>>(emptyList())
    val users: StateFlow<List<ChatUser>> = _users.asStateFlow()

    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages: StateFlow<List<Message>> = _messages.asStateFlow()

    private val _isConnected = MutableStateFlow(false)
    val isConnected: StateFlow<Boolean> = _isConnected.asStateFlow()

    private val _currentChatId = MutableStateFlow<String?>(null)

    init {
        initializeUser()
        setupPresence()
        loadUsers()
    }

    private fun setupPresence() {
        repository.setupPresence().onSuccess {
            Log.d("ChatViewModel", "Presence system initialized")
        }.onFailure { error ->
            Log.e("ChatViewModel", "Failed to setup presence", error)
        }
    }

    private fun initializeUser() {
        viewModelScope.launch {
            val userId = repository.getCurrentUserId()
            if (userId != null) {
                try {
                    val userSnapshot = FirebaseDatabase.getInstance()
                        .getReference("users")
                        .child(userId)
                        .get()
                        .await()

                    val photoUrl = userSnapshot.child("photoUrl").getValue(String::class.java) ?: ""

                    repository.initializeCurrentUser(photoUrl).onSuccess {
                        _isConnected.value = true
                    }.onFailure {
                        Log.e("ChatViewModel", "Failed to initialize user", it)
                    }
                } catch (e: Exception) {
                    Log.e("ChatViewModel", "Error getting photoUrl", e)
                    repository.initializeCurrentUser("").onSuccess {
                        _isConnected.value = true
                    }
                }
            }
        }
    }


    private fun loadUsers() {
        viewModelScope.launch {
            repository.getMyChats().collect { chatList ->
                _users.value = chatList
            }
        }
    }

    fun connectToChat(chatId: String) {
        _currentChatId.value = chatId
        viewModelScope.launch {
            repository.getMessages(chatId).collect { messageList ->
                _messages.value = messageList
                messageList.forEach { message ->
                    Log.d(
                        "ChatMessages",
                        "Message from ${message.senderName} (${message.senderId}): ${message.text} at ${message.timestamp}"
                    )
                }
            }
        }
    }

    fun sendMessage(text: String) {
        val chatId = _currentChatId.value ?: return
        if (text.isBlank()) return

        viewModelScope.launch {
            val userId = repository.getCurrentUserId() ?: return@launch
            val userName = repository.getCurrentUserName() ?: "Ви"

            val message = Message(
                text = text,
                senderId = userId,
                senderName = userName,
                timestamp = System.currentTimeMillis(),
                isCurrentUser = true
            )

            repository.sendMessage(chatId, message)
        }
    }


    fun getCurrentUserId(): String? {
        return repository.getCurrentUserId()
    }

    fun disconnect() {
        viewModelScope.launch {
            repository.updateUserStatus(false)
        }
    }

    fun updateUserStatus(isOnline: Boolean) {
        viewModelScope.launch {
            repository.updateUserStatus(isOnline)
        }
    }

    override fun onCleared() {
        disconnect()
        super.onCleared()
    }
}