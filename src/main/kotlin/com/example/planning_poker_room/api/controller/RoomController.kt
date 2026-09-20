package com.example.planning_poker_room.api.controller

import com.example.planning_poker_room.api.dto.CreateRoomRequest
import com.example.planning_poker_room.api.dto.CreateRoomResponse
import com.example.planning_poker_room.api.service.RoomService
import com.example.planning_poker_room.infrastructure.toResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
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

        return ResponseEntity.ok(roomService
            .createRoom(request)
            .toResponse()
        )
    }

    @GetMapping("/{roomId}")
    fun fetchRoomById(@PathVariable roomId: UUID): ResponseEntity<String> {

        return ResponseEntity.ok("Room with id $roomId not found.")
    }

    @PostMapping("/{roomId}/connection")
    fun joinRoom(@PathVariable roomId: UUID): ResponseEntity<String> {

        return ResponseEntity.ok(null)
    }

}