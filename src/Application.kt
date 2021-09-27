@file:Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")

package com.cem.ktorsample

import com.cem.ktorsample.data.db.dao.Messages
import com.cem.ktorsample.data.db.dao.Users
import com.cem.ktorsample.plugins.configureMonitoring
import com.cem.ktorsample.plugins.configureRouting
import com.cem.ktorsample.plugins.configureSerialization
import com.cem.ktorsample.plugins.configureWebSocket
import com.google.gson.Gson
import io.ktor.application.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.transactions.transaction


fun main(args: Array<String>): Unit =
    io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // application.conf references the main function. This annotation prevents the IDE from marking it as unused.
fun Application.module() {
    configureSerialization()
    configureRouting()
    configureWebSocket()
    configureMonitoring()

//    install(Compression)
//    install(CORS) {
//        anyHost()
//    }
//    install(DefaultHeaders)
//    install(CallLogging)

    initDB()
}


fun initDB() {
    val file = Application::class.java.getResource("/database.json").readText()
    val properties = Gson().fromJson(file.toString(), Properties::class.java)
    Database.connect(
        url = properties.url,
        driver = properties.driver,
        user = properties.userName,
        password = properties.pass,
    )
    transaction {
        addLogger(StdOutSqlLogger)
        SchemaUtils.create(Users, Messages)
    }
}

data class Properties(
    var driver: String,
    var url: String,
    var userName: String,
    var pass: String,


    )