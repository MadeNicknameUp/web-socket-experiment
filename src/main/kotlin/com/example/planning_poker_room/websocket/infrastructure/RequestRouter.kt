package com.example.planning_poker_room.websocket.infrastructure

import com.example.planning_poker_room.api.service.RoomService
import com.example.planning_poker_room.exception.unit.ConnectionNotFoundException
import com.example.planning_poker_room.exception.unit.InvalidMessageException
import com.example.planning_poker_room.store.repository.ConnectionRepository
import com.example.planning_poker_room.websocket.dto.WebSocketMessage
import com.example.planning_poker_room.websocket.dto.WebSocketMessageType
import com.example.planning_poker_room.websocket.dto.request.ResetRoundRequest
import com.example.planning_poker_room.websocket.dto.request.RevealRequest
import com.example.planning_poker_room.websocket.dto.request.RoomStateRequest
import com.example.planning_poker_room.websocket.dto.request.VoteRequest
import com.example.planning_poker_room.websocket.dto.request.WebSocketRequest
import com.example.planning_poker_room.websocket.dto.response.ParticipantVotedResponse
import com.example.planning_poker_room.websocket.dto.response.RoomStateResponse
import com.example.planning_poker_room.websocket.dto.response.RoundResetResponse
import com.example.planning_poker_room.websocket.dto.response.RoundRevealedResponse
import com.example.planning_poker_room.websocket.dto.response.SimpleResponse
import com.example.planning_poker_room.websocket.service.WebSocketService
import org.springframework.stereotype.Component
import org.springframework.web.socket.TextMessage
import tools.jackson.databind.ObjectMapper

@Component
class RequestRouter(
    private val connectionRepository: ConnectionRepository,
    private val objectMapper: ObjectMapper,
    private val roomService: RoomService,
    private val service: WebSocketService
) {

    fun convertAndRoute(message: TextMessage, sessionId: String) : Pair<ResponseMode, WebSocketMessage> {

        val request = objectMapper.readValue(
            message.payload,
            WebSocketRequest::class.java
        )

        if (request.type == WebSocketMessageType.PING)
            return Pair(ResponseMode.UNICAST_SENDER, SimpleResponse(WebSocketMessageType.PONG))

        return when (request) {
            is VoteRequest -> {

                val roomAndParticipantId = service.vote(request.vote, sessionId)
                val room = roomAndParticipantId.first
                val participantId = roomAndParticipantId.second

                Pair(
                    ResponseMode.MULTICAST_EXCEPT_SENDER, ParticipantVotedResponse(
                        version = room.version,
                        participantId = participantId
                    )
                )
            }

            is RevealRequest -> {

                val room = service.reveal(sessionId)

                service.reveal(sessionId)
                Pair(
                    ResponseMode.BROADCAST, RoundRevealedResponse(
                    type = WebSocketMessageType.ROUND_REVEALED,
                    version = room.version,
                    votes = roomService
                        .getRoomById(
                            connectionRepository.findBySessionId(sessionId)?.roomId
                                ?: throw ConnectionNotFoundException("Connection for session_id: $sessionId does not exist.")
                        ).participants.associate {
                            Pair(
                                it.id,
                                it.vote
                            )
                        }
                ))
            }

            is ResetRoundRequest -> {
                val room = service.roundReset(sessionId)
                Pair(
                    ResponseMode.BROADCAST, RoundResetResponse(
                        version = room.version
                    )
                )
            }

            is RoomStateRequest ->
                Pair(
                    ResponseMode.UNICAST_SENDER,
                    RoomStateResponse.of(service.findRoomBySessionId(sessionId))
                )

            else -> throw InvalidMessageException("Invalid request type: ${request.type}")
        }
    }
}