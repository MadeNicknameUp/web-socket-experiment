package com.example.planning_poker_room.exception.unit

class NotAuthorizedException(
    override val message: String
) : RuntimeException(message)