package com.clementvexegon.instruxa.data.model

data class Artist(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val userType: String = "artist",
    val bio: String = "",
    val instruments: List<String> = emptyList(),
    val skills: List<String> = emptyList(),
    val price: Int = 0,
    val rating: Float = 0f,
    val reviews: Int = 0,
    val gigs: Int = 0,
    val isAvailable: Boolean = true,
    val location: String = "",
    val photoUrl: String = "",
    val portfolioUrls: List<String> = emptyList()
)