package com.example.planning_poker_room.websocket.dto

data class SimpleResponse(
    override val type: WebSocketMessageType
) : WebSocketMessage
