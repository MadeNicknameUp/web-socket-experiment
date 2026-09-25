package com.example.planning_poker_room.exception.unit

class ParticipantNotFoundException(
    override val message: String
) : NotFoundException(message)