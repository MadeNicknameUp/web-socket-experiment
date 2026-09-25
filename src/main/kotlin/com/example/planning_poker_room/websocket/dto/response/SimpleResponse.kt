package com.example.planning_poker_room.websocket.dto.response

import com.example.planning_poker_room.websocket.dto.WebSocketMessage
import com.example.planning_poker_room.websocket.dto.WebSocketMessageType

data class SimpleResponse(
    override val type: WebSocketMessageType
) : WebSocketMessage
