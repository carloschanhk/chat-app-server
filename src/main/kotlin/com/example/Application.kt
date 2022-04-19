package com.example

import com.example.di.mainModule
import com.example.plugins.*
import io.ktor.application.*
import org.koin.ktor.ext.Koin

fun main(args: Array<String>): Unit =
    io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // application.conf references the main function. This annotation prevents the IDE from marking it as unused.
fun Application.module() {
    install(Koin){
        modules(mainModule)
    }
    configureSerialization()
    configureSockets()
    configureRouting()
    configureMonitoring()
    configureSecurity()
}