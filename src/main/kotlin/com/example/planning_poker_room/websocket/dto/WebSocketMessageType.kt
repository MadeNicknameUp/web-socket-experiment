package com.example.planning_poker_room.websocket.dto

enum class WebSocketMessageType {
    // --- Request
    PING,
    PONG,
    VOTE, // valid values: 1 2 3 5 8 13 21
    REVEAL,
    RESET_ROUND, // host only
    // --- Response
    JOINED,
    LEFT
}