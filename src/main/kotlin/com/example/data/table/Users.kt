package com.example.data.table

import com.example.data.model.User
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.*

object Users : IntIdTable() {
    val name: Column<String> = varchar("name", 50).uniqueIndex()
    val password: Column<String> = varchar("password", 50)
    val createdAt: Column<Long> = long("createdAt")

    fun toUser(row: ResultRow): User = User(
        id = row[id].value,
        name = row[name],
        createdAt = row[createdAt],
    )
}