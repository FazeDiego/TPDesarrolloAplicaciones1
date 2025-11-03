package com.example.safewalk.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnboardingScreen4(onNextClick: () -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    var selectedCountry by remember { mutableStateOf("Seleccionar país") }

    val countries = listOf(
        "Argentina", "Brasil", "Chile", "Uruguay", "Paraguay",
        "México", "España", "Estados Unidos", "Colombia", "Perú"
    )
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = Color.White
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.TopCenter
                ) {
                    Text(
                        text = "Vamos a comenzar\n¿Dónde te encuentras?",

                        style = MaterialTheme.typography.headlineMedium,
                        color = Color.Black,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center

                ) {
                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = !expanded }
                    ) {
                        TextField(
                            value = selectedCountry,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("País") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                            colors = ExposedDropdownMenuDefaults.textFieldColors(
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White,
                                focusedTextColor = Color.Black,
                                unfocusedTextColor = Color.Black,
                                cursorColor = Color(0xFF9C64D6),
                                focusedLabelColor = Color(0xFF9C64D6),
                                unfocusedLabelColor = Color.Gray,
                                focusedTrailingIconColor = Color(0xFF9C64D6),
                                unfocusedTrailingIconColor = Color(0xFF9C64D6)
                            ),
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth(0.8f)
                        )


                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false },
                        ) {
                            countries.forEach { country ->
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            country,
                                            color = Color.Black
                                        )
                                    },
                                    onClick = {
                                        selectedCountry = country
                                        expanded = false
                                    },
                                    modifier = Modifier.background(Color.White)
                                )
                            }
                        }

                    }
                }


                // Botón abajo
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.BottomEnd
                ) {

                    Button(
                        onClick = {
                            if (selectedCountry == "Seleccionar país") {
                                scope.launch {
                                    snackbarHostState.showSnackbar("Por favor selecciona un país antes de continuar")
                                }
                            } else {
                                onNextClick()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF9C64D6).copy(alpha = 0.7f)
                        )
                    ) {
                        Text("Siguiente", color = Color.White)
                    }
                }

            }

        }
    }

}
