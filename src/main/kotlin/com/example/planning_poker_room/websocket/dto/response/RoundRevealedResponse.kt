package com.example.planning_poker_room.websocket.dto.response

import com.example.planning_poker_room.websocket.dto.WebSocketMessage
import com.example.planning_poker_room.websocket.dto.WebSocketMessageType
import java.util.UUID

data class RoundRevealedResponse(
    override val type: WebSocketMessageType = WebSocketMessageType.ROUND_REVEALED,
    val votes: Map<UUID, Int?>
) : WebSocketMessage
