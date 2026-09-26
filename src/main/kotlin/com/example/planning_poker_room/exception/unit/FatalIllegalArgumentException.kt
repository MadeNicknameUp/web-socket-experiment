package com.example.planning_poker_room.exception.unit

class FatalIllegalArgumentException(
    override val message: String
) : IllegalArgumentException(message)