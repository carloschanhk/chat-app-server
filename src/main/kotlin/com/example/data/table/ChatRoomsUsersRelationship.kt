package com.example.data.table

import org.jetbrains.exposed.sql.*

object ChatRoomsUsersRelationship : Table() {
    val userId = reference("user_id", Users).index()
    val chatroomId = reference("chatroom_id", ChatRooms).index()
    override val primaryKey: PrimaryKey = PrimaryKey(userId, chatroomId)
}