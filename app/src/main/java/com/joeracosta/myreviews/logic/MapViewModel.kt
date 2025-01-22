package com.joeracosta.myreviews.logic

import androidx.lifecycle.ViewModel
import com.joeracosta.myreviews.data.MapData
import com.joeracosta.myreviews.data.MapState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class MapViewModel : ViewModel() {

    private val _state = MutableStateFlow(
        MapState(
            currentLocation = null,
            positionToJumpTo = null, //todo better defaults
            reviewedPlaces = emptyList()
        )
    )

    val state: StateFlow<MapState> = _state

    fun updateCurrentLocation(currentLocation: MapData, moveMap: Boolean) {
        updateMapState(
            _state.value.copy(
                currentLocation = currentLocation,
                positionToJumpTo = if (moveMap) currentLocation else _state.value.positionToJumpTo
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