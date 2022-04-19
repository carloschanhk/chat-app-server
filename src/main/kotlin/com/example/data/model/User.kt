package com.example.data.model


@kotlinx.serialization.Serializable
data class User(
    val id: Int,
    val name: String,
    val createdAt: Long,
)
