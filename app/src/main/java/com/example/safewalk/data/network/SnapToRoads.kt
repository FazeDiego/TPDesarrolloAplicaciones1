package com.example.safewalk.data.network

import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

suspend fun snapToRoads(
    pointA: LatLng,
    pointB: LatLng,
    apiKey: String   // 👉 ahora es la key de OpenRouteService
): List<LatLng> = withContext(Dispatchers.IO) {

    // OpenRouteService usa formato: start=lng,lat & end=lng,lat
    val start = "${pointA.longitude},${pointA.latitude}"
    val end = "${pointB.longitude},${pointB.latitude}"

    val urlStr =
        "https://api.openrouteservice.org/v2/directions/foot-walking?" +
                "start=$start&end=$end&api_key=$apiKey"

    try {
        val url = URL(urlStr)
        val conn = url.openConnection() as HttpURLConnection
        conn.requestMethod = "GET"

        val responseCode = conn.responseCode
        if (responseCode != HttpURLConnection.HTTP_OK) {
            // Si falla, devolvemos la recta original
            return@withContext listOf(pointA, pointB)
        }

        val response = conn.inputStream.bufferedReader().use { it.readText() }
        val json = JSONObject(response)

        // Estructura de ORS:
        // {
        //   "features": [
        //     {
        //       "geometry": {
        //         "coordinates": [[lng, lat], [lng, lat], ...]
        //       }
        //     }
        //   ]
        // }
        val features = json.getJSONArray("features")
        if (features.length() == 0) {
            return@withContext listOf(pointA, pointB)
        }

        val geometry = features
            .getJSONObject(0)
            .getJSONObject("geometry")

        val coords = geometry.getJSONArray("coordinates")

        val result = mutableListOf<LatLng>()
        for (i in 0 until coords.length()) {
            val pair = coords.getJSONArray(i)
            val lng = pair.getDouble(0)
            val lat = pair.getDouble(1)
            result.add(LatLng(lat, lng))
        }

        result
    } catch (e: Exception) {
        e.printStackTrace()
        // En caso de error, devolvemos recta A-B
        listOf(pointA, pointB)
    }
}
