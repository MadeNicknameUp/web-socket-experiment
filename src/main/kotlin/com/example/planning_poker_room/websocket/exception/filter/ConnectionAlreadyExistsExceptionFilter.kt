package com.example.planning_poker_room.websocket.exception.filter

import com.example.planning_poker_room.exception.unit.ConnectionAlreadyExistsException
import com.example.planning_poker_room.websocket.dto.WebSocketMessageType
import com.example.planning_poker_room.websocket.dto.response.exception.ExceptionResponse
import com.example.planning_poker_room.websocket.exception.output.FilterResult
import org.springframework.stereotype.Component
import org.springframework.web.socket.CloseStatus

@Component
class ConnectionAlreadyExistsExceptionFilter : ExceptionFilter {

    override fun <T> execute(
        next: () -> FilterResult<T>
    ): FilterResult<T> {

        return try {
            next()
        } catch (exception: ConnectionAlreadyExistsException) {

            FilterResult.Handled(
                response = ExceptionResponse(
                    type = WebSocketMessageType.ALREADY_CONNECTED,
                    message = exception.message,
                    code = 400
                ),
                closeStatus = CloseStatus.NOT_ACCEPTABLE
            )
        }
    }

}