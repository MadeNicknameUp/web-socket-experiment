package com.example.planning_poker_room.api.service

import com.example.planning_poker_room.api.dto.CreateRoomRequest
import com.example.planning_poker_room.store.model.Participant
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
            Room.create(
                RoomName(request.name),
                UUID.randomUUID()
            )
        )

    fun getRoomById(id: UUID): Room?
        = roomRepository.findById(id)

    // TODO: idk what to do here yet.
    fun joinRoom(id: UUID): Room? {

        return roomRepository.findById(id)
    }
}