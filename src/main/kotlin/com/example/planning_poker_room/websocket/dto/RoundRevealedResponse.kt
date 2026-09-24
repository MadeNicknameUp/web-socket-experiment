package com.example.planning_poker_room.websocket.dto

import java.util.UUID

data class RoundRevealedResponse(
    override val type: WebSocketMessageType = WebSocketMessageType.ROUND_REVEALED,
    val votes: Map<UUID, Int?>
) : WebSocketMessage
