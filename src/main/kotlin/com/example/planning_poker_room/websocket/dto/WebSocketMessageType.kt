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
    ROUND_REVEALED,
    // --- Exception
    PARTICIPANT_NOT_FOUND,
    CONNECTION_NOT_FOUND,
    ROOM_NOT_FOUND,
    INVALID_MESSAGE,
    INVALID_VOTE,
    NOT_HOST,
    ROOM_NOT_IN_VOTING_PHASE,
    ALREADY_CONNECTED,
    UNKNOWN_EXCEPTION
}