package com.example.planning_poker_room.websocket.dto.response

import java.util.UUID

data class ParticipantDto (
    val id: UUID,
    val name: String
)