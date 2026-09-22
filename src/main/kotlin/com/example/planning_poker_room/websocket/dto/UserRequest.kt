package com.example.planning_poker_room.websocket.dto

data class UserRequest(
    override val type: WebSocketMessageType
) : WebSocketMessage