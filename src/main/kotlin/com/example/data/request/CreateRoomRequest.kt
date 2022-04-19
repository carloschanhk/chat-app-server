package com.example.data.request

@kotlinx.serialization.Serializable
data class CreateRoomRequest(
    val userId: Int,
    val partnerId: Int,
)
