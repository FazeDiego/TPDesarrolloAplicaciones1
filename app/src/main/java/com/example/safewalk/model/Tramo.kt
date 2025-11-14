package com.example.safewalk.model

import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

data class Tramo(
    val puntoA: GeoPoint = GeoPoint(0.0, 0.0),
    val puntoB: GeoPoint = GeoPoint(0.0, 0.0),
    val usuarioId: String = ""
)


fun guardarTramo(tramo: Tramo, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
    val db = Firebase.firestore
    db.collection("tramos")
        .add(tramo)
        .addOnSuccessListener { onSuccess() }
        .addOnFailureListener { exception -> onFailure(exception) }
}
