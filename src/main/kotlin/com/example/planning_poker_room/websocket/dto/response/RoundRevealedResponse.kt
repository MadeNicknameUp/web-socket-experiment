package com.example.planning_poker_room.websocket.dto.response

import com.example.planning_poker_room.websocket.dto.WebSocketMessage
import com.example.planning_poker_room.websocket.dto.WebSocketMessageType
import java.util.UUID
import java.util.concurrent.atomic.AtomicLong

data class RoundRevealedResponse(
    override val type: WebSocketMessageType = WebSocketMessageType.ROUND_REVEALED,
    override val version: AtomicLong,
    val votes: Map<UUID, Int?>
) : WebSocketMessage, RoomStateChangingEvent
