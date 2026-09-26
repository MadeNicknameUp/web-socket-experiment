package com.example.planning_poker_room.api.controller

import com.example.planning_poker_room.api.dto.CreateRoomRequest
import com.example.planning_poker_room.api.dto.CreateRoomResponse
import com.example.planning_poker_room.api.dto.JoinRoomRequest
import com.example.planning_poker_room.api.dto.JoinRoomResponse
import com.example.planning_poker_room.api.service.RoomService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/api/rooms")
class RoomController(
    private val roomService: RoomService
) {

    @PostMapping
    fun createRoom(@RequestBody request: CreateRoomRequest): ResponseEntity<CreateRoomResponse> {

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(
                CreateRoomResponse.of(roomService.createRoom(request.name))
            )
    }

    @PostMapping("/{roomId}/participants")
    fun joinRoom(@PathVariable roomId: UUID, @RequestBody request: JoinRoomRequest): ResponseEntity<JoinRoomResponse> {

        return ResponseEntity.ok(
            JoinRoomResponse.of(roomService.joinRoom(roomId, request.name))
        )
    }

}