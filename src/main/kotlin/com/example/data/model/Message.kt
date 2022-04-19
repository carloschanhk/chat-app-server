package com.example.data.model

@kotlinx.serialization.Serializable
data class Message(
    val id: Int,
    val userId: Int,
    val username: String,
    val roomId: Int,
    val createdAt: Long,
    val text: String,
    val isRead: Boolean,
)
