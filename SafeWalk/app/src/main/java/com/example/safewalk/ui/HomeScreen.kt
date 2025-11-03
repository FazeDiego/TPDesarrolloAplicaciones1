package com.example.safewalk.ui

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import org.osmdroid.config.Configuration
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen() {
    var menuExpanded by remember { mutableStateOf(false) } // controla si el menú está abierto
    val context = LocalContext.current

    // 🔹 Inicializar configuración de OSM
    Configuration.getInstance().load(
        context,
        context.getSharedPreferences("osmdroid", 0)
    )

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),

        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF9C64D6),
                    titleContentColor = Color.White,
                ),
                title = {
                    Text(
                        "SafeWalk",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },

                navigationIcon = {
                    IconButton(onClick = { menuExpanded = true }) {
                        Icon(
                            imageVector = Icons.Filled.Menu,
                            contentDescription = "Menú"
                        )
                    }

                    // Menú desplegable
                    DropdownMenu(
                        expanded = menuExpanded,
                        onDismissRequest = { menuExpanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Evaluar tramo") },
                            onClick = {
                                menuExpanded = false
                                // Acción para Evaluar tramo
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Historial") },
                            onClick = {
                                menuExpanded = false
                                // Acción para Historial
                            }
                        )

                    }
                },




                actions = {

                    IconButton(onClick = { /* do something */ }) {
                        Icon(
                            imageVector = Icons.Filled.AccountCircle,
                            contentDescription = "Localized description"
                        )
                    }
                },
                scrollBehavior = scrollBehavior,
            )
        },
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            OSMMapView()
        }
    }

}



@SuppressLint("MissingPermission")
@Composable
fun OSMMapView() {
    val context = LocalContext.current

    AndroidView(
        factory = { ctx ->
            MapView(ctx).apply {
                setMultiTouchControls(true)

                // Overlay de ubicación
                val locationOverlay = MyLocationNewOverlay(GpsMyLocationProvider(ctx), this)
                locationOverlay.enableMyLocation()      // Permite ver la ubicación
                locationOverlay.enableFollowLocation()  // Hace que el mapa siga al usuario
                overlays.add(locationOverlay)

                // Ajuste inicial del mapa al primer fix
                locationOverlay.runOnFirstFix {
                    post {
                        locationOverlay.myLocation?.let { loc ->
                            val geoPoint = GeoPoint(loc.latitude, loc.longitude)
                            controller.setZoom(17.0)
                            controller.animateTo(geoPoint)
                            invalidate()
                        }
                    }
                }
            }
        },
        modifier = Modifier.fillMaxSize()
    )
}
