package com.example.planning_poker_room.store.repository

import com.example.planning_poker_room.store.model.Room
import java.util.UUID

interface RoomRepository {

    fun findById(id: UUID): Room?

    fun save(room: Room): Room
}