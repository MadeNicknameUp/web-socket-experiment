package com.example.planning_poker_room.websocket.dto

class RoundResetResponse(
    override val type: WebSocketMessageType = WebSocketMessageType.ROUND_RESET,
) : WebSocketMessage