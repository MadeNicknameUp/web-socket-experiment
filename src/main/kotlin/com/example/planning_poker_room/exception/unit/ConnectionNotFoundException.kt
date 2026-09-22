package com.example.planning_poker_room.exception.unit

class ConnectionNotFoundException(
    override val message: String
) : RuntimeException(message)