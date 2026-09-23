package com.example.planning_poker_room.exception.unit

import java.lang.RuntimeException

class ConnectionAlreadyExistsException(
    override val message: String
) : RuntimeException(message)