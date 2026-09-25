package com.example.planning_poker_room.websocket.dto.request

import com.example.planning_poker_room.websocket.dto.WebSocketMessageType
import com.fasterxml.jackson.annotation.JsonTypeName

@JsonTypeName("ROOM_STATE")
class RoomStateRequest : WebSocketRequest(WebSocketMessageType.ROOM_STATE)