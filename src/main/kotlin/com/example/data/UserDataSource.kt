package com.example.data

import com.example.data.model.User
import com.example.data.table.ChatRoomsUsersRelationship
import com.example.data.table.Users
import com.example.util.Response
import com.example.util.exception.InvalidCredentialException
import org.jetbrains.exposed.exceptions.ExposedSQLException
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction

class UserDataSource {
    fun signUpUser(newName: String, newPassword: String): Response<User> {
        return try {
            val insertedUser: User = transaction {
                Users.insert {
                    it[name] = newName
                    it[password] = newPassword
                    it[createdAt] = System.currentTimeMillis()
                }.resultedValues!!.map {
                    Users.toUser(it)
                }.first()
            }
            Response.Success(insertedUser)
        } catch (e: Exception) {
            if (e is ExposedSQLException) {
                return Response.Error(java.lang.Exception("username is already used"))
            }
            Response.Error(e)
        }
    }

    fun findUserById(userId: Int): Response<User>{
        return try {
            val user = transaction {
                Users.select {
                    Users.id eq userId
                }.map { Users.toUser(it) }.first()
            }
            Response.Success(user)
        } catch (e: Exception){
            Response.Error(e)
        }
    }

     fun searchUserByName(username: String): Response<List<User>> {
        return try{
            val users = transaction {
                Users.select {
                    Users.name.lowerCase() like "%${username.lowercase()}%"
                }.map { Users.toUser(it) }
            }
            Response.Success(users)
        } catch (e: Exception){
            Response.Error(e)
        }
    }

     fun loginUser(username: String, password: String): Response<User> {
        return try {
            val users = transaction {
                Users.select {
                    (Users.name eq username) and (Users.password eq password)
                }.map { Users.toUser(it) }
            }
            if (users.isEmpty()) throw InvalidCredentialException()
            Response.Success(users.first())
        } catch (e: Exception) {
            Response.Error(e)
        }
    }

    fun getUsersByChatRoom(roomId: Int): Response<List<User>> {
        return try {
            val users: List<User> = transaction {
                Users.join(
                    ChatRoomsUsersRelationship,
                    JoinType.INNER,
                    additionalConstraint = {
                        (ChatRoomsUsersRelationship.userId eq Users.id) and (ChatRoomsUsersRelationship.chatroomId eq roomId)
                    },
                )
                    .selectAll()
                    .map { Users.toUser(it) }
            }
            Response.Success(users)
        } catch (e: Exception) {
            Response.Error(e)
        }
    }
}