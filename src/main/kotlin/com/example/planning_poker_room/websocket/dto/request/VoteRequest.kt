package com.example.planning_poker_room.websocket.dto.request

import com.example.planning_poker_room.websocket.dto.WebSocketMessageType
import com.fasterxml.jackson.annotation.JsonTypeName

@JsonTypeName("VOTE")
data class VoteRequest(
    val vote: Int,
) : WebSocketRequest(WebSocketMessageType.VOTE)
