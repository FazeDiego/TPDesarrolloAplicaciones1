package com.example.safewalk.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.graphics.toColorInt
import androidx.navigation.NavController
import com.example.safewalk.R
import com.example.safewalk.data.FirestoreRepository
import com.example.safewalk.model.Tramo
import com.example.safewalk.viewmodel.ThemeViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.maps.android.compose.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController, themeViewModel: ThemeViewModel) {

    val context = LocalContext.current
    val isDarkTheme by themeViewModel.isDarkTheme.collectAsState()
    val fusedLocationClient = remember {
        LocationServices.getFusedLocationProviderClient(context)
    }

    // ubicación actual
    var userLocation by remember { mutableStateOf<LatLng?>(null) }

    // usuario
    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    // repo + tramos
    val repo = remember { FirestoreRepository() }
    var tramos by remember { mutableStateOf<List<Tramo>>(emptyList()) }

    // cargar tramos
    LaunchedEffect(userId) {
        repo.obtenerTramosConReviewsUsuario(userId) { list ->
            tramos = list
        }
    }

    // permisos
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { permissions ->
            val granted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                    permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true

            if (granted) {
                getUserLocation(fusedLocationClient) { loc ->
                    userLocation = loc
                }
            }
        }
    )

    // pedir ubicación inicial
    LaunchedEffect(Unit) {
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            permissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        } else {
            getUserLocation(fusedLocationClient) { loc ->
                userLocation = loc
            }
        }
    }

    // cámara del mapa
    val cameraPositionState = rememberCameraPositionState()

    // mover la cámara cuando llega la ubicación
    LaunchedEffect(userLocation) {
        userLocation?.let { loc ->
            cameraPositionState.move(
                CameraUpdateFactory.newLatLngZoom(loc, 15f)
            )
        }
    }


    //--------------------------------------------------------------------
    // UI
    //--------------------------------------------------------------------

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Mapa",
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                },
                navigationIcon = {
                    Image(
                        painter = painterResource(R.drawable.logo),
                        contentDescription = "Logo de SafeWalk",
                        modifier = Modifier.size(48.dp)
                    )
                },
                actions = {
                    IconButton(onClick = { themeViewModel.toggleTheme(context) }) {
                        Icon(
                            imageVector = if (isDarkTheme) Icons.Default.LightMode else Icons.Default.DarkMode,
                            contentDescription = if (isDarkTheme) "Cambiar a modo claro" else "Cambiar a modo oscuro"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface
            ) {
                NavigationBarItem(
                    selected = true,
                    onClick = {},
                    icon = { Icon(Icons.Default.Map, contentDescription = "Navegar a Mapa") },
                    label = { Text("Mapa", fontSize = 12.sp) }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { navController.navigate("reportsScreen") },
                    icon = { Icon(Icons.Default.Checklist, contentDescription = "Navegar a Reportes") },
                    label = { Text("Reportes", fontSize = 12.sp) }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { navController.navigate("profileScreen") },
                    icon = { Icon(Icons.Default.Person, contentDescription = "Navegar a Perfil") },
                    label = { Text("Perfil", fontSize = 12.sp) }
                )
            }
        },
        content = { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(padding)
            ) {

                // mapa
                GoogleMap(
                    modifier = Modifier.fillMaxSize(),
                    cameraPositionState = cameraPositionState,
                    properties = MapProperties(
                        isMyLocationEnabled = userLocation != null,
                        mapStyleOptions = if (isDarkTheme) {
                            MapStyleOptions.loadRawResourceStyle(context, R.raw.map_style_dark)
                        } else null
                    ),
                    uiSettings = MapUiSettings(
                        zoomControlsEnabled = true,
                        myLocationButtonEnabled = true
                    ),
                    contentPadding = PaddingValues(bottom = 100.dp, end = 16.dp)
                ) {

                    tramos.forEach { tramo ->
                        val pA = LatLng(tramo.puntoA.latitude, tramo.puntoA.longitude)
                        val pB = LatLng(tramo.puntoB.latitude, tramo.puntoB.longitude)

                        val (colorLinea, etiqueta) = tramoVisual(tramo.ratingPromedio)

                        // Línea entre A y B
                        Polyline(
                            points = listOf(pA, pB),
                            color = colorLinea,
                            width = 8f,
                            geodesic = true
                        )

                        // Marcadores suaves
                        Circle(
                            center = pA,
                            radius = 25.0,
                            strokeColor = Color.Transparent,
                            fillColor = colorLinea.copy(alpha = 0.6f)
                        )
                        Circle(
                            center = pB,
                            radius = 25.0,
                            strokeColor = Color.Transparent,
                            fillColor = colorLinea.copy(alpha = 0.6f)
                        )

                        // Texto encima de la línea (en el punto medio)
                        val midLat = (pA.latitude + pB.latitude) / 2
                        val midLng = (pA.longitude + pB.longitude) / 2
                        val midPoint = LatLng(midLat, midLng)


// Elegir color del chip según el tramo

                        val chipColor = when (tramo.ratingPromedio) {
                            in 4.0..5.0 -> android.graphics.Color.GREEN                  // Seguro
                            in 2.0..3.9 -> android.graphics.Color.parseColor("#FFA500")  // Inseguro
                            else -> android.graphics.Color.RED                           // Muy Peligroso
                        }

// Crear bitmap del label
                        val labelBitmap = styledTextBitmap(etiqueta, backgroundColor = chipColor)

                        Marker(
                            state = MarkerState(position = midPoint),
                            icon = BitmapDescriptorFactory.fromBitmap(labelBitmap)
                        )
                    }

                }

                // botón flotante
                Button(
                    onClick = { navController.navigate("newReportScreen") },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color("#0065C2".toColorInt())
                    ),
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(16.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Reportar tramo", tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Reportar Tramo", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }
    )
}

