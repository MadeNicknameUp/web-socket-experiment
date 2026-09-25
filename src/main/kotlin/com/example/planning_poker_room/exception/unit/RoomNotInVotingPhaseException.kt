package com.example.planning_poker_room.exception.unit

class RoomNotInVotingPhaseException(
    override val message: String
) : RuntimeException(message)
