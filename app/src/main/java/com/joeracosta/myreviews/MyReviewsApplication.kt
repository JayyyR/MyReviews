package com.joeracosta.myreviews

import android.app.Application
import com.google.android.libraries.places.api.Places

class MyReviewsApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        val apiKey = resources.getString(R.string.maps_api_key)
        Places.initializeWithNewPlacesApiEnabled(this, apiKey)
    }
}