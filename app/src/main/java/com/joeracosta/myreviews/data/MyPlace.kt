package com.joeracosta.myreviews.data

import com.google.android.gms.maps.model.LatLng

data class MyPlace (
    val id: String,
    val name: String,
    val review: Review?,
    val isFavorite: Boolean,
    val mapData: MapData
)

data class Review (
    val text: String,
    val score: Float
)

data class MapData (
    val latLng: LatLng,
    val address: String
)

data class MapState(
    val currentLocation: LatLng?,
    val positionToJumpTo: LatLng?,
    val reviewedPlaces: List<MyPlace>,
    val openedPlace: MyPlace?,
    val searchQuery: String?,
    val currentMapCenter: LatLng,
    val placeSearchResults: List<MyPlace>?
)

sealed class MyResult<out T> {
    data class Success<out T>(val value: T): MyResult<T>()
    data class Error(val errorMessage: String): MyResult<Nothing>()
}