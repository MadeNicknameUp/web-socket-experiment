package com.example.planning_poker_room.store.repository

import com.example.planning_poker_room.store.model.Connection
import java.util.UUID

interface ConnectionRepository {

    fun findByParticipantId(participantId: UUID): Connection

    fun save(connection: Connection): Connection

}