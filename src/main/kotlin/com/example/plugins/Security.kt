package com.example.plugins

import com.example.data.request.ChatSession
import io.ktor.application.*
import io.ktor.sessions.*
import io.ktor.util.*

fun Application.configureSecurity() {

    install(Sessions) {
        cookie<ChatSession>("SESSION")
    }

    intercept(ApplicationCallPipeline.Features) {
        if (call.sessions.get<ChatSession>() == null) {
            call.parameters["userId"]?.toInt()?.let {
                call.sessions.set(ChatSession(it, generateNonce()))
            }
        }
    }
}
