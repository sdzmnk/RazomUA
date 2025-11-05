package com.example.razomua.viewmodel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import okhttp3.*

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

data class ChatUser(
    val name: String,
    val message: String,
    val image: Int,
    val isOnline: Boolean
)
class ChatViewModel : ViewModel() {
    private val _users = MutableStateFlow(
        listOf(
            ChatUser("Діма", "У тебе нова симпатія. Почни розмову!", com.example.razomua.R.drawable.pic_for_chat1, true),
            ChatUser("Славік", "У тебе нова симпатія. Почни розмову!", com.example.razomua.R.drawable.pic_for_chat2, false)
        )
    )
    val users = _users.asStateFlow()

    init {
        // Імітуємо зміну статусу "онлайн / офлайн"
        viewModelScope.launch {
            while (true) {
                delay(4000)
                _users.value = _users.value.map { user ->
                    user.copy(isOnline = !user.isOnline)
                }
            }
        }
    }
    private val client = OkHttpClient()
    private var webSocket: WebSocket? = null

    fun connectToChatServer() {
        val request = Request.Builder()
            .url("wss://ws.ifelse.io")
            .build()

        webSocket = client.newWebSocket(request, object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                println("✅ Підключено до WebSocket")
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                println("📩 Нове повідомлення: $text")
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                println("❌ Помилка WebSocket: ${t.message}")
            }
        })
    }

    fun sendMessage(message: String) {
        webSocket?.send(message)
    }

    override fun onCleared() {
        super.onCleared()
        webSocket?.close(1000, "Screen closed")
    }
}