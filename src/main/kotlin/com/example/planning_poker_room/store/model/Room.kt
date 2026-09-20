package com.example.planning_poker_room.store.model

import java.util.UUID

class Room private constructor(
    val id: UUID,
    val name: RoomName,
    val hostParticipantId: UUID,
    val participants: List<UUID>,
    var phase: RoomPhase,
    var round: Round?
) {

    companion object {
        fun create(name: RoomName, hostId: UUID): Room {

            return Room(
                id = UUID.randomUUID(),
                name = name,
                hostParticipantId = hostId,
                phase = RoomPhase.VOTING,
                participants = emptyList(),
                round = null
            )
        }
    }
}