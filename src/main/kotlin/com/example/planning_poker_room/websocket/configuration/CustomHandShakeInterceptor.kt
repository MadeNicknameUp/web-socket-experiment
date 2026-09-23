package com.example.planning_poker_room.websocket.configuration

import org.springframework.http.server.ServerHttpRequest
import org.springframework.http.server.ServerHttpResponse
import org.springframework.http.server.ServletServerHttpRequest
import org.springframework.web.socket.WebSocketHandler
import org.springframework.web.socket.server.HandshakeInterceptor
import java.lang.Exception

class CustomHandShakeInterceptor : HandshakeInterceptor {

    override fun beforeHandshake(
        request: ServerHttpRequest,
        response: ServerHttpResponse,
        wsHandler: WebSocketHandler,
        attributes: MutableMap<String, Any>
    ): Boolean {

        if (request is ServletServerHttpRequest) {
            val httpRequest = request.servletRequest

            val path: String = httpRequest.requestURI

            println("PATH: $path")

            val roomId = path.substringAfterLast("/")
            val participantId = httpRequest.getParameter("participantId")

            attributes["roomId"] = roomId
            attributes["participantId"] = participantId
        }

        return true
    }

    override fun afterHandshake(
        request: ServerHttpRequest,
        response: ServerHttpResponse,
        wsHandler: WebSocketHandler,
        exception: Exception?
    ) {
        return;
    }
}