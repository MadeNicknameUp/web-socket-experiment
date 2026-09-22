package com.example.planning_poker_room.websocket.dto

data class JoinRoomResponse(
    override val type: WebSocketMessageType,
) : WebSocketMessage
