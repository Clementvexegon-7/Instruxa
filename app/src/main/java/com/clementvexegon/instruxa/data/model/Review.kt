package com.clementvexegon.instruxa.data.model

data class Review(
    val id: String = "",
    val artistId: String = "",
    val clientId: String = "",
    val clientName: String = "",
    val rating: Int = 0,
    val comment: String = "",
    val tags: List<String> = emptyList(),
    val recommends: Boolean = true,
    val timestamp: Long = 0L
)
