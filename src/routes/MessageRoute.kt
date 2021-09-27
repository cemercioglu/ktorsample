package com.cem.ktorsample.routes

//import com.cem.ktorsample.data.db.dao
import com.cem.ktorsample.data.db.dao.UsersDao
import com.cem.ktorsample.data.db.dao.allMessages
import com.cem.ktorsample.data.db.dao.insertMessage
import com.cem.ktorsample.data.model.MessageModel
import com.cem.ktorsample.data.model.NewMessageModel
import com.cem.ktorsample.data.model.UserModel
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*


fun Route.messages() {
    get("/messages") {
        call.respond(allMessages())
    }
    get("/messages/{id}") {
        val id = call.parameters["id"]?.toLong() ?: return@get call.respondText(
            "Missing or malformed id",
            status = HttpStatusCode.BadRequest
        )
        val customer =
            allMessages().find { it.messageId == id } ?: return@get call.respondText(
                "No message with id:$id",
                status = HttpStatusCode.NotFound
            )
        call.respond(customer)

    }
    post("/messages/new") {
        val message = call.receive<NewMessageModel>()
        call.respond(
            insertMessage(message) ?: return@post call.respondText(
                "Unknown error!",
                status = HttpStatusCode.BadRequest
            )
        )
    }

}