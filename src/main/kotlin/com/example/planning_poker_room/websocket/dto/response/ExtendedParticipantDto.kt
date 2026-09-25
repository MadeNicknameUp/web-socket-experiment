package com.example.planning_poker_room.websocket.dto.response

import com.example.planning_poker_room.store.model.Participant
import java.util.UUID

data class ExtendedParticipantDto (
    val id: UUID,
    val name: String,
    val connected: Boolean,
    val hasVoted: Boolean
) {
    companion object {
        fun of(participant: Participant): ExtendedParticipantDto =
            ExtendedParticipantDto(
                id = participant.id,
                name = participant.name.value,
                connected = participant.connected,
                hasVoted = participant.vote != null
            )
    }
}