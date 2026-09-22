package com.example.planning_poker_room.websocket.service

import com.example.planning_poker_room.exception.unit.ParticipantNotFoundException
import com.example.planning_poker_room.exception.unit.RoomNotFoundException
import com.example.planning_poker_room.store.model.Connection
import com.example.planning_poker_room.store.repository.ConnectionRepository
import com.example.planning_poker_room.store.repository.RoomRepository
import org.springframework.stereotype.Service
import tools.jackson.databind.ObjectMapper
import java.util.UUID

@Service
class SocketService(
    private val connectionRepository: ConnectionRepository,
    private val roomRepository: RoomRepository,
    private val objectMapper: ObjectMapper
) {

    fun joinRoom(roomId: UUID?, participantId: UUID?, sessionId: String) {

        require(roomId != null && participantId != null) {
            "Invalid value: roomId and participantId must not be null"
        }

        val room = roomRepository.findById(roomId)
            ?: throw RoomNotFoundException("Room with id $roomId does not exist")

        val participant = room.participants.find { it.id == participantId }
            ?: throw ParticipantNotFoundException("Participant with id $participantId does not exist.")

        connectionRepository.save(Connection(
            participantId = participantId,
            roomId = roomId,
            sessionId = sessionId
            )
        )
        participant.connected = true
    }
}