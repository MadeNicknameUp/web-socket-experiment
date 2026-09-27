package com.example.planning_poker_room.websocket.infrastructure

import com.example.planning_poker_room.store.repository.ConnectionRepository
import com.example.planning_poker_room.store.repository.SessionRepository
import org.springframework.stereotype.Component
import org.springframework.web.socket.TextMessage
import java.util.UUID

@Component
class MessageSender(
    private val connectionRepository: ConnectionRepository,
    private val sessionRepository: SessionRepository
) {

    fun broadcast(roomId: UUID, payload: String) {
        connectionRepository.findByRoomId(roomId)
            .map { it.sessionId }
            .forEach {

                if (it != null)
                    sessionRepository.findById(it).sendMessage(TextMessage(payload))

            }
    }

    fun unicastSender(sessionId: String, payload: String) {
        sessionRepository.findById(sessionId).sendMessage(TextMessage(payload))
    }

    fun multicastExceptSender(roomId: UUID, sessionId: String, payload: String) {
        connectionRepository.findByRoomId(roomId)
            .map { it.sessionId }
            .forEach {

                if (it != null && it != sessionId)
                    sessionRepository.findById(it).sendMessage(TextMessage(payload))

            }
    }

    fun sendBasedOnResponseMode(
        mode: ResponseMode,
        roomId: UUID,
        sessionId: String,
        payload: String
    ) {
        when (mode) {
            ResponseMode.BROADCAST -> broadcast(
                roomId,
                payload
            )
            ResponseMode.MULTICAST_EXCEPT_SENDER -> multicastExceptSender(
                roomId,
                sessionId,
                payload
            )
            ResponseMode.UNICAST_SENDER -> unicastSender(
                sessionId,
                payload
            )
        }
    }
}