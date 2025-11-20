package com.example.safewalk.model

import com.google.firebase.Timestamp
import java.util.Date

data class Review(
    var comentario: String = "",
    var fecha: Timestamp = Timestamp(Date()),
    var rating: Int = 0,
    var tramoId: String = "",
    var usuarioId: String = "",
    var id: String = "",
    var fotoBase64: String? = null
)

