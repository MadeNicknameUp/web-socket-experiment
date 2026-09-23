package com.example.planning_poker_room.exception.unit

class SessionNotFoundException(
    override val message: String
) : RuntimeException(message)