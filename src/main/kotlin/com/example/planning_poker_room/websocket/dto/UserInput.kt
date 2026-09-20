package com.example.planning_poker_room.websocket.dto

import com.example.planning_poker_room.store.model.Participant

data class UserInput(
    val type: String,
    val value: Int,
    val participants: List<Participant>
)