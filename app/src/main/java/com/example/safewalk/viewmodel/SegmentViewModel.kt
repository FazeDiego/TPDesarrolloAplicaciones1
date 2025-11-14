package com.example.safewalk.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng

class SegmentViewModel : ViewModel() {
    var puntoA by mutableStateOf<LatLng?>(null)
    var puntoB by mutableStateOf<LatLng?>(null)
}

