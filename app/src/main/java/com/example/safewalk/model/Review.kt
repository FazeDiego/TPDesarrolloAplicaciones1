package com.example.safewalk.model

import com.google.firebase.Timestamp
import java.util.Date

data class Review(
    val comentario: String = "",
    val fecha: Timestamp = Timestamp(Date()),
    val rating: Int = 0,
    val tramoId: String = "",
    val usuarioId: String = ""
)
