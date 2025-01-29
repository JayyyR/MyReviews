package com.joeracosta.myreviews.ui.view

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.MarkerComposable
import com.google.maps.android.compose.MarkerState
import com.joeracosta.myreviews.data.Place
import com.joeracosta.myreviews.ui.theme.Amber

@Composable
fun MapMarker(
    place: Place,
    onClick: () -> Unit
) {
    val markerState =
        remember { MarkerState(position = LatLng(place.mapData.lat, place.mapData.lng)) }
    val shape = RoundedCornerShape(10.dp, 10.dp, 10.dp, 0.dp)

    MarkerComposable(
        state = markerState,
        title = place.name,
        anchor = Offset(0.5f, 1f),
        onClick = {
            onClick()
            true
        }
    ) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .border(
                    width = 2.dp,
                    color = Color.White,
                    shape = shape
                )
                .clip(shape)
                .background(Amber)
                .padding(3.dp),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = if (place.isFavorite) Icons.Filled.Favorite else Icons.Filled.Place,
                contentDescription = "marker icon",
                tint = Color.White,
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}