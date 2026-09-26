package com.example.planning_poker_room.websocket.service

import com.example.planning_poker_room.exception.unit.ConnectionAlreadyExistsException
import com.example.planning_poker_room.exception.unit.InvalidVoteException
import com.example.planning_poker_room.exception.unit.ParticipantNotFoundException
import com.example.planning_poker_room.exception.unit.RoomNotFoundException
import com.example.planning_poker_room.exception.unit.RoomNotInVotingPhaseException
import com.example.planning_poker_room.store.model.Connection
import com.example.planning_poker_room.store.model.Participant
import com.example.planning_poker_room.store.model.Room
import com.example.planning_poker_room.store.model.RoomState
import com.example.planning_poker_room.store.repository.ConnectionRepository
import com.example.planning_poker_room.store.repository.RoomRepository
import org.springframework.stereotype.Service
import java.util.concurrent.atomic.AtomicLong
import java.util.UUID

var CURRENT_VERSION: AtomicLong = AtomicLong(0)
val VOTES_ALLOWED = listOf(1, 2, 3, 5, 8, 13, 21)

@Service
class SocketService(
    private val connectionRepository: ConnectionRepository,
    private val roomRepository: RoomRepository
) {

    fun joinRoom(roomId: UUID?, participantId: UUID?, sessionId: String): Room {

        require(roomId != null && participantId != null) {
            "Invalid input value: roomId and participantId must not be null"
        }

        val room = roomRepository.findById(roomId)
            ?: throw RoomNotFoundException("Room with id $roomId does not exist")

        // TODO: Room must now contain this action (e.g. via extension function).
        val participant = room.participants.find { it.id == participantId }
            ?: throw ParticipantNotFoundException("Participant with id $participantId does not exist.")

        if (participant.connected) {
            throw ConnectionAlreadyExistsException("Participant is already connected.")
        }

        connectionRepository.save(Connection(
            participantId = participantId,
            roomId = roomId,
            sessionId = sessionId
            )
        )

        participant.connected = true
        CURRENT_VERSION.incrementAndGet()

        return room
    }

    fun vote(vote: Int, sessionId: String): UUID {

        // TODO: Nice, but replace with Slf4j when refactored.
        println("participant.vote invoked.")

        if (vote !in VOTES_ALLOWED)
            throw InvalidVoteException("Invalid vote value: $vote. Expected: $VOTES_ALLOWED")

        val connection = connectionRepository.findBySessionId(sessionId)

        val room = roomRepository.findById(connection.roomId)
            ?: throw RoomNotFoundException("Room with id: ${connection.roomId} does not exist.")

        check(room.phase == RoomState.VOTING) {
            "Invalid state: Room is not VOTING yet/anymore."
        }

        val participant: Participant = room.participants.find { connection.participantId == it.id }
            ?: throw ParticipantNotFoundException("Participant with id: ${connection.participantId} does not exist.")

        participant.vote = vote
        CURRENT_VERSION.incrementAndGet()

        return participant.id
    }

    fun reveal(sessionId: String) {

        val connection = connectionRepository.findBySessionId(sessionId)

        val room = roomRepository.findById(connection.roomId)
            ?: throw RoomNotFoundException("Room with id: ${connection.roomId} does not exist.")

        // TODO: Check if session owner is a host.

        // TODO: It would be nice, if this state change was also managed by Room. (Simple method on domain object?)
        if (room.phase != RoomState.VOTING)
            throw RoomNotInVotingPhaseException("Invalid state: Room is not VOTING yet/anymore.")

        room.phase = RoomState.REVEALED

        CURRENT_VERSION.incrementAndGet()
    }

    fun roundReset(sessionId: String) {

        val connection = connectionRepository.findBySessionId(sessionId)

        val room = roomRepository.findById(connection.roomId)
            ?: throw RoomNotFoundException("Room with id: ${connection.roomId} does not exist.")

        // TODO: Check if session owner is a host.

        // TODO: It would be nice, if this state change was also managed by Room. (Simple method on domain object?)
        room.phase = RoomState.VOTING
        room.participants.forEach { it.vote = null }

        CURRENT_VERSION.incrementAndGet()
    }

    fun leaveRoom(sessionId: String): Pair<UUID, UUID> {
        val connection = connectionRepository.findBySessionId(sessionId)

        val room = roomRepository.findById(connection.roomId)
            ?: throw RoomNotFoundException("Room with id: ${connection.roomId} does not exist.")

        connection.sessionId = null

        val currentParticipant = room.participants.find { it.id == connection.participantId }
            ?: throw ParticipantNotFoundException("No participant with id: ${connection.participantId} found.")

        currentParticipant.connected = false

        return Pair(room.id, currentParticipant.id)
    }

    fun findRoomBySessionId(sessionId: String): Room {

        val connection = connectionRepository.findBySessionId(sessionId)

        val room = roomRepository.findById(connection.roomId)
            ?: throw RoomNotFoundException("Room with id: ${connection.roomId} does not exist.")

        return room
    }

}

















