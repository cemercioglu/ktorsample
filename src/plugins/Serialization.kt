package com.cem.ktorsample.plugins

import io.ktor.application.*
import io.ktor.features.*
import io.ktor.gson.*
import java.text.DateFormat

fun Application.configureSerialization() {
    install(ContentNegotiation) {
        gson {
            setDateFormat(DateFormat.LONG)
            setPrettyPrinting()
        }
    }
}