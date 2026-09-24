package com.example.planning_poker_room.websocket.dto

import com.fasterxml.jackson.annotation.JsonTypeName

@JsonTypeName("REVEAL")
class RevealRequest : WebSocketRequest(WebSocketMessageType.REVEAL)
