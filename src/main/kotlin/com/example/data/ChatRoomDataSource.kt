package com.example.data

import com.example.data.model.ChatRoom
import com.example.data.model.Message
import com.example.data.table.ChatRooms
import com.example.data.table.ChatRoomsUsersRelationship
import com.example.data.table.Messages
import com.example.data.table.Users
import com.example.util.Response
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

class ChatRoomDataSource {
    fun createChatRoom(userId: Int, partnerId: Int): Response<ChatRoom> {
        return try {
            // Create room
            val chatRoom = transaction {
                ChatRooms.insert {
                    it[createdAt] = System.currentTimeMillis()
                }.resultedValues!!.map { ChatRooms.toChatRoom(it) }
            }.first()

            // Add to relation table
            transaction {
                ChatRoomsUsersRelationship.insert {
                    it[ChatRoomsUsersRelationship.userId] = userId
                    it[chatroomId] = chatRoom.id
                }
                ChatRoomsUsersRelationship.insert {
                    it[ChatRoomsUsersRelationship.userId] = partnerId
                    it[chatroomId] = chatRoom.id
                }
            }
            Response.Success(chatRoom)
        } catch (e: Exception) {
            Response.Error(e)
        }
    }

    fun getChatRoomsByUser(userId: Int): Response<List<ChatRoom>> {
        return try {
            val chatRooms: List<ChatRoom> = transaction {
                val messagesA = Messages.alias("MessagesA")
                val messagesB = Messages.alias("MessagesB")
                ChatRooms
                    .join(
                        ChatRoomsUsersRelationship,
                        JoinType.INNER,
                        additionalConstraint = {
                            (ChatRoomsUsersRelationship.userId eq userId) and (ChatRoomsUsersRelationship.chatroomId eq ChatRooms.id)
                        },
                    )
//                    .join(
//                        messagesA, JoinType.INNER, additionalConstraint = { messagesA[Messages.roomId] eq ChatRooms.id }
//                    ).join(messagesB, JoinType.LEFT, additionalConstraint = {
//                        messagesB[Messages.roomId] eq ChatRooms.id and (messagesA[Messages.createdAt] less messagesB[Messages.createdAt])
//                    })
//                    .select { messagesB[Messages.id] eq null }
                    .selectAll()
                    .map {
//                        val message = Message(
//                            id = it[messagesA[Messages.id]].value,
//                            userId = it[messagesA[Messages.id]].value,
//                            roomId = it[messagesA[Messages.roomId]].value,
//                            text = it[messagesA[Messages.text]],
//                            createdAt = it[messagesA[Messages.createdAt]],
//                            isRead = it[messagesA[Messages.isRead]],
//                        )
                        ChatRooms.toChatRoom(it)
                    }
            }
            Response.Success(chatRooms)
        } catch (e: Exception) {
            Response.Error(e)
        }
    }
}