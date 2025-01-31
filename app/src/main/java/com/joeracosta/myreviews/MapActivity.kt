package com.joeracosta.myreviews

import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.CircularBounds
import com.google.android.libraries.places.api.model.RectangularBounds
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.SearchByTextRequest
import com.google.android.libraries.places.api.net.SearchByTextResponse
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.rememberCameraPositionState
import com.joeracosta.myreviews.data.Constants
import com.joeracosta.myreviews.data.MapData
import com.joeracosta.myreviews.data.Place
import com.joeracosta.myreviews.logic.LastLocationGetter
import com.joeracosta.myreviews.logic.LastLocationProviderActivityImpl
import com.joeracosta.myreviews.logic.MapViewModel
import com.joeracosta.myreviews.ui.theme.Amber
import com.joeracosta.myreviews.ui.theme.MyReviewsTheme
import com.joeracosta.myreviews.ui.view.MapMarker
import com.joeracosta.myreviews.ui.view.PlaceSheet
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

                    val layoutDirection = LocalLayoutDirection.current
                    Box(
                        Modifier
                            .fillMaxSize()
                            .padding(
                                start = innerPadding.calculateStartPadding(layoutDirection),
                                end = innerPadding.calculateEndPadding(layoutDirection),
                                bottom = innerPadding.calculateBottomPadding()
                            )
                    ) {


                        val positionToJumpTo = mapState.value.positionToJumpTo
                        val defaultMap = MapData(1.35, 103.87)
                        val defaultPosition = positionToJumpTo ?: defaultMap

                        val cameraPositionState = rememberCameraPositionState {
                            position = CameraPosition.fromLatLngZoom(
                                LatLng(
                                    defaultPosition.lat,
                                    defaultPosition.lng
                                ), Constants.DEFAULT_ZOOM_LEVEL
                            )
                        }

                        LaunchedEffect(positionToJumpTo) {

                            if (positionToJumpTo != null) {
                                //update position specifically for when location changes
                                cameraPositionState.animate(
                                    update = CameraUpdateFactory.newLatLngZoom(
                                        LatLng(
                                            positionToJumpTo.lat,
                                            positionToJumpTo.lng
                                        ),
                                        Constants.DEFAULT_ZOOM_LEVEL
                                    ),
                                    durationMs = Constants.POSITION_JUMP_SPEED
                                )
                                /*cameraPositionState.position = CameraPosition.fromLatLngZoom(
                                    LatLng(
                                        positionToJumpTo.lat,
                                        positionToJumpTo.lng
                                    ), 14f
                                )*/
                                mapViewModel.clearPositionToJumpTo()
                            }
                        }


                        GoogleMap(
                            cameraPositionState = cameraPositionState,
                            uiSettings = MapUiSettings(
                                zoomControlsEnabled = false,
                                myLocationButtonEnabled = false
                            ),
                            properties = MapProperties(
                                mapStyleOptions = MapStyleOptions.loadRawResourceStyle(
                                    baseContext,
                                    R.raw.empty_map_style
                                ),
                                isMyLocationEnabled = false
                            )
                        ) {
                            mapState.value.reviewedPlaces.forEach {
                                MapMarker(it) {
                                    mapViewModel.placeClicked(it)
                                }
                            }
                        }

                        //todo search box
                        Button(
                            onClick = { testPlaceSearch() },
                            modifier = Modifier
                                .align(Alignment.TopCenter)
                                .padding(16.dp)
                        ) {
                            Text("Test Place Search")
                        }


                        // Locate Me Button
                        FloatingActionButton(
                            onClick = {
                                handleLocation()
                            },
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(16.dp),
                            containerColor = Amber
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.gps_icon),
                                contentDescription = "Locate Me",
                                tint = Color.Black
                            )
                        }

                        val currentlyOpenedPlace = mapState.value.openedPlace
                        if (currentlyOpenedPlace != null) {
                            PlaceSheet(
                                place = currentlyOpenedPlace,
                                onJumpToGoogleMapsClicked = {
                                    //todo
                                },
                                onEditClicked = {
                                    //todo
                                }
                            ) {
                                mapViewModel.placeClosed()
                            }
                        }
                    }
                }
            }
        }
    }


    private fun testPlaceSearch() {
        val placesClient = Places.createClient(this)


        /*
        //autocomplete search
        val center = LatLng(40.9792684, -74.1158964) //todo map bounds
        val circle = CircularBounds.newInstance(center, 5000.0)
        val autoCompletePlacesRequest = FindAutocompletePredictionsRequest.builder()
            .setQuery("pizza")
            .setRegionCode("US")
            .setLocationRestriction(circle)
            .build()
        placesClient.findAutocompletePredictions(autoCompletePlacesRequest)
            .addOnSuccessListener { response ->
                println()
            }
            .addOnFailureListener {
                println()
            }

        */
        //todo text search seems to work better than autocomplete
        val placeFields = listOf(
            com.google.android.libraries.places.api.model.Place.Field.ID,
            com.google.android.libraries.places.api.model.Place.Field.DISPLAY_NAME,
            com.google.android.libraries.places.api.model.Place.Field.FORMATTED_ADDRESS)


        val center = LatLng(40.9792684, -74.1158964) //todo map bounds
        val circle = CircularBounds.newInstance(center, 5000.0)

        val  searchByTextRequest = SearchByTextRequest.builder("pizza", placeFields)
            .setMaxResultCount(10)
            .setLocationBias(circle).build();


        placesClient.searchByText(searchByTextRequest)
            .addOnSuccessListener { response: SearchByTextResponse ->
                val places = response.places
                println()
            }
            .addOnFailureListener {  e ->
                println()

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
                mapViewModel.updateCurrentLocation(
                    MapData(latestLocation.lat, latestLocation.lng),
                    true
                )
            }
        }
    }


}

