package com.example.planning_poker_room.infrastructure.repository

import com.example.planning_poker_room.store.repository.SessionRepository
import org.springframework.stereotype.Repository
import org.springframework.web.socket.WebSocketSession
import java.util.concurrent.ConcurrentHashMap

@Repository
class InMemorySessionRepository : SessionRepository {

    private val sessions: MutableMap<String, WebSocketSession> = ConcurrentHashMap()

    override fun findAllExceptForById(sessionId: String): List<WebSocketSession> =
        sessions.filterNot { it.key == sessionId } .values.toList()


    override fun findAll(): List<WebSocketSession> =
        sessions.values.toList()

    override fun remove(session: WebSocketSession) {
        sessions.remove(session.id)
    }

    override fun save(session: WebSocketSession): WebSocketSession =
        session.apply {
            sessions[id] = this
        }

}