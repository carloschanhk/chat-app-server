package com.example.routes

import com.example.controller.ChatController
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.websocket.*

fun Route.chatRoutes(chatController: ChatController) {
    route("/chatroom") {
        get {
            try {
                val result = chatController.getChatRoomsByUser(call.parameters)
                result.takeIfSuccess()?.let {
                    call.respond(status = HttpStatusCode.OK, message = it)
                } ?: throw result.takeError()
            } catch (e: Exception) {
                call.respond(
                    status = HttpStatusCode.BadRequest,
                    message = e.message ?: "Something went wrong"
                )
            }
        }
        post("/create") {
            try {
                val result = chatController.createRoom(call.receive())
                result.takeIfSuccess()?.let {
                    call.respond(status = HttpStatusCode.Created, message = it)
                } ?: throw result.takeError()
            } catch (e: Exception) {
                call.respond(
                    status = HttpStatusCode.BadRequest,
                    message = e.message ?: "Something went wrong"
                )
            }
        }
    }
}