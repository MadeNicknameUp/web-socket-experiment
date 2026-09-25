package com.example.planning_poker_room.websocket.dto.response.exception

import com.example.planning_poker_room.websocket.dto.WebSocketMessage
import com.example.planning_poker_room.websocket.dto.WebSocketMessageType

data class ExceptionResponse(
    override val type: WebSocketMessageType,
    val message: String,
    val code: Int
) : WebSocketMessage
