package com.example.planning_poker_room.api.dto

data class CreateRoomResponse(
    val roomId: String,
    val hostToken: String
)