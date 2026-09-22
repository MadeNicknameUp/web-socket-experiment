package com.example.planning_poker_room.store.model

import java.util.UUID

class Room private constructor(
    val id: UUID,
    val name: RoomName,
//    val hostParticipantId: UUID,
    val participants: MutableList<Participant>,
    var phase: RoomState,
    var round: Round?
) {

    companion object {
        fun create(name: RoomName): Room {

            return Room(
                id = UUID.randomUUID(),
                name = name,
                phase = RoomState.VOTING,
                participants = mutableListOf(),
                round = null
            )
        }
    }
}