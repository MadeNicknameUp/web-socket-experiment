package com.example.planning_poker_room.websocket.exception.filter

import com.example.planning_poker_room.exception.unit.FatalIllegalArgumentException
import com.example.planning_poker_room.websocket.dto.WebSocketMessageType
import com.example.planning_poker_room.websocket.dto.response.exception.ExceptionResponse
import com.example.planning_poker_room.websocket.exception.output.FilterResult
import org.springframework.stereotype.Component

@Component
class FatalIllegalArgumentExceptionFilter : ExceptionFilter {

    override fun <T> execute(
        next: () -> FilterResult<T>
    ): FilterResult<T> {

        return try {
            next()
        } catch (exception: FatalIllegalArgumentException) {

            FilterResult.Handled(
                response = ExceptionResponse(
                    type = WebSocketMessageType.INVALID_ARGUMENT,
                    message = exception.message,
                    code = 400
                )
            )
        }
    }

}