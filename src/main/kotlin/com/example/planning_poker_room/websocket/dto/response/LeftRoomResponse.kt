package com.example.planning_poker_room.websocket.dto.response

import com.example.planning_poker_room.websocket.dto.WebSocketMessage
import com.example.planning_poker_room.websocket.dto.WebSocketMessageType
import java.util.UUID
import java.util.concurrent.atomic.AtomicLong

data class LeftRoomResponse(
    override val type: WebSocketMessageType = WebSocketMessageType.LEFT,
    override val version: AtomicLong,
    val participantId: UUID
): WebSocketMessage, RoomStateChangingEvent
