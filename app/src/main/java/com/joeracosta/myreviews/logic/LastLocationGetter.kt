package com.joeracosta.myreviews.logic;

import com.google.android.gms.location.FusedLocationProviderClient
import com.joeracosta.myreviews.data.MapData;
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

interface LastLocationGetter {
    suspend fun getLastLocation(): MapData?
}

class LastLocationProviderActivityImpl(
    private val fusedLocationProviderClient: FusedLocationProviderClient
) : LastLocationGetter {
    override suspend fun getLastLocation(): MapData? = suspendCoroutine { cont ->
        try {
            fusedLocationProviderClient.lastLocation
                .addOnSuccessListener { location ->
                    if (location != null) {
                        val latitude = location.latitude
                        val longitude = location.longitude
                        cont.resume(MapData(latitude, longitude))
                    } else {
                        cont.resume(null)
                    }
                }
                .addOnFailureListener { e ->
                    cont.resume(null)
                }
        } catch (e: SecurityException) {
            cont.resume(null)
        }
    }

}
