package com.example.planning_poker_room.api.dto

import com.example.planning_poker_room.store.model.Room
import java.util.UUID

data class CreateRoomResponse(
    val roomId: UUID,
    val hostId: UUID
) {
    companion object {
        fun of(room: Room) = CreateRoomResponse(
            room.id,
            room.hostParticipantId
        )
    }
}