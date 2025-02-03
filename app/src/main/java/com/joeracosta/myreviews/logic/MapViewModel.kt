package com.joeracosta.myreviews.logic

import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng
import com.joeracosta.myreviews.data.MapData
import com.joeracosta.myreviews.data.MapState
import com.joeracosta.myreviews.data.MyPlace
import com.joeracosta.myreviews.data.Review
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow


class MapViewModel : ViewModel() {

    private val _state = MutableStateFlow(
        MapState(
            currentLocation = null,
            positionToJumpTo = null, //todo better defaults
            reviewedPlaces = emptyList(),
            openedPlace = null
        )
    )

    init {
        //todo testing
        val testPlace = MyPlace(
            id = "1",
            name = "Park West Tavern",
            review = Review(
                "This is review text for park west tavern. Their Guinness is not consistent",
                8.4F
            ),
            isFavorite = false,
            mapData = MapData(
                LatLng(
                    40.980407,
                    -74.118161
                ),
                "pwt address"
            )
        )

        val testPlace2 = MyPlace(
            id = "2",
            name = "Daily Treat",
            review = Review(
                "This is review text for Daily Treat aka Tasty Treat",
                9F
            ),
            isFavorite = true,
            mapData = MapData(
                LatLng(
                    40.9792684,
                    -74.1158964,
                ),
                "dt address"
            )
        )

        val listOfPlacesTest = listOf(testPlace, testPlace2)
        updateMapState(
            _state.value.copy(
                reviewedPlaces = listOfPlacesTest
            )
        )

    }

    val state: StateFlow<MapState> = _state

    fun updateCurrentLocation(currentLocation: LatLng, moveMap: Boolean) {
        updateMapState(
            _state.value.copy(
                currentLocation = currentLocation,
                positionToJumpTo = if (moveMap) currentLocation else _state.value.positionToJumpTo
            )
        )
    }

    fun placeClicked(place: MyPlace) {
        updateMapState(
            _state.value.copy(
                openedPlace = place
            )
        )
    }

    fun placeClosed() {
        updateMapState(
            _state.value.copy(
                openedPlace = null
            )
        )
    }

    fun clearPositionToJumpTo() {
        updateMapState(
            _state.value.copy(
                positionToJumpTo = null
            )
        )
    }


    private fun updateMapState(newState: MapState) {
        _state.value = newState
    }
}