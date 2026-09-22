package com.example.planning_poker_room.websocket

import com.example.planning_poker_room.store.repository.RoomRepository
import com.example.planning_poker_room.store.repository.SessionRepository
import com.example.planning_poker_room.websocket.dto.SimpleResponse
import com.example.planning_poker_room.websocket.dto.UserRequest
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
    private val repository: SessionRepository,
    private val roomRepository: RoomRepository,
    private val service: SocketService,
    private val objectMapper: ObjectMapper
) : TextWebSocketHandler() {

    override fun afterConnectionEstablished(session: WebSocketSession) {
        repository.save(session)

        println("CONNECTED ${session.id}")

        val roomId: UUID? = session.attributes["roomId"] as UUID?
        val participantId: UUID? = session.attributes["participantId"] as UUID?

        service.joinRoom(
            roomId,
            participantId,
            session.id
        )

        repository
            .findAllExceptForById(session.id)
            .forEach {
                it.sendMessage(TextMessage("${session.id} joined."))
            }
    }

    override fun handleTextMessage(
        session: WebSocketSession,
        message: TextMessage
    ) {
        println("${session.id} -> ${message.payload}")

        val request = objectMapper.readValue(message.payload, UserRequest::class.java)

        val response = objectMapper.writeValueAsString(routeMessage(request))

        // find a room and sen to it's participants
        repository
            .findAllExceptForById(session.id)
            .forEach {
                it.sendMessage(TextMessage(response))
            }
    }

    override fun afterConnectionClosed(
        session: WebSocketSession,
        status: CloseStatus
    ) {
        repository.remove(session)

        println("DISCONNECTED ${session.id}: $status")

        repository
            .findAll()
            .forEach {
                it.sendMessage(TextMessage("${session.id} left."))
            }
    }

    private fun routeMessage(request: UserRequest) : WebSocketMessage {
        when (request.type) {
            WebSocketMessageType.PING -> return SimpleResponse(WebSocketMessageType.PONG)
            else -> throw RuntimeException("Invalid request type: ${request.type}")
        }
    }
}