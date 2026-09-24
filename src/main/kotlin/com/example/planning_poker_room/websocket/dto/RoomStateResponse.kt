package com.example.planning_poker_room.websocket.dto

import com.example.planning_poker_room.store.model.Room

data class RoomStateResponse(
    override val type: WebSocketMessageType = WebSocketMessageType.ROOM_STATE,
    val phase: String,
    val participants: List<ExtendedParticipantDto>
) : WebSocketMessage {
    companion object {
        fun of(room: Room): RoomStateResponse {
            return RoomStateResponse(
                phase = room.phase.toString(),
                participants = room.participants.map { ExtendedParticipantDto.of(it) }
            )
        }
    }
}
