package com.example.planning_poker_room.api.dto

import com.example.planning_poker_room.store.model.Participant
import java.util.UUID

data class JoinRoomResponse(
    val participantId: UUID,
    val name: String
) {
    companion object {
        fun of(participant: Participant) =
            JoinRoomResponse(
                participant.id,
                participant.name.value
            )
    }
}
