package com.example.planning_poker_room.websocket.dto.response

import com.example.planning_poker_room.store.model.Room
import com.example.planning_poker_room.websocket.dto.WebSocketMessage
import com.example.planning_poker_room.websocket.dto.WebSocketMessageType
import java.util.concurrent.atomic.AtomicLong

data class RoomStateResponse(
    override val type: WebSocketMessageType = WebSocketMessageType.ROOM_STATE,
    val phase: String,
    val version: AtomicLong,
    val participants: List<ExtendedParticipantDto>
) : WebSocketMessage {
    companion object {
        fun of(room: Room): RoomStateResponse {
            return RoomStateResponse(
                phase = room.phase.toString(),
                version = room.version,
                participants = room.participants.map { ExtendedParticipantDto.of(it) }
            )
        }
    }
}
