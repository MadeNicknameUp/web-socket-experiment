package com.example.planning_poker_room.infrastructure.util

import com.example.planning_poker_room.store.model.Participant
import com.example.planning_poker_room.store.model.Room

fun Room.addParticipant(participant: Participant): Participant =
    participant.apply { participants.add(participant) }
