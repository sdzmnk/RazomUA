package com.example.razomua.ui.screens.welcome

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.example.razomua.R
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest
import com.google.android.libraries.places.api.net.PlacesClient
import kotlinx.coroutines.tasks.await

@OptIn(MapsComposeExperimentalApi::class)
@SuppressLint("MissingPermission")
@Composable
fun GoogleMapsScreen(navController: NavController) {
    val context = LocalContext.current
    val defaultLocation = LatLng(50.4501, 30.5234) // Київ

    var userLocation by remember { mutableStateOf<LatLng?>(null) }
    var permissionGranted by remember { mutableStateOf(false) }
    val nearbyPlaces = remember { mutableStateListOf<Place>() }

    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    // ✅ Ініціалізація Places API
    if (!Places.isInitialized()) {
        Places.initialize(context, context.getString(R.string.google_maps_key))
    }
    val placesClient: PlacesClient = Places.createClient(context)

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        permissionGranted = isGranted
        if (isGranted) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                userLocation = location?.let {
                    LatLng(it.latitude, it.longitude)
                } ?: defaultLocation
            }
        } else {
            userLocation = defaultLocation
        }
    }

    // 🟡 Запит дозволу при відкритті екрана
    LaunchedEffect(Unit) {
        permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED
        ) {
            permissionGranted = true
        }
    }

    // 🟢 Завантажуємо місця поруч, коли є локація
    LaunchedEffect(userLocation, permissionGranted) {
        if (permissionGranted && userLocation != null) {
            try {
                val request = FindCurrentPlaceRequest.newInstance(
                    listOf(Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.TYPES)
                )
                val response = placesClient.findCurrentPlace(request).await()
                nearbyPlaces.clear()
                response.placeLikelihoods.forEach { likelihood ->
                    likelihood.place.latLng?.let {
                        nearbyPlaces.add(likelihood.place)
                    }
                }
                Log.i("Places", "Loaded ${nearbyPlaces.size} nearby places")
            } catch (e: Exception) {
                Log.e("Places", "Error fetching places: ${e.message}")
            }
        }
    }

    Scaffold(
        bottomBar = {
            BottomAppBar(containerColor = Color.White, tonalElevation = 4.dp) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { navController.navigate("chats") }) {
                        Icon(
                            painter = painterResource(id = R.drawable.chats),
                            contentDescription = "Chats",
                            tint = Color.Black,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                    IconButton(onClick = { navController.navigate("swipe") }) {
                        Icon(
                            painter = painterResource(id = R.drawable.cards),
                            contentDescription = "Swipes",
                            tint = Color.Black,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                    IconButton(onClick = { navController.navigate("profile") }) {
                        Icon(
                            painter = painterResource(id = R.drawable.user),
                            contentDescription = "Profile",
                            tint = Color.Black,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (userLocation != null) {
                val cameraPositionState = rememberCameraPositionState {
                    position = CameraPosition.fromLatLngZoom(userLocation!!, 14f)
                }

                GoogleMap(
                    modifier = Modifier.fillMaxSize(),
                    cameraPositionState = cameraPositionState,
                    properties = MapProperties(isMyLocationEnabled = permissionGranted)
                ) {
                    // 🔵 Маркер користувача
                    Marker(
                        state = MarkerState(position = userLocation!!),
                        title = "Ти тут ❤️",
                        snippet = "Твоє поточне місцезнаходження"
                    )

                    // 🔴 Маркери місць поруч
                    nearbyPlaces.forEach { place ->
                        place.latLng?.let { latLng ->
                            Marker(
                                state = MarkerState(position = latLng),
                                title = place.name,
                                snippet = place.types?.joinToString() ?: "Місце поруч"
                            )
                        }
                    }
                }
            } else {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}
