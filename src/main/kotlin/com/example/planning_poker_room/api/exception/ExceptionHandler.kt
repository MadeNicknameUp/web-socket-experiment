package com.example.planning_poker_room.api.exception

import com.example.planning_poker_room.api.exception.dto.RestErrorDto
import com.example.planning_poker_room.exception.unit.ConnectionAlreadyExistsException
import com.example.planning_poker_room.exception.unit.ConnectionNotFoundException
import com.example.planning_poker_room.exception.unit.ParticipantNotFoundException
import com.example.planning_poker_room.exception.unit.RoomNotFoundException
import com.example.planning_poker_room.exception.unit.SessionNotFoundException
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class ExceptionHandler {

    @ExceptionHandler(value = [
        ParticipantNotFoundException::class,
        RoomNotFoundException::class,
        SessionNotFoundException::class,
        ConnectionNotFoundException::class
    ])
    fun handleNotFoundException(
        request: HttpServletRequest,
        exception: RuntimeException
    ) : ResponseEntity<RestErrorDto> {

        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(RestErrorDto(
            code = HttpStatus.NOT_FOUND.value(),
            message = exception.message,
            path = request.servletPath
        ))
    }

    @ExceptionHandler(value = [
        IllegalArgumentException::class,
        IllegalStateException::class,
        ConnectionAlreadyExistsException::class,
    ])
    fun handleBadRequestException(
        request: HttpServletRequest,
        exception: RuntimeException
    ) : ResponseEntity<RestErrorDto> {

        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(RestErrorDto(
                code = HttpStatus.BAD_REQUEST.value(),
                message = exception.message,
                path = request.servletPath
            ))
    }
}