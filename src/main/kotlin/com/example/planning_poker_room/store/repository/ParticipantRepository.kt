package com.example.planning_poker_room.store.repository

import com.example.planning_poker_room.store.model.Participant
import java.util.UUID

interface ParticipantRepository {

    fun findById(id: UUID): Participant?

    fun save(participant: Participant): Participant
}