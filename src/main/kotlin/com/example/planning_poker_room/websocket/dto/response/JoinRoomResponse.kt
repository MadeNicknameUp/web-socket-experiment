package com.example.planning_poker_room.websocket.dto.response

import com.example.planning_poker_room.websocket.dto.response.ParticipantDto
import com.example.planning_poker_room.websocket.dto.WebSocketMessage
import com.example.planning_poker_room.websocket.dto.WebSocketMessageType

data class JoinRoomResponse(
    override val type: WebSocketMessageType = WebSocketMessageType.JOINED,
    val participant: ParticipantDto
) : WebSocketMessage
