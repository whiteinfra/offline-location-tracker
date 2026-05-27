package com.example.locationapp

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "locations")
data class LocationEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val latitude: Double,
    val longitude: Double,
    val accuracy: Float? = null,
    val provider: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)
