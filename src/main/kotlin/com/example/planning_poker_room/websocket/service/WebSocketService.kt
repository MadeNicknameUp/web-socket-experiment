package com.example.planning_poker_room.websocket.service

import com.example.planning_poker_room.exception.unit.FatalIllegalArgumentException
import com.example.planning_poker_room.exception.unit.InvalidVoteException
import com.example.planning_poker_room.exception.unit.NotAuthorizedException
import com.example.planning_poker_room.exception.unit.ParticipantNotFoundException
import com.example.planning_poker_room.exception.unit.RoomNotFoundException
import com.example.planning_poker_room.exception.unit.RoomNotInVotingPhaseException
import com.example.planning_poker_room.store.model.Connection
import com.example.planning_poker_room.store.model.Participant
import com.example.planning_poker_room.store.model.Room
import com.example.planning_poker_room.store.model.RoomState
import com.example.planning_poker_room.store.repository.ConnectionRepository
import com.example.planning_poker_room.store.repository.RoomRepository
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service
import java.util.UUID

private val logger = KotlinLogging.logger {}

val VOTES_ALLOWED = listOf(1, 2, 3, 5, 8, 13, 21)

@Service
class WebSocketService(
    private val connectionRepository: ConnectionRepository,
    private val roomRepository: RoomRepository,
) {

    fun joinRoom(roomId: UUID?, participantId: UUID?, sessionId: String): Room {

        logger.trace { "room.join invoked." }

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

        return room
    }

    fun vote(vote: Int, sessionId: String): Pair<Room, UUID> {

        logger.trace { "participant.vote invoked." }

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

        room.incrementVersion()

        return Pair(room, participant.id)
    }

    fun reveal(sessionId: String): Room {

        logger.trace { "room.reveal invoked." }

        val connection = connectionRepository.findBySessionId(sessionId)

        val room = roomRepository.findById(connection.roomId)
            ?: throw RoomNotFoundException("Room with id: ${connection.roomId} does not exist.")

        if (connection.participantId != room.hostParticipantId)
            throw NotAuthorizedException("You are not authorized to reveal this room.")

        room.reveal()

        return room
    }

    fun roundReset(sessionId: String): Room {

        logger.trace { "room.reset invoked." }

        val connection = connectionRepository.findBySessionId(sessionId)

        val room = roomRepository.findById(connection.roomId)
            ?: throw RoomNotFoundException("Room with id: ${connection.roomId} does not exist.")

        if (connection.participantId != room.hostParticipantId)
            throw NotAuthorizedException("You are not authorized to reveal this room.")

        room.resetRound()

        return room
    }

    fun leaveRoom(sessionId: String): Pair<Room, UUID> {

        logger.trace { "participant.leave invoked." }

        val connection = connectionRepository.findBySessionId(sessionId)

        val room = roomRepository.findById(connection.roomId)
            ?: throw RoomNotFoundException("Room with id: ${connection.roomId} does not exist.")

        connection.sessionId = null

        room.disconnect(connection.participantId)

        return Pair(room, connection.participantId)
    }

    fun findRoomBySessionId(sessionId: String): Room {

        val connection = connectionRepository.findBySessionId(sessionId)

        val room = roomRepository.findById(connection.roomId)
            ?: throw RoomNotFoundException("Room with id: ${connection.roomId} does not exist.")

        return room
    }

}

















