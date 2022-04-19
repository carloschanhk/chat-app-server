package com.example.data.model

@kotlinx.serialization.Serializable
data class ChatRoom(
    val id: Int,
    val title: String?,
    val createdAt: Long,
    val participants: List<User> = listOf(),
    val lastMessage: Message? = null,
)
