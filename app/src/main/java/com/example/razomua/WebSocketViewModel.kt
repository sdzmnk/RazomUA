//package com.example.websocketchatapp
//
//import androidx.compose.runtime.mutableStateListOf
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.viewModelScope
//import com.example.razomua.model.Message
//import kotlinx.coroutines.flow.MutableStateFlow
//import kotlinx.coroutines.flow.StateFlow
//import kotlinx.coroutines.launch
//import okhttp3.OkHttpClient
//import okhttp3.Request
//import okhttp3.Response
//import okhttp3.WebSocket
//import okhttp3.WebSocketListener
//
//class WebSocketViewModel : ViewModel() {
//    private val client = OkHttpClient()
//    private var webSocket: WebSocket? = null
//    private val _messages = mutableStateListOf<Message>()
//    val messages: List<Message> get() = _messages
//    private val _isConnected = MutableStateFlow(false)
//    val isConnected: StateFlow<Boolean> = _isConnected
//
//    fun connect() {
//        val request = Request.Builder()
//            .url("wss://ws.ifelse.io")
//            .build()
//        webSocket = client.newWebSocket(request, object : WebSocketListener() {
//            override fun onOpen(webSocket: WebSocket, response: Response) {
//                _isConnected.value = true
//            }
//
//            override fun onMessage(webSocket: WebSocket, text: String) {
//                viewModelScope.launch {
//                    if (!text.contains("served by") &&
//                        !text.contains("connected", ignoreCase = true) &&
//                        !text.contains("ready", ignoreCase = true)
//                    ) {
//                        _messages.add(Message(text, false))
//                    }
//                }
//            }
//
//
//            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
//                viewModelScope.launch {
//                    _isConnected.value = false
//                }
//            }
//
//            override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
//                _isConnected.value = false
//            }
//        })
//    }
//
//    fun sendMessage(text: String) {
//        if (_isConnected.value) {
//            webSocket?.send(text)
//            _messages.add(Message(text, true))
//        } else {
//            _messages.add(Message("Not connected", false))
//        }
//    }
//
//    fun disconnect() {
//        webSocket?.close(1000, "User disconnected")
//        client.dispatcher.executorService.shutdown()
//    }
//
//    override fun onCleared() {
//        disconnect()
//        super.onCleared()
//    }
//}