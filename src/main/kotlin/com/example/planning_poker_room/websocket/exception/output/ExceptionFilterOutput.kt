package com.example.planning_poker_room.websocket.exception.output

import com.example.planning_poker_room.websocket.dto.WebSocketMessage
import org.springframework.web.socket.CloseStatus

sealed interface FilterResult<out T> {

    data class Success<T>(
        val value: T
    ) : FilterResult<T>

    data class Handled(
        val response: WebSocketMessage,
        val closeStatus: CloseStatus? = null
    ) : FilterResult<Nothing>
}
