package com.example.planning_poker_room.websocket.dto.request

import com.example.planning_poker_room.websocket.dto.WebSocketMessageType
import com.fasterxml.jackson.annotation.JsonTypeName

@JsonTypeName("ROUND_RESET")
class ResetRoundRequest : WebSocketRequest(WebSocketMessageType.ROUND_RESET)