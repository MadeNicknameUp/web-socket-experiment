package com.example.planning_poker_room.websocket.dto.response

import com.example.planning_poker_room.websocket.dto.WebSocketMessage
import com.example.planning_poker_room.websocket.dto.WebSocketMessageType
import java.util.concurrent.atomic.AtomicLong

class RoundResetResponse(
    override val type: WebSocketMessageType = WebSocketMessageType.ROUND_RESET,
    override val version: AtomicLong,
) : WebSocketMessage, RoomStateChangingEvent