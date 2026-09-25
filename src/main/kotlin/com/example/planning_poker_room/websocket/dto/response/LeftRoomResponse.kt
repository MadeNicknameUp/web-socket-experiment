package com.example.planning_poker_room.websocket.dto.response

import com.example.planning_poker_room.websocket.dto.WebSocketMessage
import com.example.planning_poker_room.websocket.dto.WebSocketMessageType
import java.util.UUID

data class LeftRoomResponse(
    override val type: WebSocketMessageType = WebSocketMessageType.LEFT,
    val participantId: UUID
): WebSocketMessage
