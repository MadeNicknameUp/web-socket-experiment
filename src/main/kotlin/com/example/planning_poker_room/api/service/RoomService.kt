package com.example.planning_poker_room.api.service

import com.example.planning_poker_room.api.dto.CreateRoomRequest
import com.example.planning_poker_room.exception.unit.RoomNotFoundException
import com.example.planning_poker_room.infrastructure.util.addParticipant
import com.example.planning_poker_room.store.model.Participant
import com.example.planning_poker_room.store.model.ParticipantName
import com.example.planning_poker_room.store.model.Room
import com.example.planning_poker_room.store.model.RoomName
import com.example.planning_poker_room.store.repository.RoomRepository
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class RoomService(
    private val roomRepository: RoomRepository
) {

    fun createRoom(request: CreateRoomRequest): Room =
        roomRepository.save(
            Room.create(RoomName(request.name))
        )

    fun getRoomById(id: UUID): Room
        = roomRepository.findById(id) ?: throw RoomNotFoundException("Room with id: $id does not exit.")

    fun joinRoom(roomId: UUID, participantName: String): Participant {

        val room: Room = roomRepository.findById(roomId)
            ?: throw RoomNotFoundException("Room with id: $roomId does not exit.")

        return room.addParticipant(
            Participant.create(ParticipantName(participantName))
        )
    }
}