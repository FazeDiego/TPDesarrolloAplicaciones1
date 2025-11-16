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

    // ---------------------------------------------------
    // Obtener datos de un usuario por uid
    // ---------------------------------------------------
    fun obtenerUsuario(userId: String, onResult: (com.example.safewalk.model.Usuario?) -> Unit) {
        db.collection("usuarios").document(userId)
            .get()
            .addOnSuccessListener { doc ->
                if (doc.exists()) {
                    val usuario = doc.toObject(com.example.safewalk.model.Usuario::class.java)
                    onResult(usuario)
                } else {
                    onResult(null)
                }
            }
            .addOnFailureListener {
                onResult(null)
            }
    }

    // ---------------------------------------------------
    // Obtener reviews de un usuario
    // ---------------------------------------------------
    fun obtenerReviewsUsuario(userId: String, onResult: (List<com.example.safewalk.model.Review>) -> Unit) {
        db.collection("reviews")
            .whereEqualTo("usuarioId", userId)
            .get()
            .addOnSuccessListener { result ->
                val lista = result.documents.mapNotNull { doc ->
                    doc.toObject(com.example.safewalk.model.Review::class.java)?.apply {
                        // guardar el id del documento para operaciones futuras
                        id = doc.id
                    }
                }
                onResult(lista)
            }
            .addOnFailureListener {
                onResult(emptyList())
            }
    }

    // ---------------------------------------------------
    // Actualizar review (comentario + rating) y recalcular estadísticas del tramo
    // ---------------------------------------------------
    fun actualizarReview(reviewId: String, newComentario: String, newRating: Int, tramoId: String, onResult: (Boolean) -> Unit) {
        val ref = db.collection("reviews").document(reviewId)
        ref.update(mapOf("comentario" to newComentario, "rating" to newRating))
            .addOnSuccessListener {
                // recalcular estadísticas del tramo
                recalcularEstadisticasTramo(tramoId)
                onResult(true)
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Error actualizando review", e)
                onResult(false)
            }
    }

    // ---------------------------------------------------
    // Eliminar review y recalcular estadísticas del tramo
    // ---------------------------------------------------
    fun eliminarReview(reviewId: String, tramoId: String, onResult: (Boolean) -> Unit) {
        db.collection("reviews").document(reviewId)
            .delete()
            .addOnSuccessListener {
                recalcularEstadisticasTramo(tramoId)
                onResult(true)
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Error eliminando review", e)
                onResult(false)
            }
    }

    // ---------------------------------------------------
    // Recalcular promedio y cantidad de reviews para un tramo
    // ---------------------------------------------------
    fun recalcularEstadisticasTramo(tramoId: String) {
        db.collection("reviews")
            .whereEqualTo("tramoId", tramoId)
            .get()
            .addOnSuccessListener { snaps ->
                val ratings = snaps.documents.mapNotNull { it.getLong("rating")?.toInt() }
                val cantidad = ratings.size
                val promedio = if (cantidad > 0) ratings.average() else 0.0

                val tramoRef = db.collection("tramos").document(tramoId)
                tramoRef.update(mapOf("ratingPromedio" to promedio, "cantidadReviews" to cantidad))
                    .addOnSuccessListener { Log.d("Firestore", "Estadísticas recalculadas para tramo $tramoId") }
                    .addOnFailureListener { e -> Log.e("Firestore", "Error actualizando estadísticas", e) }
            }
            .addOnFailureListener { e -> Log.e("Firestore", "Error leyendo reviews para recalculo", e) }
    }

    // ---------------------------------------------------
    // Obtener un tramo por id
    // ---------------------------------------------------
    fun obtenerTramoPorId(tramoId: String, onResult: (com.example.safewalk.model.Tramo?) -> Unit) {
        db.collection("tramos").document(tramoId)
            .get()
            .addOnSuccessListener { doc ->
                if (doc.exists()) {
                    val tramo = doc.toObject(com.example.safewalk.model.Tramo::class.java)
                    // asegurarse de guardar el id real
                    tramo?.uuid = doc.id
                    onResult(tramo)
                } else onResult(null)
            }
            .addOnFailureListener {
                onResult(null)
            }
    }

    // ---------------------------------------------------
    // Actualizar campo nombre en documento usuario
    // ---------------------------------------------------
    fun actualizarNombreUsuario(userId: String, nuevoNombre: String, onResult: (Boolean) -> Unit) {
        db.collection("usuarios").document(userId)
            .update(mapOf("nombre" to nuevoNombre))
            .addOnSuccessListener { onResult(true) }
            .addOnFailureListener { onResult(false) }
    }

    /**
     * Guarda un timestamp indicando cuándo se cambió la contraseña por última vez.
     * Útil para auditoría o forzar re-logins si es necesario.
     */
    fun actualizarPasswordTimestamp(userId: String, onResult: (Boolean) -> Unit) {
        val ahora = com.google.firebase.Timestamp.now()
        db.collection("usuarios").document(userId)
            .update(mapOf("lastPasswordChange" to ahora))
            .addOnSuccessListener { onResult(true) }
            .addOnFailureListener { onResult(false) }
    }


}
