package com.clementvexegon.instruxa.data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.clementvexegon.instruxa.data.model.Artist
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class ArtistViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()

    private val _artists = MutableStateFlow<List<Artist>>(emptyList())
    val artists: StateFlow<List<Artist>> = _artists

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    // Fetch all artists from Firestore
    fun fetchArtists() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val snapshot = db.collection("users")
                    .whereEqualTo("userType", "artist")
                    .whereEqualTo("isAvailable", true)
                    .get().await()
                _artists.value = snapshot.documents.mapNotNull {
                    it.toObject(Artist::class.java)?.copy(id = it.id)
                }
            } catch (e: Exception) {
                // Handle error
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Save artist profile to Firestore
    fun saveProfile(artist: Artist, onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                db.collection("users").document(artist.id)
                    .set(artist).await()
                onSuccess()
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
}