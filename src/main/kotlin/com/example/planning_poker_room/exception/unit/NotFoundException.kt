package com.example.planning_poker_room.exception.unit

open class NotFoundException(
    override val message: String
) : RuntimeException(message)
