package com.example.planning_poker_room.websocket.dto.request

import com.example.planning_poker_room.websocket.dto.WebSocketMessage
import com.example.planning_poker_room.websocket.dto.WebSocketMessageType
import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type", include = JsonTypeInfo.As.PROPERTY)
@JsonSubTypes(
    value = [
        JsonSubTypes.Type(value = VoteRequest::class, name = "VOTE"),
        JsonSubTypes.Type(value = RevealRequest::class, name = "REVEAL"),
        JsonSubTypes.Type(value = ResetRoundRequest::class, name = "ROUND_RESET"),
        JsonSubTypes.Type(value = RoomStateRequest::class, name = "ROOM_STATE")
    ]
)
sealed class WebSocketRequest(
    override val type: WebSocketMessageType
) : WebSocketMessage