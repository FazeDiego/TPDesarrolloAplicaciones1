package com.example.safewalk.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items


import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import coil.compose.AsyncImage
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.safewalk.R
import com.example.safewalk.data.AuthRepository
import com.example.safewalk.data.FirestoreRepository
import com.example.safewalk.model.Review
import com.example.safewalk.model.Usuario
import com.google.firebase.firestore.ktx.firestore
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.platform.LocalContext
import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import java.io.ByteArrayOutputStream
import java.io.InputStream


data class ReportItem(val title: String, val stars: Int)


@Composable
fun ReportCard(report: ReportItem, onEdit: () -> Unit, onDelete: () -> Unit) {
	Card(shape = RoundedCornerShape(10.dp), modifier = Modifier.fillMaxWidth()) {
		Row(modifier = Modifier
			.fillMaxWidth()
			.padding(12.dp),
			verticalAlignment = Alignment.CenterVertically
		) {

			Column(modifier = Modifier.weight(1f)) {
				Text(text = report.title, style = MaterialTheme.typography.bodyLarge)
				Spacer(modifier = Modifier.height(6.dp))
				RatingStars(rating = report.stars)
			}

			Row {
				IconButton(onClick = onEdit) {
					Icon(Icons.Default.Star, contentDescription = "Editar")
				}
				IconButton(onClick = onDelete) {
					Icon(Icons.Default.Delete, contentDescription = "Eliminar")
				}
			}

		}
	}
}

