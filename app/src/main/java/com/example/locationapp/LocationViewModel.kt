package com.example.locationapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class LocationViewModel @Inject constructor(
    private val repo: LocationRepository,
) : ViewModel() {

    val locations: StateFlow<List<LocationEntity>> = repo.observeCachedLocations()
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    private val _status = MutableStateFlow("Idle")
    val status: StateFlow<String> = _status

    fun fetchAndSaveCurrentLocation() {
        viewModelScope.launch {
            _status.value = "Requesting..."
            val location = repo.requestCurrentLocation() ?: repo.getLastKnownLocation()
            if (location != null) {
                repo.saveLocationToDb(location)
                _status.value = "Saved at ${Date(location.time)}"
            } else {
                _status.value = "Location unavailable"
            }
        }
    }

    fun clearCache() {
        viewModelScope.launch { repo.clearCache() }
    }
}
