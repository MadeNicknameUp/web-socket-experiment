package com.example.planning_poker_room.store.repository

import com.example.planning_poker_room.store.model.Connection
import java.util.UUID

interface ConnectionRepository {

    fun findBySessionId(sessionId: String): Connection

    fun findByParticipantId(participantId: UUID): Connection

    fun findByRoomId(roomId: UUID): List<Connection>

    fun save(connection: Connection): Connection

}