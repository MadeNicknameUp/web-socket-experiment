package com.example.planning_poker_room.websocket.dto.response

import com.example.planning_poker_room.websocket.dto.WebSocketMessage
import com.example.planning_poker_room.websocket.dto.WebSocketMessageType
import java.util.UUID

data class ParticipantVotedResponse(
    override val type: WebSocketMessageType = WebSocketMessageType.VOTE_RECEIVED,
    val participantId: UUID,
    val hasVoted: Boolean = true
) : WebSocketMessage