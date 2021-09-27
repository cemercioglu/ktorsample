package com.cem.ktorsample.plugins

import com.cem.ktorsample.routes.messages
import com.cem.ktorsample.routes.users
import io.ktor.application.*
import io.ktor.http.content.*
import io.ktor.routing.*

fun Application.configureRouting() {
    routing {
        users()
        messages()
        // Static plugin. Try to access `/static/index.html`
        static {
            resources("static")
        }

    }
}