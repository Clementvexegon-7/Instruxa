package com.clementvexegon.instruxa.data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.clementvexegon.instruxa.data.model.Booking
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class BookingViewModel : ViewModel() {

    private val db = Firebase.firestore

    private val _myBookings = MutableStateFlow<List<Booking>>(emptyList())
    val myBookings: StateFlow<List<Booking>> = _myBookings

    // Create a new booking
    fun createBooking(booking: Booking, onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                val ref = db.collection("bookings").document()
                db.collection("bookings").document(ref.id)
                    .set(booking.copy(id = ref.id, timestamp = System.currentTimeMillis()))
                    .await()
                onSuccess()
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    // Get bookings for current user
    fun fetchMyBookings(userId: String) {
        viewModelScope.launch {
            try {
                val snapshot = db.collection("bookings")
                    .whereEqualTo("clientId", userId)
                    .get().await()
                _myBookings.value = snapshot.documents.mapNotNull {
                    it.toObject(Booking::class.java)?.copy(id = it.id)
                }
            } catch (e: Exception) {}
        }
    }
}