package com.example.planning_poker_room.websocket.dto.response

import com.example.planning_poker_room.websocket.dto.WebSocketMessage
import com.example.planning_poker_room.websocket.dto.WebSocketMessageType
import java.util.concurrent.atomic.AtomicLong

data class JoinRoomResponse(
    override val type: WebSocketMessageType = WebSocketMessageType.JOINED,
    override val version: AtomicLong,
    val participant: ThinParticipantDto
) : WebSocketMessage, RoomStateChangingEvent
