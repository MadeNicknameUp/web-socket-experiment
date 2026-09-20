package com.example.planning_poker_room.websocket

import org.springframework.web.socket.CloseStatus
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.handler.TextWebSocketHandler
import java.util.concurrent.ConcurrentHashMap

class CustomWebSocketHandler : TextWebSocketHandler() {

    private val sessions: MutableMap<String, WebSocketSession> = ConcurrentHashMap()

    override fun afterConnectionEstablished(session: WebSocketSession) {
        // persist new session
        sessions[session.id] = session
    }

    override fun handleTextMessage(session: WebSocketSession, message: TextMessage) {
        // broadcast received message
        sessions.values
            .filter { session.id != it.id }
            .forEach { it.sendMessage(TextMessage(message.payload))}
    }

    override fun afterConnectionClosed(session: WebSocketSession, status: CloseStatus) {
        // remove session
        sessions.remove(session.id)
        // notify everyone
        sessions.values.forEach { it.sendMessage(TextMessage("${session.id} left.")) }
    }
}