package com.example.planning_poker_room.infrastructure.repository

import com.example.planning_poker_room.store.model.Room
import com.example.planning_poker_room.store.repository.RoomRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
class InMemoryRoomRepository : RoomRepository {

    private val rooms = mutableMapOf<UUID, Room>()

    override fun findById(id: UUID): Room? = rooms[id]

    override fun save(room: Room): Room =
        room.also { rooms[room.id] = room }

}