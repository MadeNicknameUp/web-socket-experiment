package com.example.planning_poker_room.websocket.exception

import com.example.planning_poker_room.websocket.exception.filter.ExceptionFilter
import com.example.planning_poker_room.websocket.exception.output.FilterResult
import org.springframework.stereotype.Component

@Component
class ExceptionFilterChain(
    private val filters: List<ExceptionFilter>
) {

    fun <T> execute(operation: () -> T): FilterResult<T> {

        fun next(index: Int): FilterResult<T> {

            if (index == filters.size)
                return FilterResult.Success(operation())

            return filters[index].execute {
                next(index + 1)
            }

        }

        return next(0)
    }

}