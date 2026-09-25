package com.example.planning_poker_room.websocket.dto.response

import com.example.planning_poker_room.store.model.Room
import com.example.planning_poker_room.websocket.dto.response.ExtendedParticipantDto
import com.example.planning_poker_room.websocket.dto.WebSocketMessage
import com.example.planning_poker_room.websocket.dto.WebSocketMessageType

data class RoomStateResponse(
    override val type: WebSocketMessageType = WebSocketMessageType.ROOM_STATE,
    val phase: String,
    val participants: List<ExtendedParticipantDto>
) : WebSocketMessage {
    companion object {
        fun of(room: Room): RoomStateResponse {
            return RoomStateResponse(
                phase = room.phase.toString(),
                participants = room.participants.map { ExtendedParticipantDto.Companion.of(it) }
            )
        }
    }
}
