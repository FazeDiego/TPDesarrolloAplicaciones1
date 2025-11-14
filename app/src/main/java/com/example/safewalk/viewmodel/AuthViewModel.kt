package com.example.safewalk.viewmodel

import androidx.lifecycle.ViewModel
import com.example.safewalk.data.AuthRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class AuthViewModel(
    private val repository: AuthRepository = AuthRepository(),
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) : ViewModel() {

    private val _authState = MutableStateFlow<String?>(null)
    val authState: StateFlow<String?> get() = _authState

    fun register(nombre: String, correo: String, contraseña: String, confirmar: String) {
        if (nombre.isBlank() || correo.isBlank() || contraseña.isBlank() || confirmar.isBlank()) {
            _authState.value = "Completa todos los campos"
            return
        }

        if (contraseña != confirmar) {
            _authState.value = "Las contraseñas no coinciden"
            return
        }

        repository.register(correo, contraseña) { success, error ->
            if (success) {
                val uid = repository.currentUser()?.uid ?: return@register
                val data = mapOf(
                    "uid" to uid,
                    "nombre" to nombre,
                    "correo" to correo,
                    "createdAt" to System.currentTimeMillis()
                )

                db.collection("usuarios").document(uid).set(data)
                    .addOnSuccessListener { _authState.value = "SUCCESS" }
                    .addOnFailureListener { e -> _authState.value = "Error al guardar datos: ${e.message}" }
            } else {
                _authState.value = "Error: $error"
            }
        }
    }
}
