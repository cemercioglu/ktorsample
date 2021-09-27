package com.cem.ktorsample

import com.cem.ktorsample.data.model.UserModel
import io.ktor.http.cio.websocket.*
import java.util.concurrent.atomic.AtomicInteger

class Connection(val session: DefaultWebSocketSession) {
    companion object {
        var lastId = AtomicInteger(0)
    }
    val name = "user${lastId.getAndIncrement()}"

}
class ConnectionUser(val session: DefaultWebSocketSession, private val userModel: UserModel?) {
    companion object {
        var lastId = AtomicInteger(0)

    }
    val name = "user-${userModel?.email}-${lastId.getAndIncrement()}"
}
