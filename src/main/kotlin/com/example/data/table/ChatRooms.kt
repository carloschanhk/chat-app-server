package com.example.data.table

import com.example.data.model.ChatRoom
import com.example.data.model.Message
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.*

object ChatRooms : IntIdTable() {
    val title: Column<String?> = varchar("title", 50).nullable()
    val createdAt: Column<Long> = long("createdAt")

    fun toChatRoom(row: ResultRow, message: Message? = null): ChatRoom {
        return ChatRoom(
            id = row[id].value,
            title = row[title],
            createdAt = row[createdAt],
            lastMessage = message
        )
    }
}