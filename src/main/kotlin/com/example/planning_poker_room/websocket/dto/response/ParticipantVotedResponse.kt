package com.example.planning_poker_room.websocket.dto.response

import com.example.planning_poker_room.websocket.dto.WebSocketMessage
import com.example.planning_poker_room.websocket.dto.WebSocketMessageType
import java.util.UUID
import java.util.concurrent.atomic.AtomicLong

data class ParticipantVotedResponse(
    override val type: WebSocketMessageType = WebSocketMessageType.VOTE_RECEIVED,
    val participantId: UUID,
    override val version: AtomicLong,
    val hasVoted: Boolean = true
) : WebSocketMessage, RoomStateChangingEvent