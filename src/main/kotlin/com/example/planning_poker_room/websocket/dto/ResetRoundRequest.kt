package com.example.planning_poker_room.websocket.dto

import com.fasterxml.jackson.annotation.JsonTypeName

@JsonTypeName("ROUND_RESET")
class ResetRoundRequest : WebSocketRequest(WebSocketMessageType.ROUND_RESET)