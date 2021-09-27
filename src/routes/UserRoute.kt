package com.cem.ktorsample.routes

import com.cem.ktorsample.data.db.dao.UsersDao.allUsers
import com.cem.ktorsample.data.db.dao.UsersDao.insertUser
import com.cem.ktorsample.data.model.UserModel
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*

fun Route.users() {
    get("/users") {
        call.respond(allUsers())
    }
    post("/users/new") {
        val user = call.receive<UserModel>()
        call.respond(
            insertUser(user) ?: return@post call.respondText(
                "user already exists",
                status = HttpStatusCode.Conflict
            )
        )
    }

}