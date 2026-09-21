package com.example.planning_poker_room.websocket

import org.springframework.stereotype.Component
import org.springframework.web.socket.CloseStatus
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.handler.TextWebSocketHandler
import java.util.concurrent.ConcurrentHashMap

@Component
class CustomWebSocketHandler : TextWebSocketHandler() {

    private val sessions: MutableMap<String, WebSocketSession> = ConcurrentHashMap()

    override fun afterConnectionEstablished(session: WebSocketSession) {
        sessions[session.id] = session

        println("CONNECTED ${session.id}")
        println("Active sessions: ${sessions.size}")

        sessions.values
            .filter { it.id != session.id }
            .forEach {
                it.sendMessage(TextMessage("${session.id} joined."))
            }
    }

    override fun handleTextMessage(
        session: WebSocketSession,
        message: TextMessage
    ) {
        println("${session.id} -> ${message.payload}")

        sessions.values
            .filter { it.id != session.id }
            .forEach {
                it.sendMessage(TextMessage(message.payload))
            }
    }

    override fun afterConnectionClosed(
        session: WebSocketSession,
        status: CloseStatus
    ) {
        sessions.remove(session.id)

        println("DISCONNECTED ${session.id}: $status")

        sessions.values.forEach {
            it.sendMessage(TextMessage("${session.id} left."))
        }
    }
}