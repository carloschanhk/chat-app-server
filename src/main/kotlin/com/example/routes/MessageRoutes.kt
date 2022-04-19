package com.example.routes

import com.example.controller.MessageController
import com.example.data.request.ChatSession
import com.example.data.request.SendMessageRequest
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.http.cio.websocket.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.sessions.*
import io.ktor.websocket.*
import kotlinx.coroutines.channels.consumeEach
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

fun Route.messageRoutes(messageController: MessageController) {
    // Need to connect to websocket for sending message
    route("/messages"){
        get{
            try {
                val result = messageController.getMessagesByRoom(call.parameters)
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
        webSocket {
            val session = call.sessions.get<ChatSession>()
            if(session == null) {
                close(CloseReason(CloseReason.Codes.VIOLATED_POLICY, "No session."))
                return@webSocket
            }
            try {
                messageController.onJoin(session.userId, this)
                incoming.consumeEach {
                    if (it is Frame.Text){
                        val request = Json.decodeFromString<SendMessageRequest>(it.readText())
                        messageController.sendMessage(request)
                    }
                }
            } catch (e: Exception){
                e.printStackTrace()
            } finally {
                messageController.disconnect(session.userId)
            }
        }
    }
}