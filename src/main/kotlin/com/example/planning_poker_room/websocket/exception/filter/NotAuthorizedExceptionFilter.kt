package com.example.planning_poker_room.websocket.exception.filter

import com.example.planning_poker_room.exception.unit.NotAuthorizedException
import com.example.planning_poker_room.websocket.dto.WebSocketMessageType
import com.example.planning_poker_room.websocket.dto.response.exception.ExceptionResponse
import com.example.planning_poker_room.websocket.exception.output.FilterResult
import org.springframework.stereotype.Component

@Component
class NotAuthorizedExceptionFilter : ExceptionFilter {

    override fun <T> execute(
        next: () -> FilterResult<T>
    ): FilterResult<T> {

        return try {
            next()
        } catch (exception: NotAuthorizedException) {

            FilterResult.Handled(
                response = ExceptionResponse(
                    type = WebSocketMessageType.NOT_HOST,
                    message = exception.message,
                    code = 403
                )
            )
        }
    }

}