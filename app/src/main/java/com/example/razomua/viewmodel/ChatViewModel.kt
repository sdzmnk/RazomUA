package com.example.razomua.viewmodel
////import androidx.lifecycle.ViewModel
////import androidx.lifecycle.viewModelScope
////import kotlinx.coroutines.launch
////import okhttp3.*
////
////import kotlinx.coroutines.delay
////import kotlinx.coroutines.flow.MutableStateFlow
////import kotlinx.coroutines.flow.asStateFlow
////
////data class ChatUser(
////    val name: String,
////    val message: String,
////    val image: Int,
////    val isOnline: Boolean
////)
////class ChatViewModel : ViewModel() {
////    private val _users = MutableStateFlow(
////        listOf(
////            ChatUser("Діма", "У тебе нова симпатія. Почни розмову!", com.example.razomua.R.drawable.pic_for_chat1, true),
////            ChatUser("Славік", "У тебе нова симпатія. Почни розмову!", com.example.razomua.R.drawable.pic_for_chat2, false)
////        )
////    )
////    val users = _users.asStateFlow()
////
////    init {
////        viewModelScope.launch {
////            while (true) {
////                delay(4000)
////                _users.value = _users.value.map { user ->
////                    user.copy(isOnline = !user.isOnline)
////                }
////            }
////        }
////    }
////    private val client = OkHttpClient()
////    private var webSocket: WebSocket? = null
////
////    fun connectToChatServer() {
////        val request = Request.Builder()
////            .url("wss://ws.ifelse.io")
////            .build()
////
////        webSocket = client.newWebSocket(request, object : WebSocketListener() {
////            override fun onOpen(webSocket: WebSocket, response: Response) {
////                println("Підключено до WebSocket")
////            }
////
////            override fun onMessage(webSocket: WebSocket, text: String) {
////                println("Нове повідомлення: $text")
////            }
////
////            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
////                println("Помилка WebSocket: ${t.message}")
////            }
////        })
////    }
////
////    fun sendMessage(message: String) {
////        webSocket?.send(message)
////    }
////
////    override fun onCleared() {
////        super.onCleared()
////        webSocket?.close(1000, "Screen closed")
////    }
////}
//
//
//package com.example.razomua.viewmodel
//
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.viewModelScope
//import com.example.razomua.model.ChatUser
//import com.example.razomua.model.Message
//import com.example.razomua.repository.FirebaseChatRepository
//import kotlinx.coroutines.flow.MutableStateFlow
//import kotlinx.coroutines.flow.StateFlow
//import kotlinx.coroutines.flow.asStateFlow
//import kotlinx.coroutines.launch
//
//class ChatViewModel(
//    private val repository: FirebaseChatRepository = FirebaseChatRepository()
//) : ViewModel() {
//
//    private val _users = MutableStateFlow<List<ChatUser>>(emptyList())
//    val users: StateFlow<List<ChatUser>> = _users.asStateFlow()
//
//    private val _messages = MutableStateFlow<List<Message>>(emptyList())
//    val messages: StateFlow<List<Message>> = _messages.asStateFlow()
//
//    private val _isConnected = MutableStateFlow(false)
//    val isConnected: StateFlow<Boolean> = _isConnected.asStateFlow()
//
//    private val _currentChatId = MutableStateFlow<String?>(null)
//
//    init {
//        initializeUser()
//        loadUsers()
//    }
//
//    private fun initializeUser() {
//        viewModelScope.launch {
//            repository.initializeCurrentUser()
//            repository.createTestUsers() // Створити тестових користувачів
//            _isConnected.value = true
//        }
//    }
//
//    private fun loadUsers() {
//        viewModelScope.launch {
//            repository.getAllUsers().collect { userList ->
//                _users.value = userList
//            }
//        }
//    }
//
//    fun connectToChat(chatId: String) {
//        _currentChatId.value = chatId
//        viewModelScope.launch {
//            repository.getMessages(chatId).collect { messageList ->
//                _messages.value = messageList
//            }
//        }
//    }
//
//    fun sendMessage(text: String) {
//        val chatId = _currentChatId.value ?: return
//        if (text.isBlank()) return
//
//        viewModelScope.launch {
//            val userId = repository.getCurrentUserId() ?: return@launch
//            val userName = repository.getCurrentUserName() ?: "Ви"
//
//            val message = Message(
//                text = text,
//                senderId = userId,
//                senderName = userName,
//                timestamp = System.currentTimeMillis(),
//                isCurrentUser = true
//            )
//
//            repository.sendMessage(chatId, message)
//        }
//    }
//
//    fun disconnect() {
//        viewModelScope.launch {
//            repository.updateUserStatus(false)
//        }
//    }
//
//    fun updateUserStatus(isOnline: Boolean) {
//        viewModelScope.launch {
//            repository.updateUserStatus(isOnline)
//        }
//    }
//
//    override fun onCleared() {
//        disconnect()
//        super.onCleared()
//    }
//}

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.razomua.model.ChatUser
import com.example.razomua.model.Message
import com.example.razomua.repository.FirebaseChatRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ChatViewModel(
    private val repository: FirebaseChatRepository = FirebaseChatRepository()
) : ViewModel() {

    // Теперь содержит список собеседников (чатов), а не всех пользователей
    private val _users = MutableStateFlow<List<ChatUser>>(emptyList())
    val users: StateFlow<List<ChatUser>> = _users.asStateFlow()

    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages: StateFlow<List<Message>> = _messages.asStateFlow()

    private val _isConnected = MutableStateFlow(false)
    val isConnected: StateFlow<Boolean> = _isConnected.asStateFlow()

    private val _currentChatId = MutableStateFlow<String?>(null)

    init {
        initializeUser()
        loadUsers()
    }

    private fun initializeUser() {
        viewModelScope.launch {
            repository.initializeCurrentUser().onSuccess {
                _isConnected.value = true
            }.onFailure {
                // Обработка ошибки инициализации
            }
        }
    }

    // Загружаем чаты текущего пользователя
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
                isCurrentUser = true // Это поле будет пересчитано в getMessages
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