package com.example.safewalk.ui

import android.net.Uri
import android.util.Base64
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.toColorInt
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.safewalk.data.FirestoreRepository
import com.example.safewalk.model.Review
import com.example.safewalk.model.Tramo
import com.example.safewalk.viewmodel.SegmentViewModel
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.GeoPoint
import android.graphics.BitmapFactory
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.graphics.Color
import java.io.ByteArrayOutputStream
import java.io.InputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportScreen(navController: NavController, segmentViewModel: SegmentViewModel) {

    val puntoA = segmentViewModel.puntoA
    val puntoB = segmentViewModel.puntoB
    val calleA = segmentViewModel.nombreCalleA
    val calleB = segmentViewModel.nombreCalleB

    var rating by rememberSaveable { mutableStateOf(0) }
    var comment by rememberSaveable { mutableStateOf("") }

    var reviewPhotoUri by remember { mutableStateOf<Uri?>(null) }
    var reviewBitmapBase64 by remember { mutableStateOf<String?>(null) }

    val context = LocalContext.current
    val repo = remember { FirestoreRepository() }




    // Función helper: comprimir y convertir foto a Base64
    fun processReviewPhoto(uri: Uri, context: android.content.Context, callback: (String?) -> Unit) {
        try {
            val input: InputStream = context.contentResolver.openInputStream(uri) ?: return callback(null)
            val options = BitmapFactory.Options().apply { inJustDecodeBounds = false }
            var bitmap = BitmapFactory.decodeStream(input, null, options)
            input.close()
            if (bitmap == null) return callback(null)

            // Redimensionar si es muy grande
            val maxWidth = 720
            if (bitmap.width > maxWidth) {
                val ratio = bitmap.width.toFloat() / bitmap.height
                val targetW = maxWidth
                val targetH = (targetW / ratio).toInt()
                bitmap = android.graphics.Bitmap.createScaledBitmap(bitmap, targetW, targetH, true)
            }

            val bos = ByteArrayOutputStream()
            bitmap.compress(android.graphics.Bitmap.CompressFormat.JPEG, 85, bos)
            val bytes = bos.toByteArray()
            val base64 = Base64.encodeToString(bytes, Base64.DEFAULT)
            callback(base64)
        } catch (e: Exception) {
            e.printStackTrace()
            callback(null)
        }
    }

    val imagePicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let { processReviewPhoto(it, context) { base64 -> reviewBitmapBase64 = base64; reviewPhotoUri = it } }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Reportar Segmento",
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigate("home") }) {
                        Icon(Icons.Default.Close, contentDescription = "Cerrar", tint = MaterialTheme.colorScheme.onSurface)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                    navigationIconContentColor = MaterialTheme.colorScheme.onSurface
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

            Text(
                text = "Evaluá este segmento de Ruta",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "¿Cómo calificarías este segmento?",
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
            Spacer(modifier = Modifier.height(16.dp))

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

            // Botón para agregar foto
            Button(
                onClick = { imagePicker.launch("image/*") },
                colors = ButtonDefaults.buttonColors(containerColor = Color("#0065C2".toColorInt()))
            ) {
                Text("Agregar Foto", color = Color.White)
            }

            reviewPhotoUri?.let { uri ->
                Spacer(modifier = Modifier.height(16.dp))
                Image(
                    painter = rememberAsyncImagePainter(uri),
                    contentDescription = "Foto seleccionada",
                    modifier = Modifier.size(180.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = comment,
                onValueChange = { comment = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                placeholder = { Text("Escribe tus comentarios…") },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF0065C2),
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                    cursorColor = MaterialTheme.colorScheme.onSurface,
                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                    unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                    focusedPlaceholderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    unfocusedPlaceholderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    if (puntoA != null && puntoB != null) {
                        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: "UNKNOWN_USER"
                        val geoA = GeoPoint(puntoA.latitude, puntoA.longitude)
                        val geoB = GeoPoint(puntoB.latitude, puntoB.longitude)

                        repo.buscarTramoExistente(geoA, geoB) { tramoExistenteId ->

                            fun guardarReviewFinal(tramoId: String) {
                                val reviewTemp = Review(
                                    comentario = comment,
                                    fecha = Timestamp.now(),
                                    rating = rating,
                                    tramoId = tramoId,
                                    usuarioId = userId,
                                    id = "",
                                    fotoBase64 = reviewBitmapBase64
                                )

                                repo.guardarReview(reviewTemp) { idReview ->
                                    if (idReview != null) {
                                        // actualizar ID si necesitas
                                        Toast.makeText(context, "Reseña enviada correctamente", Toast.LENGTH_SHORT).show()
                                        repo.actualizarEstadisticasTramo(tramoId, rating)
                                        navController.navigate("home")
                                    } else {
                                        Toast.makeText(context, "Error al guardar reseña", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }

                            if (tramoExistenteId != null) {
                                guardarReviewFinal(tramoExistenteId)
                            } else {
                                val tramoNuevo = Tramo(
                                    uuid = "",
                                    puntoA = geoA,
                                    puntoB = geoB,
                                    usuarioId = userId,
                                    ratingPromedio = 0.0,
                                    cantidadReviews = 0,
                                    nombreCalleA = calleA,
                                    nombreCalleB = calleB
                                )

                                repo.guardarTramo(tramoNuevo) { nuevoTramoId ->
                                    if (nuevoTramoId != null) {
                                        guardarReviewFinal(nuevoTramoId)
                                    } else {
                                        Toast.makeText(context, "Error al guardar tramo", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                        }
                    } else {
                        Log.d("REPORT", "puntoA o puntoB nulos")
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color("#0065C2".toColorInt())),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
            ) {
                Text("Enviar Reseña", color = Color.White)
            }
        }
    }
}
