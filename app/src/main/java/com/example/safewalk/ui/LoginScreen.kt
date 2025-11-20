package com.example.safewalk.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.safewalk.R
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.core.graphics.toColorInt
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

// Función helper para traducir errores de Firebase al español
private fun getFirebaseErrorMessage(exception: Exception?): String {
    val errorMessage = exception?.message ?: ""
    return when {
        errorMessage.contains("badly formatted", ignoreCase = true) ||
        errorMessage.contains("invalid-email", ignoreCase = true) ->
            "El formato del correo electrónico es incorrecto"

        errorMessage.contains("INVALID_LOGIN_CREDENTIALS", ignoreCase = true) ||
        errorMessage.contains("wrong-password", ignoreCase = true) ||
        errorMessage.contains("user-not-found", ignoreCase = true) ->
            "Correo o contraseña incorrectos"

        errorMessage.contains("network", ignoreCase = true) ->
            "Error de conexión. Verifica tu internet"

        errorMessage.contains("too-many-requests", ignoreCase = true) ->
            "Demasiados intentos. Intenta más tarde"

        else -> "Error al iniciar sesión. Intenta nuevamente"
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(navController: NavController) {

    var nombre by rememberSaveable  { mutableStateOf("") }
    var correo by rememberSaveable  { mutableStateOf("") }
    var contraseña by rememberSaveable  { mutableStateOf("") }
    var confirmarContraseña by rememberSaveable  { mutableStateOf("") }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var passwordVisible by rememberSaveable  { mutableStateOf(false) }
    val auth = FirebaseAuth.getInstance()

    // Estados para recuperar contraseña
    var showResetPasswordDialog by remember { mutableStateOf(false) }
    var resetEmail by remember { mutableStateOf("") }


    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.White)
    ) {
        Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 60.dp)
                        .verticalScroll(rememberScrollState())   // 👈 hace scroll
                        .imePadding(),                            // 👈 levanta todo con el teclado
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Text(
                        text = "Iniciar Sesion",
                        color = Color.Black,
                        fontSize = 25.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        fontFamily = Archivo
                    )

                    Image(
                        painter = painterResource(R.drawable.logo),
                        contentDescription = "Logo App",
                        modifier = Modifier.size(200.dp)
                    )

                    Text(
                        text = "SafeWalk",
                        textAlign = TextAlign.Center,
                        color = Color("#0065C2".toColorInt()),
                        fontSize = 25.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.fillMaxWidth(0.9f)
                    )

                    Spacer(modifier = Modifier.height(15.dp))

                    Text(
                        text = "Usuario",
                        color = Color.Black,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.fillMaxWidth(0.9f)
                    )
                    Spacer(modifier = Modifier.height(5.dp))

                    OutlinedTextField(
                        value = correo,
                        onValueChange = { correo = it },
                        placeholder = { Text("correo@ejemplo.com") },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            disabledContainerColor = Color.Transparent,
                            errorContainerColor = Color.Transparent,
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black,
                            cursorColor = Color.Black,
                            focusedPlaceholderColor = Color.Gray,
                            unfocusedPlaceholderColor = Color.Gray
                        ),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email
                        ),
                        modifier = Modifier.fillMaxWidth(0.9f)
                    )


                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = "Contraseña",
                        color = Color.Black,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.fillMaxWidth(0.9f)
                    )
                    Spacer(modifier = Modifier.height(5.dp))

                    OutlinedTextField(
                        value = contraseña,
                        onValueChange = { contraseña = it },
                        placeholder = { Text("**************") },
                        visualTransformation = if (passwordVisible)
                            VisualTransformation.None
                        else
                            PasswordVisualTransformation(),
                        trailingIcon = {
                            val icon = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(icon, contentDescription = null, tint = Color.Gray)
                            }
                        },
                                colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            disabledContainerColor = Color.Transparent,
                            errorContainerColor = Color.Transparent,
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black,
                            cursorColor = Color.Black,
                            focusedPlaceholderColor = Color.Gray,
                            unfocusedPlaceholderColor = Color.Gray
                        ),
                        modifier = Modifier.fillMaxWidth(0.9f)
                    )


                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = {
                            if (correo.isBlank() || contraseña.isBlank()) {
                                scope.launch {
                                    snackbarHostState.showSnackbar("Por favor completa todos los campos")
                                }
                            } else {
                                auth.signInWithEmailAndPassword(correo, contraseña)
                                    .addOnCompleteListener { task ->
                                        if (task.isSuccessful) {
                                            // LOGIN OK → pasar a Home
                                            navController.navigate("home") {
                                                popUpTo("loginScreen") { inclusive = true }
                                            }
                                        } else {
                                            // ERROR → mostrar mensaje traducido
                                            scope.launch {
                                                snackbarHostState.showSnackbar(
                                                    getFirebaseErrorMessage(task.exception)
                                                )
                                            }
                                        }
                                    }
                            }
                        },
                                colors = ButtonDefaults.buttonColors(
                            containerColor = Color("#0065C2".toColorInt()),
                        ),
                        modifier = Modifier
                            .fillMaxWidth(0.9f)
                            .padding(top = 24.dp)
                    ) {
                        Text("Iniciar Sesion", color = Color.White)
                    }

                    Spacer(modifier = Modifier.height(5.dp))

                    Text(
                        text = "¿Olvidaste tu contraseña?",
                        color = Color("#0065C2".toColorInt()),
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.clickable {
                            showResetPasswordDialog = true
                        }
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = "¿No tenes una cuenta de Safe Walk? Registrate",
                        color = Color("#0065C2".toColorInt()),
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.clickable {
                            navController.navigate("registerScreen")
                        }
                    )

                    Spacer(modifier = Modifier.height(40.dp)) // 👈 evita cortes al final
                }

                // SnackbarHost en la parte superior
                SnackbarHost(
                    hostState = snackbarHostState,
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 16.dp, start = 16.dp, end = 16.dp)
                ) { data ->
                    Snackbar(
                        snackbarData = data,
                        containerColor = Color("#D32F2F".toColorInt()),
                        contentColor = Color.White
                    )
                }

                // Diálogo para recuperar contraseña
                if (showResetPasswordDialog) {
                    AlertDialog(
                        onDismissRequest = {
                            showResetPasswordDialog = false
                            resetEmail = ""
                        },
                        title = { Text("Recuperar contraseña") },
                        text = {
                            Column {
                                Text("Ingresa tu correo electrónico y te enviaremos un enlace para restablecer tu contraseña.")
                                Spacer(modifier = Modifier.height(8.dp))
                                OutlinedTextField(
                                    value = resetEmail,
                                    onValueChange = { resetEmail = it },
                                    label = { Text("Correo electrónico") },
                                    placeholder = { Text("correo@ejemplo.com") },
                                    keyboardOptions = KeyboardOptions(
                                        keyboardType = KeyboardType.Email
                                    ),
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        },
                        confirmButton = {
                            TextButton(
                                onClick = {
                                    if (resetEmail.isBlank()) {
                                        scope.launch {
                                            snackbarHostState.showSnackbar("Por favor ingresa tu correo")
                                        }
                                    } else {
                                        auth.sendPasswordResetEmail(resetEmail)
                                            .addOnCompleteListener { task ->
                                                if (task.isSuccessful) {
                                                    scope.launch {
                                                        snackbarHostState.showSnackbar(
                                                            "Correo enviado. Revisa tu bandeja de entrada"
                                                        )
                                                    }
                                                    showResetPasswordDialog = false
                                                    resetEmail = ""
                                                } else {
                                                    scope.launch {
                                                        snackbarHostState.showSnackbar(
                                                            getFirebaseErrorMessage(task.exception)
                                                        )
                                                    }
                                                }
                                            }
                                    }
                                }
                            ) {
                                Text("Enviar")
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = {
                                showResetPasswordDialog = false
                                resetEmail = ""
                            }) {
                                Text("Cancelar")
                            }
                        }
                    )
                }
            }
        }
    