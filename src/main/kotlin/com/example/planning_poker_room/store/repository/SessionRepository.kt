package com.example.planning_poker_room.store.repository

import org.springframework.web.socket.WebSocketSession

interface SessionRepository {

    fun findAllExceptForById(sessionId: String): List<WebSocketSession>

    fun findAll(): List<WebSocketSession>

    fun remove(session: WebSocketSession)

    fun save(session: WebSocketSession): WebSocketSession

}