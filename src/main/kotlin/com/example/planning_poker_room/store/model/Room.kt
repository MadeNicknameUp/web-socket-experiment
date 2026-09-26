package com.example.planning_poker_room.store.model

import com.example.planning_poker_room.exception.unit.ConnectionAlreadyExistsException
import com.example.planning_poker_room.exception.unit.ParticipantNotFoundException
import com.example.planning_poker_room.exception.unit.RoomNotInVotingPhaseException
import java.util.UUID

class Room private constructor(
    val id: UUID,
    val name: RoomName,
//    val hostParticipantId: UUID,
    val participants: MutableList<Participant>,
    var phase: RoomState,
) {

    fun connect(participantId: UUID) {
        val participant = findParticipantById(participantId)
            ?: throw ParticipantNotFoundException("Participant with id $participantId does not exist.")

        if (participant.connected)
            throw ConnectionAlreadyExistsException("Participant is already connected.")

        participant.connected = true
    }

    fun disconnect(participantId: UUID) {
        val currentParticipant = findParticipantById(participantId)
            ?: throw ParticipantNotFoundException("No participant with id: ${participantId} found.")

        currentParticipant.connected = false
    }

    fun findParticipantById(participantId: UUID): Participant? {
        return participants.find { it.id == participantId }
    }

    fun resetRound() {
        phase = RoomState.VOTING
        participants.forEach { it.vote = null }
    }

    fun reveal() {
        if (phase != RoomState.VOTING)
            throw RoomNotInVotingPhaseException("Invalid state: Room is not VOTING yet/anymore.")

        phase = RoomState.REVEALED
    }

    companion object {
        fun create(name: RoomName): Room {

            return Room(
                id = UUID.randomUUID(),
                name = name,
                phase = RoomState.VOTING,
                participants = mutableListOf(),
            )
        }
    }
}