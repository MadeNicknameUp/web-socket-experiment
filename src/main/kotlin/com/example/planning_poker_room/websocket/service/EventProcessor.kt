package com.example.planning_poker_room.websocket.service

import org.springframework.stereotype.Service
import org.springframework.web.socket.TextMessage
import tools.jackson.databind.ObjectMapper

@Service
class EventProcessor(
    private val objectMapper: ObjectMapper
) {

    fun process(message: TextMessage): TextMessage {

        return message
    }
}