package com.example.planning_poker_room.exception.unit

class RoomNotFoundException(
    override val message: String
) : NotFoundException(message)