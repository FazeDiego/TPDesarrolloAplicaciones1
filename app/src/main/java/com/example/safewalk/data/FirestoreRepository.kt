package com.example.safewalk.data

import android.location.Location
import android.util.Log
import com.example.safewalk.model.Review
import com.example.safewalk.model.Tramo
import com.google.firebase.Timestamp
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class FirestoreRepository {

    private val db = Firebase.firestore

    // ----------------------------------------
    // 1) Calcular distancia en metros
    // ----------------------------------------
    private fun distanciaMetros(a: GeoPoint, b: GeoPoint): Double {
        val results = FloatArray(1)
        Location.distanceBetween(a.latitude, a.longitude, b.latitude, b.longitude, results)
        return results[0].toDouble()
    }

    // ---------------------------------------------------
    // 2) Buscar tramo existente a menos de 10 metros
    // ---------------------------------------------------
    fun buscarTramoExistente(
        puntoA: GeoPoint,
        puntoB: GeoPoint,
        onResult: (String?) -> Unit
    ) {
        db.collection("tramos")
            .get()
            .addOnSuccessListener { snapshot ->

                val match = snapshot.documents.firstOrNull { doc ->
                    val t = doc.toObject(Tramo::class.java)
                    if (t != null) {
                        val distA = distanciaMetros(t.puntoA, puntoA)
                        val distB = distanciaMetros(t.puntoB, puntoB)
                        distA < 10 && distB < 10       // regla definida por vos
                    } else false
                }

                onResult(match?.id)
            }
            .addOnFailureListener {
                onResult(null)
            }
    }

    // ---------------------------------------------------
    // 3) Guardar tramo — devuelve ID generado
    // ---------------------------------------------------
    fun guardarTramo(
        tramo: Tramo,
        onResult: (String?) -> Unit
    ) {
        val ref = db.collection("tramos").document()   // generamos ID manualmente
        val tramoConUuid = tramo.copy(uuid = ref.id)

        ref.set(tramoConUuid)
            .addOnSuccessListener {
                Log.d("Firestore", "Tramo guardado con ID: ${ref.id}")
                onResult(ref.id)
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Error guardando tramo", e)
                onResult(null)
            }
    }

    // ---------------------------------------------------
    // 4) Guardar review
    // ---------------------------------------------------
    fun guardarReview(review: Review, onResult: (Boolean) -> Unit) {
        db.collection("reviews")
            .add(review)
            .addOnSuccessListener {
                onResult(true)
            }
            .addOnFailureListener {
                onResult(false)
            }
    }

    // ------------------------------------------------------------------
    // 5) Actualizar estadísticas (promedio y cantidad) — TRANSACTION
    // ------------------------------------------------------------------
    fun actualizarEstadisticasTramo(tramoId: String, nuevoRating: Int) {

        val tramoRef = db.collection("tramos").document(tramoId)

        db.runTransaction { tx ->
            val snapshot = tx.get(tramoRef)

            val ratingActual = snapshot.getDouble("ratingPromedio") ?: 0.0
            val cantidadActual = snapshot.getLong("cantidadReviews")?.toInt() ?: 0

            val nuevaCantidad = cantidadActual + 1
            val nuevoPromedio =
                (ratingActual * cantidadActual + nuevoRating) / nuevaCantidad

            tx.update(
                tramoRef,
                mapOf(
                    "ratingPromedio" to nuevoPromedio,
                    "cantidadReviews" to nuevaCantidad
                )
            )

        }.addOnSuccessListener {
            Log.d("Firestore", "Promedio actualizado correctamente")
        }.addOnFailureListener { e ->
            Log.e("Firestore", "Error actualizando estadísticas", e)
        }
    }

    fun obtenerTramosUsuario(userId: String, onResult: (List<Tramo>) -> Unit) {
        val db = Firebase.firestore

        db.collection("tramos")
            .whereEqualTo("usuarioId", userId)
            .get()
            .addOnSuccessListener { result ->
                val lista = result.documents.mapNotNull { doc ->
                    doc.toObject(Tramo::class.java)?.apply {

                        // Guardamos el ID real del documento
                        uuid = doc.id

                        // Defaults por si no existen los campos
                        val data = doc.data ?: emptyMap()
                        ratingPromedio = (data["ratingPromedio"] as? Double) ?: 0.0
                        cantidadReviews = (data["cantidadReviews"] as? Long)?.toInt() ?: 0
                    }
                }

                onResult(lista)
            }
            .addOnFailureListener {
                onResult(emptyList())
            }
    }


}
