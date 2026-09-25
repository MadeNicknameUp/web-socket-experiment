package com.example.planning_poker_room.api.exception.dto

data class RestErrorDto(
    val code: Int,
    val message: String?,
    val path: String
)