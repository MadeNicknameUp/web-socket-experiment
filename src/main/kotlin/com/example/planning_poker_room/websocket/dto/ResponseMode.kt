package com.example.planning_poker_room.websocket.dto

enum class ResponseMode {
    BROADCAST,
    UNICAST_SENDER,
    MULTICAST_EXCEPT_SENDER
}