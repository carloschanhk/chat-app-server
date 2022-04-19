package com.example.data.request

@kotlinx.serialization.Serializable
data class SendMessageRequest(
    val roomId: Int,
    val userId: Int,
    val content: String,
)