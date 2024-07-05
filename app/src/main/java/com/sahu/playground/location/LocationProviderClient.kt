package com.sahu.playground.location

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Looper
import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.google.android.gms.location.CurrentLocationRequest
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationToken
import com.google.android.gms.tasks.OnTokenCanceledListener
import java.util.Collections
import java.util.LinkedList
import java.util.PriorityQueue
import javax.inject.Inject
import javax.inject.Singleton

//@Singleton
class LocationProviderClient(val context: Context, val fusedLocationClient: FusedLocationProviderClient, val onLocationReceived: (location: Location) -> Unit) {

//    @Inject
//    internal lateinit var context: Context
//    @Inject
//    internal lateinit var fusedLocationClient: FusedLocationProviderClient
    private var locationCallback: LocationCallback? = null
    private var locationRequest: LocationRequest? = null



    fun requestLocationUpdates(): Boolean {
        if(ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            return false
        locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 10000)
            .setWaitForAccurateLocation(true)
            .setMinUpdateIntervalMillis(5000)
            .setMaxUpdateDelayMillis(20000)
            .build()

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                Log.i("LocationProviderClient", "Location result with ${locationResult.locations.size} locations" )
//                val stack = PriorityQueue<Int>(10, compareByDescending(Int))
                val maxHeap = PriorityQueue<Int>(Collections.reverseOrder())
//                stack.contains()
//                mapOf<String, String>().containsKey()
                for (location in locationResult.locations) {
                    // Update UI with location data
                    onLocationReceived(location)
                    Log.i("LocationProviderClient", "Location updated: $location" )
                }
            }
        }

//        val currentLocationRequest = CurrentLocationRequest.Builder()

//        fusedLocationClient.getCurrentLocation(locationRequest, object : CancellationToken(){
//            override fun onCanceledRequested(p0: OnTokenCanceledListener): CancellationToken {
//
//            }
//
//            override fun isCancellationRequested(): Boolean {
//                return false
//            }
//
//        })
        fusedLocationClient.requestLocationUpdates(locationRequest!!, locationCallback!!, Looper.getMainLooper())
//        fusedLocationClient.apiKey
        return true
    }

    fun stopLocationUpdates() {
        locationCallback?.let {
            fusedLocationClient.removeLocationUpdates(it)
            Log.i("LocationProviderClient", "Location updated: stopped" )
        }
        locationCallback = null
        locationRequest = null
    }
}