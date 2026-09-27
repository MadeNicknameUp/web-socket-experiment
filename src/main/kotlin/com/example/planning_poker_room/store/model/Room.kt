package com.example.planning_poker_room.store.model

import com.example.planning_poker_room.exception.unit.ConnectionAlreadyExistsException
import com.example.planning_poker_room.exception.unit.ParticipantNotFoundException
import com.example.planning_poker_room.exception.unit.RoomNotInVotingPhaseException
import java.util.UUID
import java.util.concurrent.atomic.AtomicLong

class Room private constructor(
    val id: UUID,
    val name: RoomName,
    val hostParticipantId: UUID,
    val participants: MutableList<Participant>,
    var phase: RoomState,
    var version: AtomicLong = AtomicLong(0)
) {

    fun connect(participantId: UUID) {
        val participant = findParticipantById(participantId)
            ?: throw ParticipantNotFoundException("Participant with id $participantId does not exist.")

        if (participant.connected)
            throw ConnectionAlreadyExistsException("Participant is already connected.")

        participant.connected = true
        incrementVersion()
    }

    fun disconnect(participantId: UUID) {
        val currentParticipant = findParticipantById(participantId)
            ?: throw ParticipantNotFoundException("No participant with id: $participantId found.")

        currentParticipant.connected = false
        incrementVersion()
    }

    fun findParticipantById(participantId: UUID): Participant? {
        return participants.find { it.id == participantId }
    }

    fun resetRound() {
        phase = RoomState.VOTING
        participants.forEach { it.vote = null }
        incrementVersion()
    }

    fun reveal() {
        if (phase != RoomState.VOTING)
            throw RoomNotInVotingPhaseException("Invalid state: Room is not VOTING yet/anymore.")

        phase = RoomState.REVEALED
        incrementVersion()
    }

    fun incrementVersion() =
        version.incrementAndGet()


    companion object {
        fun create(name: RoomName): Room {

            return Room(
                id = UUID.randomUUID(),
                name = name,
                phase = RoomState.VOTING,
                hostParticipantId = UUID.randomUUID(),
                participants = mutableListOf(),
            )
        }
    }
}