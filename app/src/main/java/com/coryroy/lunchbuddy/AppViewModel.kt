package com.coryroy.lunchbuddy

import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng

const val COST = "cost"
const val DISTANCE = "distance"

const val COST_DEFAULT = 15
const val DISTANCE_DEFAULT = 200

class AppViewModel(
        var distance: Int = DISTANCE_DEFAULT,
        var cost: Int = COST_DEFAULT,
        var latlng: LatLng = LatLng(45.2,-119.1)
) : ViewModel()