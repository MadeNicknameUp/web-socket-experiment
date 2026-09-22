package com.example.planning_poker_room.infrastructure.repository

import com.example.planning_poker_room.exception.unit.ConnectionNotFoundException
import com.example.planning_poker_room.store.model.Connection
import com.example.planning_poker_room.store.repository.ConnectionRepository
import org.springframework.stereotype.Repository
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

@Repository
class InMemoryConnectionRepository : ConnectionRepository {

    private val connections: MutableMap<UUID, Connection> = ConcurrentHashMap()

    override fun findByParticipantId(participantId: UUID): Connection =
        connections[participantId]
            ?: throw ConnectionNotFoundException("Connection for $participantId does not exist.")


    override fun save(connection: Connection): Connection =
        connection.apply {
            connections[this.participantId] = this
        }

}