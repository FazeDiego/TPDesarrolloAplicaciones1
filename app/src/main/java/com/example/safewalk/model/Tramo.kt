package com.example.safewalk.model

import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

data class Tramo(
    var uuid: String = "",
    val puntoA: GeoPoint = GeoPoint(0.0, 0.0),
    val puntoB: GeoPoint = GeoPoint(0.0, 0.0),
    val usuarioId: String = "",
    var ratingPromedio: Double = 0.0,
    var cantidadReviews: Int = 0,
    var nombreCalleA: String = "",
    var nombreCalleB: String = ""
)


fun guardarTramo(tramo: Tramo, onSuccess: (String) -> Unit, onFailure: (Exception) -> Unit) {
    val db = Firebase.firestore

    val nuevoDoc = db.collection("tramos").document()

    tramo.uuid = nuevoDoc.id


    nuevoDoc
        .set(tramo)
        .addOnSuccessListener { onSuccess(nuevoDoc.id) }
        .addOnFailureListener { e -> onFailure(e) }
}
