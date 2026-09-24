package com.example.planning_poker_room.websocket

import com.example.planning_poker_room.api.service.RoomService
import com.example.planning_poker_room.exception.unit.ParticipantNotFoundException
import com.example.planning_poker_room.store.repository.ConnectionRepository
import com.example.planning_poker_room.store.repository.SessionRepository
import com.example.planning_poker_room.websocket.dto.ParticipantDto
import com.example.planning_poker_room.websocket.dto.ParticipantJoined
import com.example.planning_poker_room.websocket.dto.LeftRoomResponse
import com.example.planning_poker_room.websocket.dto.ParticipantVotedResponse
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

        val responseMessage = TextMessage(objectMapper.writeValueAsString(ParticipantJoined(
            participant = ParticipantDto(
                id = currentParticipant.id,
                name = currentParticipant.name.value
            ))))

        room.participants
            .forEach {

                val sessionId = connectionRepository.findByParticipantId(it.id).sessionId ?: return

                sessionRepository.findById(
                    sessionId
                ).sendMessage(responseMessage)
            }
    }

    override fun handleTextMessage(
        session: WebSocketSession,
        message: TextMessage
    ) {
        println("${session.id} -> ${message.payload}")

        val response = routeMessage(message, session.id)
        val jsonResponse = objectMapper.writeValueAsString(response)

        val connection = connectionRepository.findBySessionId(session.id)

        val room = roomService.getRoomById(connection.roomId)

        println(connectionRepository.findByRoomId(room.id).size)

        connectionRepository.findByRoomId(room.id)
            .map { it.sessionId }
            .forEach {

                if (it != null && it != session.id)
                    sessionRepository.findById(it).sendMessage(TextMessage(jsonResponse))

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

        println("Room: ${room.participants}")

        room.participants
            .forEach {
                val sessionId = connectionRepository.findByParticipantId(it.id).sessionId ?: return

                println("Iterating: $sessionId")

                if (sessionId != session.id) {

                    sessionRepository.findById(
                        sessionId
                    ).sendMessage(responseMessage)
                }
            }
    }

    private fun routeMessage(message: TextMessage, sessionId: String) : WebSocketMessage {

        val request = objectMapper.readValue(
            message.payload,
            WebSocketRequest::class.java
        )

        if (request.type == WebSocketMessageType.PING)
            return SimpleResponse(WebSocketMessageType.PONG)

        return when (request) {
            is VoteRequest -> ParticipantVotedResponse(
                participantId = service.vote(request.vote, sessionId)
            )
            else -> throw RuntimeException("Invalid request type: ${request.type}")
        }
    }
}