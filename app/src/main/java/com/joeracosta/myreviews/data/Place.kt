package com.joeracosta.myreviews.data

data class Place (
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
    val lat: Double,
    val lng: Double,
    //val address: String //todo
)

data class MapState(
    val currentLocation: MapData?,
    val positionToJumpTo: MapData?,
    val reviewedPlaces: List<Place>,
    val openedPlace: Place?
)

