package com.example.safewalk.data

import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest

class AuthRepository(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) {

    fun register(email: String, password: String, callback: (Boolean, String?) -> Unit) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener { callback(true, null) }
            .addOnFailureListener { e -> callback(false, e.message) }
    }

    fun login(email: String, password: String, callback: (Boolean, String?) -> Unit) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener { callback(true, null) }
            .addOnFailureListener { e -> callback(false, e.message) }
    }

    fun currentUser() = auth.currentUser
    fun logout() {
        auth.signOut()
        TramoCache.clear() // limpiar tramos de la sesión anterior
    }


    fun updateProfilePhoto(url: String, onResult: (Boolean) -> Unit) {
        val user = auth.currentUser
        if (user == null) { onResult(false); return }

        val profileUpdates = com.google.firebase.auth.UserProfileChangeRequest.Builder()
            .setPhotoUri(android.net.Uri.parse(url))
            .build()

        user.updateProfile(profileUpdates)
            .addOnSuccessListener { onResult(true) }
            .addOnFailureListener { onResult(false) }
    }

    fun updateDisplayName(name: String, onResult: (Boolean) -> Unit) {
        val user = auth.currentUser
        if (user == null) { onResult(false); return }

        val profileUpdates = UserProfileChangeRequest.Builder()
            .setDisplayName(name)
            .build()

        user.updateProfile(profileUpdates)
            .addOnSuccessListener { onResult(true) }
            .addOnFailureListener { onResult(false) }
    }

    fun changePassword(newPassword: String, onResult: (Boolean, String?) -> Unit) {
        val user = auth.currentUser
        if (user == null) { onResult(false, "No hay usuario logueado"); return }

        user.updatePassword(newPassword)
            .addOnSuccessListener { onResult(true, null) }
            .addOnFailureListener { e -> onResult(false, e.message) }
    }

    fun deleteAccount(onResult: (Boolean, String?) -> Unit) {
        val user = auth.currentUser
        if (user == null) { onResult(false, "No hay usuario logueado"); return }

        user.delete()
            .addOnSuccessListener { onResult(true, null) }
            .addOnFailureListener { e -> onResult(false, e.message) }
    }
}
