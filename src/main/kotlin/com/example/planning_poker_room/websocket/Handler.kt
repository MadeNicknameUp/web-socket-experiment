package com.example.planning_poker_room.websocket

import com.example.planning_poker_room.exception.unit.ConnectionNotFoundException
import com.example.planning_poker_room.exception.unit.NotFoundException
import com.example.planning_poker_room.api.service.RoomService
import com.example.planning_poker_room.exception.unit.ConnectionAlreadyExistsException
import com.example.planning_poker_room.exception.unit.InvalidMessageException
import com.example.planning_poker_room.exception.unit.InvalidVoteException
import com.example.planning_poker_room.exception.unit.ParticipantNotFoundException
import com.example.planning_poker_room.exception.unit.RoomNotFoundException
import com.example.planning_poker_room.exception.unit.RoomNotInVotingPhaseException
import com.example.planning_poker_room.store.repository.ConnectionRepository
import com.example.planning_poker_room.store.repository.SessionRepository
import com.example.planning_poker_room.websocket.dto.response.ThinParticipantDto
import com.example.planning_poker_room.websocket.dto.response.JoinRoomResponse
import com.example.planning_poker_room.websocket.dto.response.LeftRoomResponse
import com.example.planning_poker_room.websocket.dto.response.ParticipantVotedResponse
import com.example.planning_poker_room.websocket.dto.request.ResetRoundRequest
import com.example.planning_poker_room.websocket.dto.request.RevealRequest
import com.example.planning_poker_room.websocket.dto.request.RoomStateRequest
import com.example.planning_poker_room.websocket.dto.response.RoomStateResponse
import com.example.planning_poker_room.websocket.dto.response.RoundResetResponse
import com.example.planning_poker_room.websocket.dto.response.RoundRevealedResponse
import com.example.planning_poker_room.websocket.dto.response.SimpleResponse
import com.example.planning_poker_room.websocket.dto.request.WebSocketRequest
import com.example.planning_poker_room.websocket.dto.request.VoteRequest
import com.example.planning_poker_room.websocket.dto.WebSocketMessage
import com.example.planning_poker_room.websocket.dto.WebSocketMessageType
import com.example.planning_poker_room.websocket.dto.response.exception.ExceptionResponse
import com.example.planning_poker_room.websocket.service.CURRENT_VERSION
import com.example.planning_poker_room.websocket.service.SocketService
import org.springframework.stereotype.Component
import org.springframework.web.socket.CloseStatus
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.handler.TextWebSocketHandler
import tools.jackson.databind.ObjectMapper
import tools.jackson.databind.exc.InvalidTypeIdException
import java.util.UUID

