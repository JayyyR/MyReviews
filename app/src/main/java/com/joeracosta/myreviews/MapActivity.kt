package com.joeracosta.myreviews

import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.rememberCameraPositionState
import com.joeracosta.myreviews.data.MapData
import com.joeracosta.myreviews.data.Place
import com.joeracosta.myreviews.data.Review
import com.joeracosta.myreviews.logic.LastLocationGetter
import com.joeracosta.myreviews.logic.LastLocationProviderActivityImpl
import com.joeracosta.myreviews.logic.MapViewModel
import com.joeracosta.myreviews.ui.theme.MyReviewsTheme
import com.joeracosta.myreviews.ui.view.MapMarker
import kotlinx.coroutines.launch

class MapActivity : ComponentActivity() {

    private var lastLocationGetter: LastLocationGetter? = null

    private val foregroundLocationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions.any { it.value }

        if (granted) {
            updateCurrentLocation()
        } else {
            //todo error saying need permissions
        }
    }

    private lateinit var mapViewModel: MapViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mapViewModel = ViewModelProvider(this)[MapViewModel::class.java]

        handleLocation()

        enableEdgeToEdge()
        setContent {
            val mapState = mapViewModel.state.collectAsState()
            MyReviewsTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->


                    val positionToJumpTo = mapState.value.positionToJumpTo
                    val defaultMap = MapData(1.35, 103.87)
                    val defaultPosition = positionToJumpTo ?: defaultMap

                    val cameraPositionState = rememberCameraPositionState {
                        position = CameraPosition.fromLatLngZoom(
                            LatLng(
                                defaultPosition.lat,
                                defaultPosition.lng
                            ), 14f
                        )
                    }

                    LaunchedEffect(positionToJumpTo) {

                        if (positionToJumpTo != null) {
                            //update position specifically for when location changes
                            cameraPositionState.position = CameraPosition.fromLatLngZoom(
                                LatLng(
                                    positionToJumpTo.lat,
                                    positionToJumpTo.lng
                                ), 14f
                            )
                            mapViewModel.clearPositionToJumpTo()
                        }
                    }

                    val testPlace = Place(
                        id = "1",
                        name = "Park West Tavern",
                        review = Review (
                            "This is review text",
                            8.4F
                        ),
                        isFavorite = false,
                        mapData = MapData (
                            40.980407,
                            -74.118161
                        )

                    )

                    GoogleMap(
                        modifier = Modifier.padding(innerPadding),
                        cameraPositionState = cameraPositionState
                    ) {
                        MapMarker(testPlace) {
                            //on click
                        }
                    }
                }
            }
        }
    }


    private fun handleLocation() {

        val permissionFine = android.Manifest.permission.ACCESS_FINE_LOCATION
        val permissionCoarse = android.Manifest.permission.ACCESS_COARSE_LOCATION

        val fineGranted = ContextCompat.checkSelfPermission(
            this,
            permissionFine
        ) == PackageManager.PERMISSION_GRANTED

        val coarseGranted = ContextCompat.checkSelfPermission(
            this,
            permissionCoarse
        ) == PackageManager.PERMISSION_GRANTED

        // Permission has been granted
        if (fineGranted || coarseGranted) {
            updateCurrentLocation()
            return
        }

        //rationale dialog?

        foregroundLocationPermissionLauncher.launch(
            arrayOf(
                android.Manifest.permission.ACCESS_COARSE_LOCATION,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            )
        )
    }


    private fun updateCurrentLocation() {
        val fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        lastLocationGetter = LastLocationProviderActivityImpl(fusedLocationProviderClient)

        lifecycleScope.launch {
            val latestLocation = lastLocationGetter?.getLastLocation()
            if (latestLocation != null) {
                mapViewModel.updateCurrentLocation(MapData(latestLocation.lat, latestLocation.lng), true)
            }
        }
    }


}

