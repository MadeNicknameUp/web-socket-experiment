package com.example.planning_poker_room.store.model

import java.util.UUID

class Participant private constructor(
    val id: UUID,
    val name: ParticipantName,
    val connected: Boolean,
    val currentVote: String?
) {

    companion object {
        fun create(name: ParticipantName): Participant {

            return Participant(
                id = UUID.randomUUID(),
                name = name,
                connected = false,
                currentVote = null
            )
        }
    }
}