package com.example.safewalk.data

import android.util.Log
import com.example.safewalk.model.Tramo
import com.google.firebase.firestore.FirebaseFirestore

class FirestoreRepository {

    private val db = FirebaseFirestore.getInstance()

    fun guardarTramo(tramo: Tramo, onResult: (Boolean) -> Unit) {
        db.collection("tramos")
            .add(tramo)
            .addOnSuccessListener { documentRef ->
                Log.d("Firestore", "Tramo guardado con ID: ${documentRef.id}")
                onResult(true)
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Error guardando tramo", e)
                onResult(false)
            }
    }
}
