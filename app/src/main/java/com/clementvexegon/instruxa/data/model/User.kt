package com.clementvexegon.instruxa.data.model

data class User(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val isArtist: Boolean = false,
    val profileImage: String = ""
)