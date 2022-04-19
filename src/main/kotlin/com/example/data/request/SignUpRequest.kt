package com.example.data.request

@kotlinx.serialization.Serializable
data class SignUpRequest(
    val name: String,
    val password: String,
)
