package com.example.planning_poker_room.store.repository

import com.example.planning_poker_room.store.model.Room
import com.example.planning_poker_room.store.model.RoomName
import java.util.UUID

interface RoomRepository {

    fun findById(id: UUID): Room?

    fun findByName(name: RoomName): Room?

    fun save(room: Room): Room
}