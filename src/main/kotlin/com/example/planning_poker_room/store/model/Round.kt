package com.example.planning_poker_room.store.model

import java.util.UUID

class Round(
    var number: Int,
    val votes: Map<UUID, Int>
)