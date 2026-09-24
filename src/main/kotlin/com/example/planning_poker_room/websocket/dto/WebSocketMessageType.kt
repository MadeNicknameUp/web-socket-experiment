package com.example.planning_poker_room.websocket.dto

enum class WebSocketMessageType {
    // --- Request
    PING,
    PONG,
    VOTE, // valid values: 1 2 3 5 8 13 21
    REVEAL,
    ROUND_RESET, // host only
    // --- Response
    LEFT,
    JOINED,
    ROOM_STATE,
    VOTE_RECEIVED,
    ROUND_REVEALED
}