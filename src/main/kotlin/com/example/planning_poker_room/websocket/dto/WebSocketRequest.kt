package com.example.planning_poker_room.websocket.dto

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type", include = JsonTypeInfo.As.PROPERTY)
@JsonSubTypes(
    value = [JsonSubTypes.Type(value = VoteRequest::class, name = "VOTE")]

)
sealed class WebSocketRequest(
    override val type: WebSocketMessageType
) : WebSocketMessage