@Composable
fun RatingStars(rating: Int, max: Int = 5) {
	Row {
		for (i in 1..max) {
			if (i <= rating) {
				Icon(Icons.Filled.Star, contentDescription = null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.primary)
			} else {
				Icon(Icons.Outlined.Star, contentDescription = null, modifier = Modifier.size(16.dp))
			}
			Spacer(modifier = Modifier.size(4.dp))
		}
	}
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(navController: NavController) {
	// Repositorios
	val authRepo = remember { AuthRepository() }
	val repo = remember { FirestoreRepository() }

	// Estado local
	var usuario by remember { mutableStateOf<Usuario?>(null) }
	var reports by remember { mutableStateOf<List<Review>>(emptyList()) }
	var loading by remember { mutableStateOf(true) }
	var reportPairs by remember { mutableStateOf<List<Pair<ReportItem, Review>>>(emptyList()) }

	// Estados para editar/borrar
	var editingReview by remember { mutableStateOf<Review?>(null) }
	var showEditDialog by remember { mutableStateOf(false) }
	var editComment by remember { mutableStateOf("") }
	var editRating by remember { mutableStateOf(0) }

	var reviewToDelete by remember { mutableStateOf<Review?>(null) }
	var showDeleteConfirm by remember { mutableStateOf(false) }

	// Settings menu
	var showSettingsMenu by remember { mutableStateOf(false) }

	// Profile photo
	val context = LocalContext.current
	var profilePhotoUri by remember { mutableStateOf<Uri?>(null) }
	var profileBitmap by remember { mutableStateOf<ImageBitmap?>(null) }

	// Helper: compress image to target size (approximate)
	fun compressImageToTargetSize(context: android.content.Context, uri: Uri, targetBytes: Int = 100_000): ByteArray? {
		try {
			val input: InputStream = context.contentResolver.openInputStream(uri) ?: return null
			// decode bounds
			val optionsBounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
			BitmapFactory.decodeStream(input, null, optionsBounds)
			input.close()

			val maxDim = 1024
			var inSampleSize = 1
			val (width, height) = optionsBounds.outWidth to optionsBounds.outHeight
			if (width > maxDim || height > maxDim) {
				val largest = maxOf(width, height)
				inSampleSize = largest / maxDim
				if (inSampleSize <= 0) inSampleSize = 1
			}

			val input2 = context.contentResolver.openInputStream(uri) ?: return null
			val decodeOptions = BitmapFactory.Options().apply { inSampleSize = inSampleSize }
			var bitmap = BitmapFactory.decodeStream(input2, null, decodeOptions)
			input2.close()
			if (bitmap == null) return null

			// scale if still large
			val maxWidth = 720
			if (bitmap.width > maxWidth) {
				val ratio = bitmap.width.toFloat() / bitmap.height
				val targetW = maxWidth
				val targetH = (targetW / ratio).toInt()
				bitmap = android.graphics.Bitmap.createScaledBitmap(bitmap, targetW, targetH, true)
			}

			val bos = ByteArrayOutputStream()
			var quality = 85
			bitmap.compress(android.graphics.Bitmap.CompressFormat.JPEG, quality, bos)
			var bytes = bos.toByteArray()
			while (bytes.size > targetBytes && quality >= 40) {
				bos.reset()
				quality -= 10
				bitmap.compress(android.graphics.Bitmap.CompressFormat.JPEG, quality, bos)
				bytes = bos.toByteArray()
			}
			return bytes
		} catch (e: Exception) {
			return null
		}
	}

	val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
		if (uri != null) {
			val userId = authRepo.currentUser()?.uid ?: return@rememberLauncherForActivityResult
			// compress and store as Base64 in Firestore (ok for a few small images)
			val bytes = compressImageToTargetSize(context, uri, targetBytes = 100_000)
			if (bytes != null) {
				val base64 = Base64.encodeToString(bytes, Base64.DEFAULT)
				val db = com.google.firebase.ktx.Firebase.firestore
				db.collection("usuarios").document(userId)
					.update(mapOf("photoBase64" to base64))
					.addOnSuccessListener {
						// update local UI: decode bytes to bitmap
						try {
							val bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
							profileBitmap = bmp.asImageBitmap()
							profilePhotoUri = uri
						} catch (_: Exception) { profilePhotoUri = uri }
					}
					.addOnFailureListener {
						// fallback: show local uri
						profilePhotoUri = uri
					}
			} else {
				profilePhotoUri = uri
			}
		}
	}

	// obtener uid del usuario logueado
	val uid = authRepo.currentUser()?.uid

	// función para cargar reviews y resolver nombres de tramos
	fun loadReviews() {
		if (uid == null) {
			loading = false
			return
		}

		// obtener usuario
		repo.obtenerUsuario(uid) { u ->
			usuario = u
			// si el usuario ya tiene photoBase64 guardada en Firestore, decodificar y mostrarla
			u?.photoBase64?.let { b64 ->
				try {
					val decoded = Base64.decode(b64, Base64.DEFAULT)
					val bmp = BitmapFactory.decodeByteArray(decoded, 0, decoded.size)
					profileBitmap = bmp.asImageBitmap()
					profilePhotoUri = null
				} catch (e: Exception) {
					// ignore malformed
				}
			} ?: run {
				// si no hay base64, intentar cargar photoUrl si existe
				u?.photoUrl?.let { url ->
					try { profilePhotoUri = Uri.parse(url) } catch (e: Exception) {}
				}
			}
		}

		// obtener reviews del usuario
		repo.obtenerReviewsUsuario(uid) { list ->
			reports = list
			// ahora resolvemos los tramos asociados para mostrar nombres de calles
			if (list.isEmpty()) {
				reportPairs = emptyList()
				loading = false
			} else {
				val resolved = mutableListOf<Pair<ReportItem, Review>>()
				var remaining = list.size
				list.forEach { review ->
					repo.obtenerTramoPorId(review.tramoId) { tramo ->
						val title =
							if (tramo != null && (tramo.nombreCalleA.isNotBlank() || tramo.nombreCalleB.isNotBlank())) {
								val a = tramo.nombreCalleA.ifBlank { "" }
								val b = tramo.nombreCalleB.ifBlank { "" }
								when {
									a.isNotBlank() && b.isNotBlank() -> "$a - $b"
									a.isNotBlank() -> a
									b.isNotBlank() -> b
									else -> review.comentario.ifBlank { review.tramoId }
								}
							} else {
								// fallback a comentario o tramoId
								review.comentario.ifBlank { review.tramoId }
							}

						resolved.add(Pair(ReportItem(title, review.rating), review))
						remaining -= 1
						if (remaining == 0) {
							reportPairs = resolved.toList()
							loading = false
						}
					}
				}
			}
		}
	}

	LaunchedEffect(uid) { loadReviews() }

	Scaffold(
		topBar = {
			TopAppBar(
				title = { Text(text = "Mi Perfil") },
				actions = {
					// Settings icon with dropdown menu
					Box {
						IconButton(onClick = { showSettingsMenu = true }) {
							Icon(Icons.Default.Settings, contentDescription = "Ajustes")
						}
						DropdownMenu(
							expanded = showSettingsMenu,
							onDismissRequest = { showSettingsMenu = false }
						) {
							DropdownMenuItem(text = { Text("Cerrar sesión") }, onClick = {
								// perform logout and navigate to login screen, clearing backstack
								authRepo.logout()
								showSettingsMenu = false
								navController.navigate("loginScreen") {
									popUpTo("home") { inclusive = true }
								}
							})

							// Placeholder for other settings options
							DropdownMenuItem(text = { Text("Editar perfil") }, onClick = {
								// TODO: navegar a pantalla de edición de perfil
								showSettingsMenu = false
							})
						}
					}
				}
			)
		},
		bottomBar = {
			NavigationBar(containerColor = Color.White) {
				NavigationBarItem(
					selected = false,
					onClick = { navController.navigate("home") },
					icon = { Icon(Icons.Default.Map, contentDescription = "Mapa") },
					label = { Text("Mapa") }
				)
				NavigationBarItem(
					selected = false,
					onClick = { navController.navigate("reportScreen") },
					icon = { Icon(Icons.Default.Checklist, contentDescription = "Reportes") },
					label = { Text("Reportes") }
				)
				NavigationBarItem(
					selected = false,
					onClick = {},
					icon = { Icon(Icons.Default.Person, contentDescription = "Perfil") },
					label = { Text("Perfil") }
				)
			}
		},
	) { innerPadding ->
		Surface(
			modifier = Modifier
				.padding(innerPadding)
				.fillMaxSize(),
			color = Color.Black.copy(alpha = 0.05f)
		) {

			// Usamos un único LazyColumn para el header + lista de reviews — evita necesidad de weight()
			LazyColumn(
				modifier = Modifier
					.fillMaxSize(),
				verticalArrangement = Arrangement.spacedBy(8.dp)
			) {
				// Header: tarjeta con avatar y nombre
				item {
					Card(
						modifier = Modifier
							.fillMaxWidth()
							.padding(horizontal = 16.dp, vertical = 8.dp),
						shape = RoundedCornerShape(12.dp)
					) {
						Row(
							modifier = Modifier
								.fillMaxWidth()
								.padding(16.dp),
							verticalAlignment = Alignment.CenterVertically
						) {
							Column(horizontalAlignment = Alignment.CenterHorizontally) {
								Box(
									modifier = Modifier
										.size(64.dp)
										.clip(CircleShape)
										.background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
									contentAlignment = Alignment.Center
								) {
									when {
										profileBitmap != null -> {
											Image(
												bitmap = profileBitmap!!,
												contentDescription = "Avatar",
												modifier = Modifier
													.size(64.dp)
													.clip(CircleShape),
												contentScale = ContentScale.Crop
											)
										}
										profilePhotoUri != null -> {
											AsyncImage(
												model = profilePhotoUri,
												contentDescription = "Avatar",
												modifier = Modifier
													.size(64.dp)
													.clip(CircleShape),
												contentScale = ContentScale.Crop,
												placeholder = painterResource(id = R.drawable.logo),
												error = painterResource(id = R.drawable.logo)
											)
										}
										else -> {
											Image(
												painter = painterResource(id = R.drawable.logo),
												contentDescription = "Avatar",
												modifier = Modifier
													.size(64.dp)
													.clip(CircleShape),
												contentScale = ContentScale.Crop
											)
										}
									}
								}

								Spacer(modifier = Modifier.height(6.dp))

								Text(
									text = "Cambiar foto",
									style = MaterialTheme.typography.labelSmall,
									color = MaterialTheme.colorScheme.primary,
									modifier = Modifier
										.clickable { launcher.launch("image/*") }
								)
							}

							Spacer(modifier = Modifier.size(12.dp))

							Column(modifier = Modifier.weight(1.5f)) {
								Text(
									text = usuario?.nombre ?: "Usuario",
									style = MaterialTheme.typography.bodyLarge
								)
							}

						}
					}
				}

				// Título sección
				item {
					Column(modifier = Modifier.padding(horizontal = 16.dp)) {
						Spacer(modifier = Modifier.height(8.dp))
						Text(text = "Mis Reportes", style = MaterialTheme.typography.titleLarge)
						Spacer(modifier = Modifier.height(8.dp))
					}
				}

				// Contenido según estado
				if (loading) {
					item {
						Text(text = "Cargando...", modifier = Modifier.padding(16.dp))
					}
				} else if (reports.isEmpty()) {
					item {
						Text(text = "No hay reviews", modifier = Modifier.padding(16.dp))
					}
				} else {
					items(reportPairs) { pair ->
						val report = pair.first
						val review = pair.second
						ReportCard(
							report,
							onEdit = {
								editingReview = review
								editComment = review.comentario
								editRating = review.rating
								showEditDialog = true
							},
							onDelete = {
								reviewToDelete = review
								showDeleteConfirm = true
							})
					}
				}

				// Dialogs as items to keep them in same scope
				item {
					if (showEditDialog && editingReview != null) {
						androidx.compose.material3.AlertDialog(
							onDismissRequest = { showEditDialog = false; editingReview = null },
							title = { Text("Editar reseña") },
							text = {
								Column {
									OutlinedTextField(
										value = editComment,
										onValueChange = { editComment = it },
										label = { Text("Comentario") }
									)
									Spacer(modifier = Modifier.height(8.dp))
									Row {
										for (i in 1..5) {
											IconButton(onClick = { editRating = i }) {
												Icon(
													imageVector = Icons.Default.Star,
													contentDescription = "Estrella",
													tint = if (i <= editRating) MaterialTheme.colorScheme.primary else Color.Gray
												)
											}
										}
									}
								}
							},
							confirmButton = {
								androidx.compose.material3.TextButton(onClick = {
									val r = editingReview!!
									repo.actualizarReview(
										r.id,
										editComment,
										editRating,
										r.tramoId
									) { ok ->
										if (ok) {
											// recargar lista
											loadReviews()
										}
									}
									showEditDialog = false
									editingReview = null
								}) { Text("Guardar") }
							},
							dismissButton = {
								androidx.compose.material3.TextButton(onClick = {
									showEditDialog = false; editingReview = null
								}) { Text("Cancelar") }
							}
						)
					}

					if (showDeleteConfirm && reviewToDelete != null) {
						androidx.compose.material3.AlertDialog(
							onDismissRequest = { showDeleteConfirm = false; reviewToDelete = null },
							title = { Text("Eliminar reseña") },
							text = { Text("¿Eliminar esta reseña? Esta acción no se puede deshacer.") },
							confirmButton = {
								androidx.compose.material3.TextButton(onClick = {
									val r = reviewToDelete!!
									repo.eliminarReview(r.id, r.tramoId) { ok ->
										if (ok) loadReviews()
									}
									showDeleteConfirm = false
									reviewToDelete = null
								}) { Text("Eliminar") }
							},
							dismissButton = {
								androidx.compose.material3.TextButton(onClick = {
									showDeleteConfirm = false; reviewToDelete = null
								}) { Text("Cancelar") }
							}
						)
					}
				}
			}

		}
	}
}



// Helper: convertir Review (modelo Firestore) a ReportItem usado por la UI
fun reviewToReportItem(r: Review): ReportItem {
	val title = when {
		r.tramoId.isNotBlank() -> r.tramoId
		r.comentario.isNotBlank() -> r.comentario
		else -> "Reporte"
	}
	return ReportItem(title, r.rating)
}
