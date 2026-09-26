package com.example.planning_poker_room.websocket.exception.filter

import com.example.planning_poker_room.websocket.exception.output.FilterResult

interface ExceptionFilter {

    fun <T> execute(
        next: () -> FilterResult<T>
    ): FilterResult<T>

}