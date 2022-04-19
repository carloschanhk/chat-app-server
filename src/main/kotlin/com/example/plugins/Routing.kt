package com.example.plugins

import com.example.controller.ChatController
import com.example.controller.MessageController
import com.example.controller.UserController
import com.example.data.request.CreateRoomRequest
import com.example.data.request.SendMessageRequest
import com.example.data.request.SignUpRequest
import com.example.data.table.ChatRooms
import com.example.data.table.ChatRoomsUsersRelationship
import com.example.data.table.Messages
import com.example.data.table.Users
import com.example.routes.chatRoutes
import com.example.routes.messageRoutes
import com.example.routes.userRoute
import io.ktor.application.*
import io.ktor.response.*
import io.ktor.routing.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.koin.ktor.ext.inject

fun Application.configureRouting() {
    Database.connect("jdbc:h2:mem:regular;DB_CLOSE_DELAY=-1;", "org.h2.Driver")
    transaction {
        SchemaUtils.create(Users, ChatRooms, Messages, ChatRoomsUsersRelationship)
    }
    val userController by inject<UserController>()
    val chatController by inject<ChatController>()
    val messageController by inject<MessageController>()
    install(Routing) {
        userRoute(userController)
        chatRoutes(chatController)
        messageRoutes(messageController)
    }
}
