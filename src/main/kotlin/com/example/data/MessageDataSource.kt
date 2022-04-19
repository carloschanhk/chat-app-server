package com.example.data

import com.example.data.model.Message
import com.example.data.table.Messages
import com.example.data.table.Users
import com.example.util.Response
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

class MessageDataSource {

    fun addMessage(roomId: Int, userId: Int, content: String, username: String): Response<Message> {
        return try {
            // Add message
            val message = transaction {
                Messages.insert {
                    it[Messages.roomId] = roomId
                    it[Messages.userId] = userId
                    it[text] = content
                    it[createdAt] = System.currentTimeMillis()
                }.resultedValues!!.map {
                    Messages.toMessage(it, userId,username)
                }.first()
            }
            Response.Success(message)
        } catch (e: Exception) {
            Response.Error(e)
        }
    }

    fun getMessagesFromRoom(roomId: Int, offset: Long = 0, limit: Int = 100): Response<List<Message>> {
        return try {
            val messages = transaction {
                Messages.join(Users, JoinType.INNER, additionalConstraint = { Messages.userId eq Users.id })
                    .select {
                        Messages.roomId eq roomId
                    }.orderBy(Messages.createdAt to SortOrder.DESC)
                    .limit(limit, offset = offset)
                    .map { Messages.toMessage(it) }
            }
            Response.Success(messages)
        } catch (e: Exception) {
            Response.Error(e)
        }
    }
}