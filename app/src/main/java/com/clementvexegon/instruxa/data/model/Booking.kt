package com.clementvexegon.instruxa.data.model

data class Booking(
    val id: String = "",
    val artistId: String = "",
    val clientId: String = "",
    val artistName: String = "",
    val clientName: String = "",
    val date: String = "",
    val time: String = "",
    val duration: Int = 1,
    val sessionType: String = "",
    val price: Int = 0,
    val status: String = "pending",
    val mpesaPhone: String = "",
    val notes: String = "",
    val timestamp: Long = 0L
)