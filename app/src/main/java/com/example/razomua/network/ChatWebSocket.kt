package com.example.razomua.network

import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okhttp3.Response

class ChatWebSocket(private val url: String) {

    private lateinit var webSocket: WebSocket
    private val client = OkHttpClient()

    fun connect() {
        val request = Request.Builder()
            .url(url) // наприклад: "wss://yourserver.com/chat"
            .build()

        val listener = object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                println("Підключено до сервера")
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                println("Нове повідомлення: $text")
            }

            override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
                println("З’єднання закривається: $reason")
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                println("Помилка: ${t.message}")
            }
        }

        webSocket = client.newWebSocket(request, listener)
    }

    fun sendMessage(message: String) {
        webSocket.send(message)
    }

    fun disconnect() {
        webSocket.close(1000, "User left chat")
    }
}
