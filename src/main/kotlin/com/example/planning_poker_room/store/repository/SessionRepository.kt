package com.example.planning_poker_room.store.repository

import org.springframework.web.socket.WebSocketSession

interface SessionRepository {

    fun findAll(): List<WebSocketSession>

    fun findById(sessionId: String): WebSocketSession

    fun remove(session: WebSocketSession)

    fun save(session: WebSocketSession): WebSocketSession

}