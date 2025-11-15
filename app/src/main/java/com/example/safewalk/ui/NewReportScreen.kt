package com.example.safewalk.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.toColorInt
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.safewalk.viewmodel.SegmentViewModel
import com.google.android.gms.maps.model.BitmapDescriptorFactory

// MAPS
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

// PLACES
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.maps.android.compose.Circle


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewReportScreen(navController: NavController, segmentViewModel: SegmentViewModel) {

    val db = FirebaseFirestore.getInstance()



    val context = LocalContext.current
    val placesClient = remember { Places.createClient(context) }

    val focusManager = LocalFocusManager.current

    var puntoA by rememberSaveable  { mutableStateOf("") }
    var puntoB by rememberSaveable  { mutableStateOf("") }

    var suggestionsA by rememberSaveable { mutableStateOf(listOf<AutocompletePrediction>()) }
    var suggestionsB by rememberSaveable { mutableStateOf(listOf<AutocompletePrediction>()) }

    var puntoALatLng by rememberSaveable { mutableStateOf<LatLng?>(null) }
    var puntoBLatLng by rememberSaveable { mutableStateOf<LatLng?>(null) }

    // Buscar texto
    fun searchPlace(query: String, onResult: (List<AutocompletePrediction>) -> Unit) {
        if (query.isEmpty()) {
            onResult(emptyList())
            return
        }

        val request = FindAutocompletePredictionsRequest.builder()
            .setQuery(query)
            .build()

        placesClient.findAutocompletePredictions(request)
            .addOnSuccessListener { response -> onResult(response.autocompletePredictions) }
            .addOnFailureListener { onResult(emptyList()) }
    }

    // Obtener coordenadas
    fun fetchLatLng(prediction: AutocompletePrediction, onResult: (LatLng?) -> Unit) {
        val placeId = prediction.placeId
        val request = FetchPlaceRequest.newInstance(placeId, listOf(Place.Field.LAT_LNG))

        placesClient.fetchPlace(request)
            .addOnSuccessListener { response ->
                onResult(response.place.latLng)
            }
            .addOnFailureListener {
                onResult(null)
            }
    }

    Scaffold(
        containerColor = Color.White,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Seleccionar puntos de Ruta",
                        color = Color.Black,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigate("home") }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Volver",
                            tint = Color.Black
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = Color.Black,
                    navigationIconContentColor = Color.Black
                )
            )
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(20.dp)
        ) {

            // ---------------- CAMPO A ----------------
            Text("Punto A", color = Color.Black)

            OutlinedTextField(
                value = puntoA,
                onValueChange = {
                    puntoA = it
                    searchPlace(it) { results -> suggestionsA = results }
                },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Buscar dirección…", color = Color.Gray) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF0065C2),
                    unfocusedBorderColor = Color.Gray,
                    cursorColor = Color.Black,
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black,
                    focusedPlaceholderColor = Color.Gray,
                    unfocusedPlaceholderColor = Color.Gray
                )
            )

            suggestionsA.take(4).forEach { suggestion ->
                TextButton(
                    onClick = {
                        puntoA = suggestion.getFullText(null).toString()
                        suggestionsA = emptyList()
                        focusManager.clearFocus()

                        fetchLatLng(suggestion) { puntoALatLng = it }
                    }
                ) {
                    Text(suggestion.getFullText(null).toString(), color = Color.Black)
                }
            }

            Spacer(Modifier.height(20.dp))

            // ---------------- CAMPO B ----------------
            Text("Punto B", color = Color.Black)

            OutlinedTextField(
                value = puntoB,
                onValueChange = {
                    puntoB = it
                    searchPlace(it) { results -> suggestionsB = results }
                },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Buscar dirección…", color = Color.Gray) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF0065C2),
                    unfocusedBorderColor = Color.Gray,
                    cursorColor = Color.Black,
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black,
                    focusedPlaceholderColor = Color.Gray,
                    unfocusedPlaceholderColor = Color.Gray
                )
            )

            suggestionsB.take(4).forEach { suggestion ->
                TextButton(
                    onClick = {
                        puntoB = suggestion.getFullText(null).toString()
                        suggestionsB = emptyList()
                        focusManager.clearFocus()
                        fetchLatLng(suggestion) { puntoBLatLng = it }
                    }
                ) {
                    Text(suggestion.getFullText(null).toString(), color = Color.Black)
                }
            }

            // ---------------- MAPA ----------------
            if (puntoALatLng != null && puntoBLatLng != null) {

                val cameraPositionState = rememberCameraPositionState {
                    position = CameraPosition.fromLatLngZoom(puntoALatLng!!, 13f)
                }

                Spacer(Modifier.height(20.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(12.dp))
                ) {
                    GoogleMap(
                        modifier = Modifier.fillMaxSize(),
                        cameraPositionState = cameraPositionState
                    ) {
                        Circle(
                            center = puntoALatLng!!,
                            radius = 30.0,                      // más visible al alejar
                            strokeColor = Color.Transparent,
                            fillColor = Color(0xFF0065C2)
                        )

                        Circle(
                            center = puntoBLatLng!!,
                            radius = 30.0,
                            strokeColor = Color.Transparent,
                            fillColor = Color(0xFF0065C2)
                        )

                        Polyline(
                            points = listOf(puntoALatLng!!, puntoBLatLng!!),
                            color = Color("#0065C2".toColorInt()),  // azul de tu app
                            width = 8f,                             // grosor
                            geodesic = true                         // más natural
                        )

                    }
                }
            }

            Spacer(Modifier.height(32.dp))






            // ---------------- BOTÓN ----------------
            Button(
                onClick = {
                    segmentViewModel.puntoA = puntoALatLng
                    segmentViewModel.puntoB = puntoBLatLng
                    navController.navigate("reportScreen") },

                colors = ButtonDefaults.buttonColors(
                    containerColor = Color("#0065C2".toColorInt())
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
            ) {
                Text("Confirmar Selección", color = Color.White)
            }
        }
    }
}
