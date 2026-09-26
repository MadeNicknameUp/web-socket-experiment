package com.example.planning_poker_room.websocket.exception.filter

import com.example.planning_poker_room.exception.unit.InvalidVoteException
import com.example.planning_poker_room.websocket.dto.WebSocketMessageType
import com.example.planning_poker_room.websocket.dto.response.exception.ExceptionResponse
import com.example.planning_poker_room.websocket.exception.output.FilterResult
import org.springframework.stereotype.Component

@Component
class InvalidVoteExceptionFilter : ExceptionFilter {

    override fun <T> execute(
        next: () -> FilterResult<T>
    ): FilterResult<T> {

        return try {
            next()
        } catch (exception: InvalidVoteException) {

            FilterResult.Handled(
                response = ExceptionResponse(
                    type = WebSocketMessageType.INVALID_VOTE,
                    code = 400,
                    message = exception.message
                )
            )
        }
    }

}