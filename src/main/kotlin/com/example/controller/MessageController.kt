package com.example.controller

import com.example.data.MessageDataSource
import com.example.data.UserDataSource
import com.example.data.model.Message
import com.example.data.request.SendMessageRequest
import com.example.util.Response
import com.example.util.exception.MissingParameterException
import io.ktor.http.*
import io.ktor.http.cio.websocket.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.concurrent.ConcurrentHashMap

class MessageController(
    private val messageDataSource: MessageDataSource,
    private val userDataSource: UserDataSource,
) {
    private val onlineUsers = ConcurrentHashMap<Int, WebSocketSession>()

    suspend fun onJoin(userId: Int, socket: WebSocketSession) {
        // Close previous session in case there are leftover
        disconnect(userId)
        onlineUsers[userId] = socket
    }

    suspend fun disconnect(userId: Int) {
        onlineUsers.remove(userId)?.close()
    }

    fun getMessagesByRoom(queryParameters: Parameters): Response<List<Message>> {
        return try {
            val roomId = queryParameters["roomId"]?.toInt() ?: throw MissingParameterException()
            messageDataSource.getMessagesFromRoom(roomId)
        } catch (e: Exception) {
            Response.Error(e)
        }
    }

    suspend fun sendMessage(sendMessageRequest: SendMessageRequest) {
        try {
            val addedMessage = with(sendMessageRequest) {
                val user = userDataSource.findUserById(userId)
                messageDataSource.addMessage(
                    roomId,
                    userId,
                    content,
                    user.takeIfSuccess()?.name ?: throw user.takeError(),
                )
            }
            val chatroomUsers = userDataSource.getUsersByChatRoom(sendMessageRequest.roomId)

            addedMessage.takeIfSuccess()?.let { message ->
                chatroomUsers.takeIfSuccess()?.let { users ->
                    users.forEach {
                        val parsedMessage = Json.encodeToString(message)
                        onlineUsers[it.id]?.send(Frame.Text(parsedMessage))
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}