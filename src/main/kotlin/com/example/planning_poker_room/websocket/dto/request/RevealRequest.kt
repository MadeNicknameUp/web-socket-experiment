package com.example.planning_poker_room.websocket.dto.request

import com.example.planning_poker_room.websocket.dto.WebSocketMessageType
import com.fasterxml.jackson.annotation.JsonTypeName

@JsonTypeName("REVEAL")
class RevealRequest : WebSocketRequest(WebSocketMessageType.REVEAL)
