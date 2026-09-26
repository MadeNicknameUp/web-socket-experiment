package com.example.planning_poker_room.websocket.service

import com.example.planning_poker_room.exception.unit.FatalIllegalArgumentException
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

        if (roomId == null || participantId == null)
            throw FatalIllegalArgumentException("Invalid input value: roomId and participantId must not be null")

        val room = roomRepository.findById(roomId)
            ?: throw RoomNotFoundException("Room with id $roomId does not exist")

        room.connect(participantId)

        connectionRepository.save(Connection(
            participantId = participantId,
            roomId = roomId,
            sessionId = sessionId
            )
        )

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

        if (room.phase != RoomState.VOTING)
            throw RoomNotInVotingPhaseException("Invalid state: Room is not VOTING yet/anymore.")

        val participant: Participant = room.findParticipantById(connection.participantId)
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

        room.reveal()

        CURRENT_VERSION.incrementAndGet()
    }

    fun roundReset(sessionId: String) {

        val connection = connectionRepository.findBySessionId(sessionId)

        val room = roomRepository.findById(connection.roomId)
            ?: throw RoomNotFoundException("Room with id: ${connection.roomId} does not exist.")

        // TODO: Check if session owner is a host.

        room.resetRound()

        CURRENT_VERSION.incrementAndGet()
    }

    fun leaveRoom(sessionId: String): Pair<UUID, UUID> {
        val connection = connectionRepository.findBySessionId(sessionId)

        val room = roomRepository.findById(connection.roomId)
            ?: throw RoomNotFoundException("Room with id: ${connection.roomId} does not exist.")

        connection.sessionId = null

        room.disconnect(connection.participantId)

        return Pair(room.id, connection.participantId)
    }

    fun findRoomBySessionId(sessionId: String): Room {

        val connection = connectionRepository.findBySessionId(sessionId)

        val room = roomRepository.findById(connection.roomId)
            ?: throw RoomNotFoundException("Room with id: ${connection.roomId} does not exist.")

        return room
    }

}

















