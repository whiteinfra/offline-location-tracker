package com.example.locationapp

import android.Manifest
import android.content.pm.PackageManager
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import java.util.Date

@Composable
fun LocationsScreen(viewModel: LocationViewModel = hiltViewModel()) {
    val context = LocalContext.current
    var hasPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    if (!hasPermission) {
        LocationPermissionScreen(onPermissionsGranted = { hasPermission = true })
    } else {
        LocationContent(viewModel)
    }
}

@Composable
fun LocationContent(viewModel: LocationViewModel) {
    val locations by viewModel.locations.collectAsState()
    val status by viewModel.status.collectAsState()

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Status: $status")

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Button(onClick = { viewModel.fetchAndSaveCurrentLocation() }) {
                Text("Отримати позицію")
            }
            Button(onClick = { viewModel.clearCache() }) {
                Text("Очистити")
            }
        }

        Spacer(modifier = Modifier.height(12.dp))
        LazyColumn {
            items(locations, key = { it.id }) { item ->
                Card(Modifier.fillMaxWidth().padding(4.dp)) {
                    Column(Modifier.padding(12.dp)) {
                        Text("Lat: ${item.latitude}, Lon: ${item.longitude}")
                        Text("Accuracy: ${item.accuracy ?: "n/a"}")
                        Text("Time: ${Date(item.timestamp)}")
                    }
                }
            }
        }
    }
}
