package com.example.planning_poker_room.exception.unit

data class InvalidVoteException(
    override val message: String
) : RuntimeException(message)
