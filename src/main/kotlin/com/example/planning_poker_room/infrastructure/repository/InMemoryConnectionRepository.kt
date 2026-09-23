package com.example.planning_poker_room.infrastructure.repository

import com.example.planning_poker_room.exception.unit.ConnectionNotFoundException
import com.example.planning_poker_room.store.model.Connection
import com.example.planning_poker_room.store.repository.ConnectionRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
class InMemoryConnectionRepository : ConnectionRepository {

    private val connections: MutableList<Connection> = ArrayList()

    override fun findBySessionId(sessionId: String): Connection =
        connections .find { it.sessionId.equals(sessionId) }
            ?: throw ConnectionNotFoundException("Connection for session_id: $sessionId does not exist.")

    override fun findByParticipantId(participantId: UUID): Connection =
        connections.find { it.participantId.equals(participantId) }
            ?: throw ConnectionNotFoundException("Connection for participant_id: $participantId does not exist.")

    override fun findByRoomId(roomId: UUID): List<Connection>
        = connections.filter { it.roomId == roomId }


    override fun save(connection: Connection): Connection =
        connection.apply {
            connections.add(this)
        }

}