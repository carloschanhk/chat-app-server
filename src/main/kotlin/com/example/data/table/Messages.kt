package com.example.data.table

import com.example.data.model.Message
import com.example.data.model.User
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.*

object Messages : IntIdTable() {
    val userId = reference("user_id", Users).index()
    val roomId = reference("room_id", ChatRooms).index()
    val text: Column<String> = varchar("text", 255)
    val createdAt: Column<Long> = long("createdAt")
    val isRead: Column<Boolean> = bool("isRead").default(false)

    fun toMessage(row: ResultRow): Message {
        return Message(
            id = row[id].value,
            userId = row[Users.id].value,
            username = row[Users.name],
            roomId = row[roomId].value,
            text = row[text],
            createdAt = row[createdAt],
            isRead = row[isRead],
        )
    }

    fun toMessage(row: ResultRow,userId: Int, username: String): Message {
        return Message(
            id = row[id].value,
            userId = userId,
            username = username,
            roomId = row[roomId].value,
            text = row[text],
            createdAt = row[createdAt],
            isRead = row[isRead],
        )
    }
}