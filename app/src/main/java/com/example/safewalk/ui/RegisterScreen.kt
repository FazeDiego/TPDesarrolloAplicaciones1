package com.example.safewalk.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.safewalk.R
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.core.graphics.toColorInt
import androidx.navigation.NavController
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(navController: NavController) {

    var nombre by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }
    var contraseña by remember { mutableStateOf("") }
    var confirmarContraseña by remember { mutableStateOf("") }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()




    Scaffold(
        topBar = {
            Column {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            text = "Registro",
                            color = Color.Black,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            fontFamily = Archivo
                        )
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = Color.Transparent,
                        titleContentColor = Color.Black
                    )
                )

                // 👇 Línea separadora debajo del título
                Divider(
                    color = Color(0xFFBDBDBD), // gris suave, podés ajustar el tono
                    thickness = 1.dp,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        content = { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = Color.White)
                    .padding(paddingValues)
            ) {

                Column(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .verticalScroll(rememberScrollState())
                        .padding(top = 20.dp),

                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {

                    Text(
                        text = "¡Crea tu cuenta ahora!",
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
                        text = "Nombre",
                        color = Color.Black,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.fillMaxWidth(0.9f)
                    )
                    Spacer(modifier = Modifier.height(5.dp))

                    OutlinedTextField(
                        value = nombre,
                        onValueChange = { nombre = it },
                        placeholder = { Text("Ingresá tu nombre") },
                        modifier = Modifier.fillMaxWidth(0.9f)
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = "Correo Electronico",
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
                        visualTransformation = PasswordVisualTransformation(),
                        onValueChange = { contraseña = it },
                        placeholder = { Text("*********") },
                        modifier = Modifier.fillMaxWidth(0.9f)
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = "Confirmar Contraseña",
                        color = Color.Black,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.fillMaxWidth(0.9f)
                    )
                    Spacer(modifier = Modifier.height(5.dp))

                    OutlinedTextField(

                        value = confirmarContraseña,
                        visualTransformation = PasswordVisualTransformation(),
                        onValueChange = { confirmarContraseña = it },
                        placeholder = { Text("*********") },
                        modifier = Modifier.fillMaxWidth(0.9f)

                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Button(
                        onClick = {
                            if (contraseña == "" || correo == ""){
                                scope.launch {
                                    snackbarHostState.showSnackbar("Por favor completa todos los campos antes de continuar")
                                }
                            } else {
                                navController.navigate("home") {  // navega a home
                                    popUpTo("loginScreen") {
                                        inclusive = true
                                    }  // opcional: limpiar backstack
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color("#0065C2".toColorInt()),
                        ),
                        modifier = Modifier
                            .fillMaxWidth(0.6f)
                            .padding(top = 24.dp)
                    ) {
                        Text("Ingresar", color = Color.White)
                    }

                    Spacer(modifier = Modifier.height(5.dp))


                    Text(
                        text = "¿Ya tenés cuenta? Iniciar Sesion",
                        color = Color("#0065C2".toColorInt()),
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.clickable{
                            navController.navigate("loginScreen")
                        }
                    )

                }




            }
        }
    )
}
