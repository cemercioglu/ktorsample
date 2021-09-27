package com.cem.ktorsample.plugins

import com.cem.ktorsample.routes.messagesWebSocket
import io.ktor.application.*
import io.ktor.http.cio.websocket.*
import java.time.Duration

fun Application.configureWebSocket() {
    install(io.ktor.websocket.WebSockets) {
        pingPeriod = Duration.ofSeconds(15)
        timeout = Duration.ofSeconds(15)
        maxFrameSize = Long.MAX_VALUE
        masking = false

    }
    messagesWebSocket()

}