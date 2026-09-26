package com.example.planning_poker_room.websocket.exception.filter

import com.example.planning_poker_room.exception.unit.ConnectionNotFoundException
import com.example.planning_poker_room.exception.unit.NotFoundException
import com.example.planning_poker_room.exception.unit.ParticipantNotFoundException
import com.example.planning_poker_room.exception.unit.RoomNotFoundException
import com.example.planning_poker_room.websocket.dto.WebSocketMessageType
import com.example.planning_poker_room.websocket.dto.response.exception.ExceptionResponse
import com.example.planning_poker_room.websocket.exception.output.FilterResult
import org.springframework.stereotype.Component
import org.springframework.web.socket.CloseStatus

@Component
class NotFoundExceptionFilter : ExceptionFilter {

    override fun <T> execute(
        next: () -> FilterResult<T>
    ): FilterResult<T> {

        return try {
            next()
        } catch (exception: NotFoundException) {

            FilterResult.Handled(
                response = ExceptionResponse(
                    type = when (exception) {
                        is ParticipantNotFoundException -> WebSocketMessageType.PARTICIPANT_NOT_FOUND
                        is ConnectionNotFoundException -> WebSocketMessageType.CONNECTION_NOT_FOUND
                        is RoomNotFoundException -> WebSocketMessageType.ROOM_NOT_FOUND
                        else -> WebSocketMessageType.UNKNOWN_EXCEPTION
                    },
                    code = 404,
                    message = exception.message
                ),
                closeStatus = CloseStatus.BAD_DATA
            )
        }
    }

}