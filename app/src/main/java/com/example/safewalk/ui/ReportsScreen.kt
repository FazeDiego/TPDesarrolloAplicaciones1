package com.example.safewalk.ui


import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.background
import androidx.compose.ui.draw.clip
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.toColorInt
import androidx.navigation.NavController
import com.example.safewalk.data.FirestoreRepository
import com.example.safewalk.model.Review
import com.example.safewalk.model.Tramo
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import java.text.SimpleDateFormat
import java.util.*
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64


// -------------------- Data class para combinar review + tramo + usuario --------------------
data class ReviewCardData(
    val review: Review,
    val usuarioNombre: String?,
    val usuarioPhotoBase64: String?,
    val tramo: Tramo?
)

// -------------------- Composable de la Card --------------------
@Composable
fun ReportCard(review: Review, usuarioNombre: String, usuarioPhotoBase64: String?, tramo: Tramo?) {

    // Decodificar la imagen Base64 antes del Composable
    val reviewBitmap: Bitmap? = remember(review.fotoBase64) {
        review.fotoBase64?.let { base64 ->
            try {
                val imageBytes = Base64.decode(base64, Base64.DEFAULT)
                BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }

    // Decodificar la foto de perfil del usuario
    val userProfileBitmap: Bitmap? = remember(usuarioPhotoBase64) {
        usuarioPhotoBase64?.let { base64 ->
            try {
                val imageBytes = Base64.decode(base64, Base64.DEFAULT)
                BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }

    Card(
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            // Usuario con foto de perfil
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Foto de perfil circular
                if (userProfileBitmap != null) {
                    Image(
                        bitmap = userProfileBitmap.asImageBitmap(),
                        contentDescription = "Foto de perfil",
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                    )
                } else {
                    // Placeholder circular con inicial
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = usuarioNombre.firstOrNull()?.uppercase() ?: "?",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    // Nombre del usuario
                    Text(
                        text = usuarioNombre,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )

                    // Fecha
                    val fechaStr =
                        SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(review.fecha.toDate())
                    Text(
                        text = fechaStr,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Comentario
            Text(text = review.comentario.ifBlank { "Sin comentario" })

            Spacer(modifier = Modifier.height(12.dp))

            // ⭐⭐⭐ Rating
            Row(verticalAlignment = Alignment.CenterVertically) {
                repeat(5) { i ->
                    Icon(
                        imageVector = if (i < review.rating) Icons.Default.Star else Icons.Default.StarBorder,
                        contentDescription = null,
                        tint = Color("#0065C2".toColorInt()),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ----------------- Foto -----------------
            reviewBitmap?.let { bitmap ->
                Image(
                    bitmap = bitmap.asImageBitmap(),
                    contentDescription = "Foto de la review",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .padding(vertical = 8.dp)
                )
            }

            // Tramo (Calles)
            tramo?.let {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Tramo: ${it.nombreCalleA} - ${it.nombreCalleB}",
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun TramoStatsCard(tramo: Tramo) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color("#0065C2".toColorInt())),
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Nombre del tramo
            Text(
                text = "${tramo.nombreCalleA} - ${tramo.nombreCalleB}",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Promedio con estrellas
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "%.1f".format(tramo.ratingPromedio),
                    fontSize = 36.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.width(8.dp))
                Row {
                    val fullStars = tramo.ratingPromedio.toInt()
                    val maxStars = 5

                    for (i in 1..maxStars) {
                        Icon(
                            imageVector = if (i <= fullStars) Icons.Filled.Star else Icons.Outlined.StarBorder,
                            contentDescription = null,
                            tint = Color.Yellow,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }

            }

            Spacer(modifier = Modifier.height(8.dp))

            // Cantidad de reviews
            Text(
                text = "${tramo.cantidadReviews} review${if (tramo.cantidadReviews != 1) "s" else ""}",
                fontSize = 14.sp,
                color = Color.White.copy(alpha = 0.8f)
            )
        }
    }
}



// -------------------- Pantalla de Reports --------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportsScreen(navController: NavController) {
    val context = LocalContext.current
    val placesClient = remember { Places.createClient(context) }
    val focusManager = LocalFocusManager.current
    val repo = remember { FirestoreRepository() }

    var reviews by remember { mutableStateOf<List<Review>>(emptyList()) }
    var reportCards by remember { mutableStateOf<List<ReviewCardData>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }

    var puntoA by rememberSaveable { mutableStateOf("") }
    var puntoB by rememberSaveable { mutableStateOf("") }
    var suggestionsA by rememberSaveable { mutableStateOf(listOf<AutocompletePrediction>()) }
    var suggestionsB by rememberSaveable { mutableStateOf(listOf<AutocompletePrediction>()) }

    var calleA by remember { mutableStateOf("") }
    var calleB by remember { mutableStateOf("") }

    // Funciones de búsqueda de lugar
    fun searchPlace(query: String, onResult: (List<AutocompletePrediction>) -> Unit) {
        if (query.isEmpty()) { onResult(emptyList()); return }
        val request = FindAutocompletePredictionsRequest.builder().setQuery(query).build()
        placesClient.findAutocompletePredictions(request)
            .addOnSuccessListener { resp -> onResult(resp.autocompletePredictions) }
            .addOnFailureListener { onResult(emptyList()) }
    }

    fun fetchPlaceDetails(prediction: AutocompletePrediction, onResult: (String?) -> Unit) {
        val placeId = prediction.placeId
        val request = FetchPlaceRequest.newInstance(placeId, listOf(Place.Field.ADDRESS_COMPONENTS))
        placesClient.fetchPlace(request)
            .addOnSuccessListener { response ->
                val comps = response.place.addressComponents?.asList()
                val routeName = comps?.firstOrNull { it.types.contains("route") }?.name
                onResult(routeName ?: prediction.getFullText(null).toString())
            }
            .addOnFailureListener { onResult(null) }
    }

    // -------------------- Cargar reviews y datos relacionados --------------------
    LaunchedEffect(Unit) {
        repo.obtenerTodosLosReviews { list ->
            reviews = list
            if (list.isEmpty()) {
                reportCards = emptyList()
                loading = false
            } else {
                val resolved = mutableListOf<ReviewCardData>()
                var remaining = list.size
                list.forEach { review ->
                    repo.obtenerTramoPorId(review.tramoId) { tramo ->
                        repo.obtenerUsuario(review.usuarioId) { usuario ->
                            resolved.add(
                                ReviewCardData(
                                    review = review,
                                    usuarioNombre = usuario?.nombre ?: "Anonimo",
                                    usuarioPhotoBase64 = usuario?.photoBase64,
                                    tramo = tramo
                                )
                            )
                            remaining--
                            if (remaining == 0) {
                                reportCards = resolved.sortedByDescending { it.review.fecha.toDate() }
                                loading = false
                            }
                        }
                    }
                }
            }
        }
    }

    // -------------------- Filtrado --------------------
    val filteredReports = reportCards.filter { card ->
        val matchA = calleA.isBlank() || card.tramo?.nombreCalleA?.contains(calleA, ignoreCase = true) == true
        val matchB = calleB.isBlank() || card.tramo?.nombreCalleB?.contains(calleB, ignoreCase = true) == true
        matchA && matchB
    }

    // -------------------- UI Scaffold --------------------
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Reviews por Tramo",
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigate("home") }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(16.dp)
        ) {
            // Campo Punto A
            Text("Punto A")
            OutlinedTextField(
                value = puntoA,
                onValueChange = {
                    puntoA = it
                    searchPlace(it) { results -> suggestionsA = results }
                },
                placeholder = { Text("Buscar dirección…") },
                modifier = Modifier.fillMaxWidth()
            )
            suggestionsA.take(4).forEach { suggestion ->
                TextButton(onClick = {
                    puntoA = suggestion.getFullText(null).toString()
                    suggestionsA = emptyList()
                    focusManager.clearFocus()
                    fetchPlaceDetails(suggestion) { name -> calleA = name ?: "" }
                }) { Text(suggestion.getFullText(null).toString()) }
            }

            Spacer(Modifier.height(16.dp))

            // Campo Punto B
            Text("Punto B")
            OutlinedTextField(
                value = puntoB,
                onValueChange = {
                    puntoB = it
                    searchPlace(it) { results -> suggestionsB = results }
                },
                placeholder = { Text("Buscar dirección…") },
                modifier = Modifier.fillMaxWidth()
            )
            suggestionsB.take(4).forEach { suggestion ->
                TextButton(onClick = {
                    puntoB = suggestion.getFullText(null).toString()
                    suggestionsB = emptyList()
                    focusManager.clearFocus()
                    fetchPlaceDetails(suggestion) { name -> calleB = name ?: "" }
                }) { Text(suggestion.getFullText(null).toString()) }
            }

            Spacer(Modifier.height(16.dp))

            // Lista de cards
            if (loading) {
                Text("Cargando reviews…")
            } else if (filteredReports.isEmpty()) {
                Text("No hay reviews para estos puntos")
            } else {
                val hayFiltro = calleA.isNotBlank() || calleB.isNotBlank()

                val filteredReports = reportCards.filter { card ->
                    val matchA = calleA.isBlank() || card.tramo?.nombreCalleA?.contains(calleA, ignoreCase = true) == true
                    val matchB = calleB.isBlank() || card.tramo?.nombreCalleB?.contains(calleB, ignoreCase = true) == true
                    matchA && matchB
                }

                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    // Mostrar TramoStatsCard solo si hay filtro y hay resultados filtrados
                    if (hayFiltro && filteredReports.isNotEmpty()) {
                        val firstTramo = filteredReports.firstOrNull()?.tramo
                        firstTramo?.let { tramo ->
                            item { TramoStatsCard(tramo) }
                        }
                    }

                    // Luego las cards individuales de cada review
                    items(filteredReports) { card ->
                        ReportCard(
                            review = card.review,
                            usuarioNombre = card.usuarioNombre ?: "Anonimo",
                            usuarioPhotoBase64 = card.usuarioPhotoBase64,
                            tramo = card.tramo
                        )
                    }
                }

            }
        }
    }
}
