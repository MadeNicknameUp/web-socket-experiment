package com.example.planning_poker_room.api.dto

data class CreateRoomRequest(
    val roomName: String,
    val hostName: String
)