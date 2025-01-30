package com.joeracosta.myreviews.ui.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.joeracosta.myreviews.R
import com.joeracosta.myreviews.data.MapData
import com.joeracosta.myreviews.data.Place
import com.joeracosta.myreviews.data.Review
import com.joeracosta.myreviews.ui.theme.Amber
import com.joeracosta.myreviews.ui.theme.ForestGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaceSheet(
    place: Place,
    onEditClicked: () -> Unit,
    onDismissed: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismissed
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Text(
                    modifier = Modifier.weight(1f),
                    text = place.name,
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Start
                )

                Button(
                    onClick = onEditClicked,
                    colors = ButtonDefaults.buttonColors(
                        contentColor = ForestGreen,
                        containerColor = Color.Transparent
                    ),
                    elevation = ButtonDefaults.buttonElevation(0.dp) // Removes shadow
                ) {
                    val editText = stringResource(R.string.edit_button)
                    Text(text = editText)
                }
            }
        }
    }
}

@Preview
@Composable
fun SheetPreview() {
    val testPlace = Place(
        id = "1",
        name = "Park West Tavern",
        review = Review(
            "This is review text",
            8.4F
        ),
        isFavorite = false,
        mapData = MapData(
            40.980407,
            -74.118161
        )
    )
    Box(
        Modifier
            .fillMaxSize()
            .background(Amber)
    ) {

        PlaceSheet(testPlace, {}) { }
    }
}