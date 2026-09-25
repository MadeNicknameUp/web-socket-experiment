package com.example.planning_poker_room.exception.unit

class InvalidMessageException(
    override val message: String
) : RuntimeException(message)