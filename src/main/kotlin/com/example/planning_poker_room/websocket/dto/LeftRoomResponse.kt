package com.example.planning_poker_room.websocket.dto

import java.util.UUID

data class LeftRoomResponse(
    override val type: WebSocketMessageType = WebSocketMessageType.LEFT,
    val participantId: UUID
): WebSocketMessage
