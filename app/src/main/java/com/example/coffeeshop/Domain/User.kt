package com.example.coffeeshop.Domain

data class User(
    val uid: String = "",
    val name: String = "user",
    val email: String = "",
    val role: String = "user",
    val avatarUrl: String = ""
)