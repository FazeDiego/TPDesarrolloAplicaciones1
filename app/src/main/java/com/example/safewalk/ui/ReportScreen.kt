package com.example.safewalk.ui


import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarOutline
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.toColorInt
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.safewalk.R
import com.example.safewalk.data.FirestoreRepository
import com.example.safewalk.model.Tramo
import com.example.safewalk.viewmodel.SegmentViewModel
import com.google.firebase.firestore.GeoPoint


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportScreen(navController: NavController, segmentViewModel: SegmentViewModel) {



    val puntoA = segmentViewModel.puntoA
    val puntoB = segmentViewModel.puntoB


    var rating by rememberSaveable { mutableStateOf(0) }
    var comment by rememberSaveable { mutableStateOf("") }

    val repo = FirestoreRepository()

    Scaffold(
        containerColor = Color.White,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Reportar Segmento", color = Color.Black, fontSize = 22.sp, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigate("home") }) {
                        Icon(Icons.Default.Close, contentDescription = "Cerrar", tint = Color.Black)
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
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {

            // Título principal
            Text(
                text = "Evaluá este segmento de Ruta",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Subtítulo
            Text(
                text = "¿Cómo calificarías este segmento?",
                fontSize = 16.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(16.dp))

            // ⭐ Valoración 1-5
            Row {
                for (i in 1..5) {
                    IconButton(onClick = { rating = i }) {
                        Icon(
                            imageVector = if (i <= rating) Icons.Default.Star else Icons.Default.StarOutline,
                            contentDescription = "Estrella $i",
                            tint = Color("#0065C2".toColorInt())
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Box para comentarios
            OutlinedTextField(
                value = comment,
                onValueChange = { comment = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                placeholder = { Text("Escribe tus comentarios…") },
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

            Spacer(modifier = Modifier.height(32.dp))

            // Botón de confirmar
            Button(
                onClick = {
                    if (puntoA != null && puntoB != null) {
                        val tramo = Tramo(
                            puntoA = GeoPoint(puntoA.latitude, puntoA.longitude),
                            puntoB = GeoPoint(puntoB.latitude, puntoB.longitude),
                            usuarioId = "TEST_USER"
                        )


                        Log.d("REPORT", "Enviando tramo: $tramo")

                        repo.guardarTramo(tramo) { ok ->
                            Log.d("REPORT", "Resultado guardarTramo: $ok")
                            if (ok) {
                                navController.navigate("home")
                            } else {
                                Toast.makeText(
                                    navController.context,
                                    "Error al guardar tramo",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    } else {
                        Log.d("REPORT", "puntoA o puntoB nulos")
                    }
                }
            ) {
                Text("Enviar Reporte")
            }

        }
    }
}
