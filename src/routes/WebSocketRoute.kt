package com.cem.ktorsample.routes

import com.cem.ktorsample.ConnectionUser
import com.cem.ktorsample.data.db.dao.UsersDao
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.http.cio.websocket.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.websocket.*
import java.util.*


fun Application.messagesWebSocket() {
    val wsRoutePath = "/ws/chat/"
    routing {
        val connections = Collections.synchronizedSet<ConnectionUser?>(LinkedHashSet())
        webSocket("$wsRoutePath{userId}") {
            println("Adding user!")
            val id = call.parameters["userId"]?.toInt() ?: call.respondText(
                "Missing or malformed id",
                status = HttpStatusCode.BadRequest
            )
            val userModel = UsersDao.getUserById(id as Int)
            if (userModel != null) {
                val thisConnection = ConnectionUser(this, userModel)

                send(Frame.Text("${userModel.email} connected"))
                connections += thisConnection
                try {
                    send("You are connected! There are ${connections.count()} users here.")
                    for (frame in incoming) {
                        frame as? Frame.Text ?: continue
                        val receivedText = frame.readText()
                        val textWithUsername = "[${thisConnection.name}]: $receivedText"
                        connections.forEach {
                            it.session.send(textWithUsername)
                        }
                    }
                } catch (e: Exception) {
                    println(e.localizedMessage)
                } finally {
                    println("Removing $thisConnection!")
                    connections -= thisConnection
                }
            } else {
                val reason = CloseReason(CloseReason.Codes.CANNOT_ACCEPT, "User not found")
                this.close(reason)
                println("Connection Closed, Id:$id")
            }
        }
    }
}