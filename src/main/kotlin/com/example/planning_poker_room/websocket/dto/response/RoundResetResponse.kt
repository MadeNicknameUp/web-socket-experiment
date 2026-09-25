package com.example.planning_poker_room.websocket.dto.response

import com.example.planning_poker_room.websocket.dto.WebSocketMessage
import com.example.planning_poker_room.websocket.dto.WebSocketMessageType

class RoundResetResponse(
    override val type: WebSocketMessageType = WebSocketMessageType.ROUND_RESET,
) : WebSocketMessage