// TODO: This class is clearly doing way too much.
// TODO: Externalize this try...catch blocks as middleware/proxy.
// TODO: Replace and improve logging. (Slf4j)
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

        val roomId: UUID = extractAttributeByName(session, "roomId")
        val participantId: UUID = extractAttributeByName(session, "participantId")

        try {
            val room = service.joinRoom(
                roomId = roomId,
                participantId = participantId,
                sessionId = session.id
            )

            val connection = connectionRepository.findBySessionId(session.id)

            val currentParticipant = room.participants.find { it.id == connection.participantId }
                ?: throw ParticipantNotFoundException("No participant with id: $participantId found.")

            val jsonJoinRoomResponse = objectMapper.writeValueAsString(JoinRoomResponse(
                version = CURRENT_VERSION,
                participant = ThinParticipantDto(
                    id = currentParticipant.id,
                    name = currentParticipant.name.value
                )))

            val jsonRoomStateResponse = objectMapper.writeValueAsString(RoomStateResponse.of(room))

            broadcast(room.id, jsonJoinRoomResponse)
            unicastSender(session.id, jsonRoomStateResponse)
        } catch (exception: ConnectionAlreadyExistsException) {

            val response = ExceptionResponse(
                type = WebSocketMessageType.ALREADY_CONNECTED,
                message = exception.message,
                code = 400
            )
            val jsonResponse = objectMapper.writeValueAsString(response)

            unicastSender(session.id, jsonResponse)
            session.close(CloseStatus.POLICY_VIOLATION)
        } catch (exception: IllegalArgumentException) {
            val response = ExceptionResponse(
                type = WebSocketMessageType.INVALID_ARGUMENT,
                message = exception.message ?: "Invalid data passed.",
                code = 400
            )
            val jsonResponse = objectMapper.writeValueAsString(response)

            unicastSender(session.id, jsonResponse)
            session.close(CloseStatus.BAD_DATA)
        }

    }

    override fun handleTextMessage(
        session: WebSocketSession,
        message: TextMessage
    ) {
        println("${session.id} -> ${message.payload}")

        var jsonResponse: String?
        var responseMode: ResponseMode = ResponseMode.UNICAST_SENDER

        try {
            val typeResponsePair = routeMessage(message, session.id)
            responseMode = typeResponsePair.first
            val response = typeResponsePair.second
            jsonResponse = objectMapper.writeValueAsString(response)
        } catch (exception: NotFoundException) {
            val response = ExceptionResponse(
                type = when (exception) {
                    is ParticipantNotFoundException -> WebSocketMessageType.PARTICIPANT_NOT_FOUND
                    is ConnectionNotFoundException -> WebSocketMessageType.CONNECTION_NOT_FOUND
                    is RoomNotFoundException -> WebSocketMessageType.ROOM_NOT_FOUND
                    else -> WebSocketMessageType.UNKNOWN_EXCEPTION
                },
                code = 404,
                message = exception.message
            )
            jsonResponse = objectMapper.writeValueAsString(response)
        } catch (exception: IllegalArgumentException) {
            val response = ExceptionResponse(
                type = WebSocketMessageType.INVALID_ARGUMENT,
                code = 400,
                message = exception.message ?: "Invalid request data."
            )
            jsonResponse = objectMapper.writeValueAsString(response)
        } catch (exception: InvalidVoteException) {
            val response = ExceptionResponse(
                type = WebSocketMessageType.INVALID_VOTE,
                code = 400,
                message = exception.message
            )
            jsonResponse = objectMapper.writeValueAsString(response)
        } catch (exception: RoomNotInVotingPhaseException) {
            val response = ExceptionResponse(
                type = WebSocketMessageType.ROOM_NOT_IN_VOTING_PHASE,
                code = 400,
                message = exception.message
            )
            jsonResponse = objectMapper.writeValueAsString(response)
        } catch (_: InvalidTypeIdException) {
            val response = ExceptionResponse(
                type = WebSocketMessageType.INVALID_MESSAGE,
                code = 400,
                message = "Invalid message semantics."
            )
            jsonResponse = objectMapper.writeValueAsString(response)
        }

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

        val roomAndParticipant = service.leaveRoom(session.id)
        val roomId = roomAndParticipant.first
        val participantId = roomAndParticipant.second

        val response = LeftRoomResponse(
            participantId = participantId,
            version = CURRENT_VERSION
        )

        val jsonResponse = objectMapper.writeValueAsString(response)

        multicastExceptSender(roomId, session.id,jsonResponse)
    }

    // TODO: Should this be owned by a router?
    private fun routeMessage(message: TextMessage, sessionId: String) : Pair<ResponseMode, WebSocketMessage> {

        val request = objectMapper.readValue(
            message.payload,
            WebSocketRequest::class.java
        )

        if (request.type == WebSocketMessageType.PING)
            return Pair(ResponseMode.UNICAST_SENDER, SimpleResponse(WebSocketMessageType.PONG))

        return when (request) {
            is VoteRequest -> Pair(ResponseMode.MULTICAST_EXCEPT_SENDER, ParticipantVotedResponse(
                version = CURRENT_VERSION,
                participantId = service.vote(request.vote, sessionId)
            ))
            is RevealRequest -> {
                service.reveal(sessionId)
                return Pair(ResponseMode.BROADCAST, RoundRevealedResponse(
                    type = WebSocketMessageType.ROUND_REVEALED,
                    version = CURRENT_VERSION,
                    votes = roomService.getRoomById(connectionRepository.findBySessionId(sessionId).roomId).participants.associate {
                        Pair(
                            it.id,
                            it.vote
                        )
                    }
                ))
            }
            is ResetRoundRequest -> {
                service.roundReset(sessionId)
                return Pair(ResponseMode.BROADCAST, RoundResetResponse(
                    version = CURRENT_VERSION
                ))
            }
            is RoomStateRequest ->
                Pair(
                    ResponseMode.UNICAST_SENDER,
                    RoomStateResponse.of(service.findRoomBySessionId(sessionId))
                )
            else -> throw InvalidMessageException("Invalid request type: ${request.type}")
        }
    }

    // TODO: This stuff has to move out of the class.
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

    private fun extractAttributeByName(session: WebSocketSession, name: String): UUID =
        UUID.fromString(session.attributes[name] as String?).apply {
            println("Extracted attribute $name: $this")
        }
}