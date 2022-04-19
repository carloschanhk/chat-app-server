package com.example.controller

import com.example.data.ChatRoomDataSource
import com.example.data.MessageDataSource
import com.example.data.UserDataSource
import com.example.data.model.ChatRoom
import com.example.data.request.CreateRoomRequest
import com.example.util.Response
import com.example.util.exception.MissingParameterException
import io.ktor.http.*

class ChatController(
    private val chatRoomDataSource: ChatRoomDataSource,
    private val userDataSource: UserDataSource,
    private val messageDataSource: MessageDataSource,
) {
    fun createRoom(createRoomRequest: CreateRoomRequest): Response<ChatRoom> {
        return try {
            chatRoomDataSource.createChatRoom(createRoomRequest.userId, createRoomRequest.partnerId)
        } catch (e: Exception) {
            Response.Error(e)
        }
    }

    fun getChatRoomsByUser(queryParameters: Parameters): Response<List<ChatRoom>> {
        return try {
            val userId: Int = queryParameters["userId"]?.toInt() ?: throw MissingParameterException()
            val result = chatRoomDataSource.getChatRoomsByUser(userId)
            val chatRooms = result.takeIfSuccess()?.let {
                it.map { room ->
                    val chatroomUsers = userDataSource.getUsersByChatRoom(room.id)
                    val lastMessage = messageDataSource.getMessagesFromRoom(room.id, limit = 1)
                    room.copy(
                        participants = chatroomUsers.takeIfSuccess() ?: throw chatroomUsers.takeError(),
                        lastMessage = lastMessage.takeIfSuccess()?.first() ?: throw lastMessage.takeError(),
                    )
                }
            } ?: throw result.takeError()
            Response.Success(chatRooms)
        } catch (e: Exception) {
            Response.Error(e)
        }
    }
}