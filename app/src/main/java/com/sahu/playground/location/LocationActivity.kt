package com.sahu.playground.location

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.location.Location
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.location.LocationServices
import com.sahu.playground.appUtil.BaseActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LocationActivity : BaseActivity() {

//    @Inject
    private lateinit var locationProviderClient: LocationProviderClient

    companion object {
        const val DEEPLINK_PATH = "locationActivity"
    }

    private val requestPermissions =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { results: Boolean ->
            // Handle permission requests results
            // See the permission example in the Android platform samples: https://github.com/android/platform-samples
            if (results) {
                locationProviderClient.requestLocationUpdates()
            } else {
                locationStatus.value = LocationStatus.PermissionDenied
            }
        }


    private fun requestPermission() {
        // Permission request logic
        requestPermissions.launch(ACCESS_FINE_LOCATION)
    }

    sealed interface LocationStatus {

        data object LOADING : LocationStatus
        data object PermissionDenied : LocationStatus
        data class SUCCESS(val location: Location) : LocationStatus
    }

    private val locationStatus = MutableLiveData<LocationStatus>(LocationStatus.LOADING)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        locationProviderClient = LocationProviderClient(this, LocationServices.getFusedLocationProviderClient(this)) {
            if(locationStatus.value is LocationStatus.SUCCESS){
                if((locationStatus.value as LocationStatus.SUCCESS).location.accuracy > it.accuracy)
                    locationStatus.value = LocationStatus.SUCCESS(it)
            }else {
                locationStatus.value = LocationStatus.SUCCESS(it)
            }
        }
        refreshLocation()
    }

    private fun refreshLocation() {
        if (!locationProviderClient.requestLocationUpdates()) {
            requestPermission()
        }
    }

    @Composable
    override fun ComposableView() {

        Surface(
            Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .systemBarsPadding()
        ) {
            Column(Modifier.fillMaxSize()) {
                val status: LocationStatus =
                    locationStatus.observeAsState().value ?: LocationStatus.LOADING
                Box( Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) {
                    when (status) {
                        is LocationStatus.LOADING -> Text(text = "Loading location / requesting permission")
                        is LocationStatus.PermissionDenied -> Text(text = "Location permission not present")
                        is LocationStatus.SUCCESS -> UpdateUI(location = status.location)
                    }
                }
                Button(
                    onClick = { refreshLocation() },
                    modifier = Modifier
                        .padding(48.dp)
                        .align(Alignment.CenterHorizontally)
                ) {
                    Text(text = "Refresh Location")
                }
            }
        }
    }

    @Composable
    private fun UpdateUI(location: Location) {
        // Update your UI with the location data
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Latitude: ${location.latitude}",
                style = MaterialTheme.typography.headlineLarge
            )
            Text(
                text = "Longitude: ${location.longitude}",
                style = MaterialTheme.typography.headlineLarge
            )
            Text(
                text = "Accuracy: ${location.accuracy} meters",
                style = MaterialTheme.typography.headlineLarge
            )
        }
    }

    override fun onStop() {
        super.onStop()
        locationProviderClient.stopLocationUpdates()
    }
}