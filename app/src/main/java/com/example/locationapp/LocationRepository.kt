package com.example.locationapp

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.os.Looper
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import javax.inject.Singleton

@OptIn(ExperimentalCoroutinesApi::class)
@Singleton
class LocationRepository @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val dao: LocationDao,
) {
    private val fusedClient = LocationServices.getFusedLocationProviderClient(context)

    @SuppressLint("MissingPermission")
    suspend fun getLastKnownLocation(): Location? = suspendCancellableCoroutine { cont ->
        fusedClient.lastLocation
            .addOnSuccessListener { loc -> cont.resume(loc) {} }
            .addOnFailureListener { _ -> cont.resume(null) {} }
    }

    @SuppressLint("MissingPermission")
    suspend fun requestCurrentLocation(): Location? = suspendCancellableCoroutine { cont ->
        val request = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 0L).build()
        val callback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                val l = result.lastLocation
                fusedClient.removeLocationUpdates(this)
                cont.resume(l) {}
            }
        }
        fusedClient.requestLocationUpdates(request, callback, Looper.getMainLooper())
            .addOnFailureListener {
                fusedClient.removeLocationUpdates(callback)
                cont.resume(null) {}
            }

        cont.invokeOnCancellation {
            fusedClient.removeLocationUpdates(callback)
        }
    }

    suspend fun saveLocationToDb(location: Location) {
        dao.insert(
            LocationEntity(
                latitude = location.latitude,
                longitude = location.longitude,
                accuracy = location.accuracy,
                provider = location.provider,
                timestamp = location.time
            )
        )
    }

    fun observeCachedLocations(): Flow<List<LocationEntity>> = dao.observeAll()

    suspend fun clearCache() = dao.clear()
}
