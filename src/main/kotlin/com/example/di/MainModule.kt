package com.example.di

import com.example.controller.ChatController
import com.example.controller.MessageController
import com.example.controller.UserController
import com.example.data.ChatRoomDataSource
import com.example.data.MessageDataSource
import com.example.data.UserDataSource
import org.koin.core.scope.get
import org.koin.dsl.module

val mainModule = module {
    single {
        UserDataSource()
    }
    single {
        UserController(get())
    }
    single {
        ChatRoomDataSource()
    }
    single {
        ChatController(get(), get(), get())
    }
    single {
        MessageDataSource()
    }
    single {
        MessageController(get(), get())
    }
}