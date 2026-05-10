package com.clementvexegon.instruxa.data.repository

import com.clementvexegon.instruxa.data.model.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class MainRepository {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    // 🔐 Get current user ID
    fun getCurrentUserId(): String? {
        return auth.currentUser?.uid
    }

    // 👤 Save User
    suspend fun saveUser(user: User) {
        user.id.let {
            db.collection("users")
                .document(it)
                .set(user)
                .await()
        }
    }

    // 🎤 Save Artist Profile
    suspend fun saveArtist(artist: Artist) {
        db.collection("artists")
            .document(artist.id)
            .set(artist)
            .await()
    }

    // 📥 Get All Artists
    suspend fun getArtists(): List<Artist> {
        val snapshot = db.collection("artists").get().await()
        return snapshot.toObjects(Artist::class.java)
    }

    // 📅 Book Artist
    suspend fun createBooking(booking: Booking) {
        db.collection("bookings")
            .document()
            .set(booking)
            .await()
    }

    // ⭐ Add Review
    suspend fun addReview(review: Review) {
        db.collection("reviews")
            .document()
            .set(review)
            .await()
    }
}