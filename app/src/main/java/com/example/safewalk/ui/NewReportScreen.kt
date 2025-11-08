package com.example.safewalk.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.toColorInt
import androidx.navigation.NavController
import com.example.safewalk.R



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewReportScreen(navController: NavController) {
    var puntoA by remember { mutableStateOf("") }
    var puntoB by remember { mutableStateOf("") }
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Seleccionar puntos de Ruta",
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigate("home") }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Volver",
                            modifier = Modifier.size(28.dp), // 👈 tamaño visible cómodo
                            tint = Color.Black
                        )
                    }
                }

            )
        },
        content = { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.TopCenter
            ) {
                Column(
                    horizontalAlignment = Alignment.Start,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                            .border(
                                width = 1.dp,
                                color = Color.Black.copy(alpha = 0.1f), // línea casi transparente
                                shape = RoundedCornerShape(8.dp)
                            )
                            .padding(12.dp)
                    )
                        {
                            Column {
                                Text("Punto A", color = Color.Black)
                                Spacer(modifier = Modifier.height(5.dp))
                                OutlinedTextField(
                                    value = puntoA,
                                    onValueChange = { puntoA = it },
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }

                    Spacer(modifier = Modifier.height(15.dp))


                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                            .border(
                                width = 1.dp,
                                color = Color.Black.copy(alpha = 0.1f), // línea casi transparente
                                shape = RoundedCornerShape(8.dp)
                            )
                            .padding(12.dp)
                    )
                    {
                        Column {
                            Text("Punto B", color = Color.Black)
                            Spacer(modifier = Modifier.height(5.dp))
                            OutlinedTextField(
                                value = puntoB,
                                onValueChange = { puntoB = it },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }


                    Box(modifier = Modifier.fillMaxSize()){
                        Button(
                            onClick = { navController.navigate("reportSegmentScreen") },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color("#0065C2".toColorInt()),
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding( 24.dp)
                                .align (Alignment.BottomCenter)
                        ) {
                            Text("Confirmar Seleccion", color = Color.White)
                        }

                    }



                    }





            }
        }

    )
}
