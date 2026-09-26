package com.example.planning_poker_room.websocket.dto.response

import java.util.concurrent.atomic.AtomicLong

interface RoomStateChangingEvent {
    val version: AtomicLong
}