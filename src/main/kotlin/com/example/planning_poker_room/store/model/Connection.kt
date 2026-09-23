package com.example.planning_poker_room.store.model

import java.util.UUID

class Connection(
    val participantId: UUID,
    var sessionId: String?,
    val roomId: UUID
)