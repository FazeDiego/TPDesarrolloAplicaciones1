package com.example.safewalk.model

data class Usuario(
    val correo: String = "",
    val createdAt: Long = 0L,
    val nombre: String = "",
    val uid: String = "",
    val photoUrl: String? = null,
    val photoBase64: String? = null
)