@SuppressLint("MissingPermission")
fun getUserLocation(
    fusedLocationClient: FusedLocationProviderClient,
    onLocationFound: (LatLng) -> Unit
) {
    fusedLocationClient.lastLocation.addOnSuccessListener { location ->
        if (location != null) {
            onLocationFound(LatLng(location.latitude, location.longitude))
        } else {
            fusedLocationClient.getCurrentLocation(
                com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY,
                null
            ).addOnSuccessListener { loc2 ->
                loc2?.let { onLocationFound(LatLng(it.latitude, it.longitude)) }
            }
        }
    }
}

fun tramoVisual(ratingPromedio: Double): Pair<Color, String> {
    return when {
        ratingPromedio >= 4.0 -> Color.Green to "Seguro"           // 4.0 - 5.0 estrellas
        ratingPromedio >= 2.0 -> Color(0xFFFFA500) to "Inseguro"   // 2.0 - 3.9 estrellas
        else -> Color.Red to "Muy Peligroso"                       // 0.0 - 1.9 estrellas
    }
}

fun styledTextBitmap(
    text: String,
    textSize: Float = 40f,
    textColor: Int = android.graphics.Color.WHITE,
    backgroundColor: Int = android.graphics.Color.parseColor("#0065C2"),
    padding: Float = 20f,
    cornerRadius: Float = 30f
): Bitmap {
    val paintText = Paint(Paint.ANTI_ALIAS_FLAG)
    paintText.textSize = textSize
    paintText.color = textColor
    paintText.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)

    val textWidth = paintText.measureText(text)
    val textHeight = paintText.descent() - paintText.ascent()

    val width = (textWidth + 2 * padding).toInt()
    val height = (textHeight + 2 * padding).toInt()

    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)

    // Fondo redondeado
    val paintBg = Paint(Paint.ANTI_ALIAS_FLAG)
    paintBg.color = backgroundColor
    paintBg.style = Paint.Style.FILL
    canvas.drawRoundRect(0f, 0f, width.toFloat(), height.toFloat(), cornerRadius, cornerRadius, paintBg)

    // Texto centrado
    val x = padding
    val y = padding - paintText.ascent()
    canvas.drawText(text, x, y, paintText)

    return bitmap
}
