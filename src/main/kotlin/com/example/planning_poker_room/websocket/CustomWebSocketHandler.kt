package com.example.planning_poker_room.websocket

import com.example.planning_poker_room.api.service.RoomService
import com.example.planning_poker_room.exception.unit.ParticipantNotFoundException
import com.example.planning_poker_room.store.repository.ConnectionRepository
import com.example.planning_poker_room.store.repository.SessionRepository
import com.example.planning_poker_room.websocket.dto.response.ThinParticipantDto
import com.example.planning_poker_room.websocket.dto.response.JoinRoomResponse
import com.example.planning_poker_room.websocket.dto.response.LeftRoomResponse
import com.example.planning_poker_room.websocket.dto.response.RoomStateResponse
import com.example.planning_poker_room.websocket.exception.ExceptionFilterChain
import com.example.planning_poker_room.websocket.exception.output.FilterResult
import com.example.planning_poker_room.websocket.service.CURRENT_VERSION
import com.example.planning_poker_room.websocket.service.WebSocketService
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Component
import org.springframework.web.socket.CloseStatus
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.handler.TextWebSocketHandler
import tools.jackson.databind.ObjectMapper
import java.util.UUID

private val logger = KotlinLogging.logger {}

@Component
class CustomWebSocketHandler(
    private val connectionRepository: ConnectionRepository,
    private val sessionRepository: SessionRepository,
    private val objectMapper: ObjectMapper,
    private val roomService: RoomService,
    private val service: WebSocketService,
    private val router: RequestRouter,
    private val sender: MessageSender,
    private val exceptionHandler: ExceptionFilterChain
) : TextWebSocketHandler() {

    override fun afterConnectionEstablished(session: WebSocketSession) {
        sessionRepository.save(session)

        logger.debug { "CONNECTED ${session.id}" }

        val roomId: UUID = extractAttributeByName(session, "roomId")
        val participantId: UUID = extractAttributeByName(session, "participantId")

        val filterResult = exceptionHandler.execute {
            val room = service.joinRoom(
                roomId = roomId,
                participantId = participantId,
                sessionId = session.id
            )

            val currentParticipant = room.findParticipantById(participantId)
                ?: throw ParticipantNotFoundException("No participant with id: $participantId found.")

            JoinRoomResponse(
                version = CURRENT_VERSION,
                participant = ThinParticipantDto(
                    id = currentParticipant.id,
                    name = currentParticipant.name.value
                ))
        }

        when (filterResult) {
            is FilterResult.Success -> {

                val payload = objectMapper.writeValueAsString(filterResult.value)

                sender.broadcast(
                    roomId,
                    payload
                )

                val roomPhasePayload = objectMapper.writeValueAsString(
                    RoomStateResponse.of(roomService.getRoomById(roomId))
                )

                sender.unicastSender(
                    session.id,
                    roomPhasePayload
                )
            }
            is FilterResult.Handled -> {

                val payload = objectMapper.writeValueAsString(filterResult.response)

                sender.unicastSender(
                    session.id,
                    payload
                )

                if(filterResult.closeStatus != null)
                    session.close(filterResult.closeStatus)
            }
        }

    }

    override fun handleTextMessage(
        session: WebSocketSession,
        message: TextMessage
    ) {
        logger.debug { ("${session.id} -> ${message.payload}") }

        var responseMode: ResponseMode = ResponseMode.UNICAST_SENDER

        val connection = connectionRepository.findBySessionId(session.id)

        val filterResult = exceptionHandler.execute {
            val typeResponsePair = router.convertAndRoute(message, session.id)
            responseMode = typeResponsePair.first
            typeResponsePair.second
        }

        when (filterResult) {
            is FilterResult.Success -> {

                val payload = objectMapper.writeValueAsString(filterResult.value)

                sender.sendBasedOnResponseMode(
                    responseMode,
                    connection.roomId,
                    session.id,
                    payload
                )
            }
            is FilterResult.Handled -> {

                val payload = objectMapper.writeValueAsString(filterResult.response)

                sender.unicastSender(
                    session.id,
                    payload
                )

                if(filterResult.closeStatus != null)
                    session.close(filterResult.closeStatus)
            }
        }
    }

    override fun afterConnectionClosed(
        session: WebSocketSession,
        status: CloseStatus
    ) {
        sessionRepository.remove(session)

        logger.debug { "DISCONNECTED ${session.id}: $status" }

        val roomAndParticipant = service.leaveRoom(session.id)
        val roomId = roomAndParticipant.first
        val participantId = roomAndParticipant.second

        val response = LeftRoomResponse(
            participantId = participantId,
            version = CURRENT_VERSION
        )

        val jsonResponse = objectMapper.writeValueAsString(response)

        sender.multicastExceptSender(
            roomId,
            session.id,
            jsonResponse
        )
    }

    private fun extractAttributeByName(session: WebSocketSession, name: String): UUID =
        UUID.fromString(session.attributes[name] as String?).apply {
            logger.debug { "Extracted attribute $name: $this" }
        }
}