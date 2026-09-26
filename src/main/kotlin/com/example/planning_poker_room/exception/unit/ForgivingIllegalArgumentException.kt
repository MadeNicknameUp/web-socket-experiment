package com.example.planning_poker_room.exception.unit

class ForgivingIllegalArgumentException(
    override val message: String
) : IllegalArgumentException(message)