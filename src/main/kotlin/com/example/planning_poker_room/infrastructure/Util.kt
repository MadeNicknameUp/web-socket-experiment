package com.example.planning_poker_room.infrastructure

import com.example.planning_poker_room.api.dto.CreateRoomResponse
import com.example.planning_poker_room.store.model.Room

fun Room.toResponse(): CreateRoomResponse =
    CreateRoomResponse(
        id.toString(),
        hostParticipantId.toString()
    )