package com.example.planning_poker_room.store.repository

import com.example.planning_poker_room.store.model.Room
import com.example.planning_poker_room.store.model.RoomName
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
class InMemoryRoomRepository : RoomRepository {

    private val rooms = mutableMapOf<UUID, Room>()

    override fun findById(id: UUID): Room? = rooms[id]


    override fun findByName(name: RoomName): Room?
        = rooms.values.find { it.name == name}


    override fun save(room: Room): Room =
        room.also { rooms[room.id] = room }

}