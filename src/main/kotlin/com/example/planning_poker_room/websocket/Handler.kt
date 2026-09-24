package com.example.planning_poker_room.websocket

import com.example.planning_poker_room.api.service.RoomService
import com.example.planning_poker_room.exception.unit.ParticipantNotFoundException
import com.example.planning_poker_room.store.repository.ConnectionRepository
import com.example.planning_poker_room.store.repository.SessionRepository
import com.example.planning_poker_room.websocket.dto.ExtendedParticipantDto
import com.example.planning_poker_room.websocket.dto.ParticipantDto
import com.example.planning_poker_room.websocket.dto.JoinRoomResponse
import com.example.planning_poker_room.websocket.dto.LeftRoomResponse
import com.example.planning_poker_room.websocket.dto.ParticipantVotedResponse
import com.example.planning_poker_room.websocket.dto.ResetRoundRequest
import com.example.planning_poker_room.websocket.dto.ResponseMode
import com.example.planning_poker_room.websocket.dto.RevealRequest
import com.example.planning_poker_room.websocket.dto.RoomStateRequest
import com.example.planning_poker_room.websocket.dto.RoomStateResponse
import com.example.planning_poker_room.websocket.dto.RoundResetResponse
import com.example.planning_poker_room.websocket.dto.RoundRevealedResponse
import com.example.planning_poker_room.websocket.dto.SimpleResponse
import com.example.planning_poker_room.websocket.dto.WebSocketRequest
import com.example.planning_poker_room.websocket.dto.VoteRequest
import com.example.planning_poker_room.websocket.dto.WebSocketMessage
import com.example.planning_poker_room.websocket.dto.WebSocketMessageType
import com.example.planning_poker_room.websocket.service.SocketService
import org.springframework.stereotype.Component
import org.springframework.web.socket.CloseStatus
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.handler.TextWebSocketHandler
import tools.jackson.databind.ObjectMapper
import java.util.UUID

@Component
class CustomWebSocketHandler(
    private val connectionRepository: ConnectionRepository,
    private val sessionRepository: SessionRepository,
    private val roomService: RoomService,
    private val service: SocketService,
    private val objectMapper: ObjectMapper
) : TextWebSocketHandler() {

    override fun afterConnectionEstablished(session: WebSocketSession) {
        sessionRepository.save(session)

        println("CONNECTED ${session.id}")

        println(session.attributes["roomId"])
        println(session.attributes["participantId"])

        val roomId: UUID = UUID.fromString(session.attributes["roomId"] as String?)
        val participantId: UUID = UUID.fromString(session.attributes["participantId"] as String?)

        val room = service.joinRoom(
            roomId = roomId,
            participantId = participantId,
            sessionId = session.id
        )

        val connection = connectionRepository.findBySessionId(session.id)

        val currentParticipant = room.participants.find { it.id == connection.participantId }
            ?: throw ParticipantNotFoundException("No participant with id: $participantId found.")

        val jsonJoinRoomResponse = objectMapper.writeValueAsString(JoinRoomResponse(
            participant = ParticipantDto(
                id = currentParticipant.id,
                name = currentParticipant.name.value
            )))

        val jsonRoomStateResponse = objectMapper.writeValueAsString(RoomStateResponse(
            phase = room.phase.toString(),
            participants = room.participants.map { ExtendedParticipantDto.of(it) }
        ))

        broadcast(room.id, jsonJoinRoomResponse)
        broadcast(room.id, jsonRoomStateResponse)
    }

    override fun handleTextMessage(
        session: WebSocketSession,
        message: TextMessage
    ) {
        println("${session.id} -> ${message.payload}")

        val typeResponsePair = routeMessage(message, session.id)
        val responseMode = typeResponsePair.first
        val response = typeResponsePair.second
        val jsonResponse = objectMapper.writeValueAsString(response)

        val connection = connectionRepository.findBySessionId(session.id)

        val room = roomService.getRoomById(connection.roomId)

        when (responseMode) {
            ResponseMode.BROADCAST -> broadcast(
                room.id,
                jsonResponse
            )
            ResponseMode.MULTICAST_EXCEPT_SENDER -> multicastExceptSender(
                room.id,
                session.id,
                jsonResponse
            )
            ResponseMode.UNICAST_SENDER -> unicastSender(
                session.id,
                jsonResponse
            )
        }
    }

    override fun afterConnectionClosed(
        session: WebSocketSession,
        status: CloseStatus
    ) {
        sessionRepository.remove(session)

        println("DISCONNECTED ${session.id}: $status")

        val connection = connectionRepository.findBySessionId(session.id)

        val room = roomService.getRoomById(connection.roomId)

        connection.sessionId = null

        val currentParticipant = room.participants.find { it.id == connection.participantId }
            ?: throw ParticipantNotFoundException("No participant with id: ${connection.participantId} found.")

        currentParticipant.connected = false

        val response = LeftRoomResponse(
            participantId = currentParticipant.id
        )

        val responseMessage = TextMessage(
            objectMapper.writeValueAsString(response)
        )

        room.participants
            .forEach {
                val sessionId = connectionRepository.findByParticipantId(it.id).sessionId

                if (sessionId != null && sessionId != session.id) {

                    sessionRepository.findById(
                        sessionId
                    ).sendMessage(responseMessage)
                }
            }
    }

    private fun routeMessage(message: TextMessage, sessionId: String) : Pair<ResponseMode, WebSocketMessage> {

        val request = objectMapper.readValue(
            message.payload,
            WebSocketRequest::class.java
        )

        if (request.type == WebSocketMessageType.PING)
            return Pair(ResponseMode.UNICAST_SENDER, SimpleResponse(WebSocketMessageType.PONG))

        return when (request) {
            is VoteRequest -> Pair(ResponseMode.MULTICAST_EXCEPT_SENDER, ParticipantVotedResponse(
                participantId = service.vote(request.vote, sessionId)
            ))
            is RevealRequest -> {
                service.reveal(sessionId)
                return Pair(ResponseMode.BROADCAST, RoundRevealedResponse(
                    WebSocketMessageType.ROUND_REVEALED,
                    roomService.getRoomById(connectionRepository.findBySessionId(sessionId).roomId).participants.associate {
                        Pair(
                            it.id,
                            it.vote
                        )
                    }
                ))
            }
            is ResetRoundRequest -> {
                service.roundReset(sessionId)
                return Pair(ResponseMode.BROADCAST, RoundResetResponse())
            }
            is RoomStateRequest ->
                Pair(
                    ResponseMode.UNICAST_SENDER,
                    RoomStateResponse.of(service.findRoomBySessionId(sessionId))
                )
            else -> throw RuntimeException("Invalid request type: ${request.type}")
        }
    }

    private fun broadcast(roomId: UUID, payload: String) {
        connectionRepository.findByRoomId(roomId)
            .map { it.sessionId }
            .forEach {

                if (it != null)
                    sessionRepository.findById(it).sendMessage(TextMessage(payload))

            }
    }

    private fun unicastSender(sessionId: String, payload: String) {
        sessionRepository.findById(sessionId).sendMessage(TextMessage(payload))
    }

    private fun multicastExceptSender(roomId: UUID, sessionId: String, payload: String) {
        connectionRepository.findByRoomId(roomId)
            .map { it.sessionId }
            .forEach {

                if (it != null && it != sessionId)
                    sessionRepository.findById(it).sendMessage(TextMessage(payload))

            }
    }

}