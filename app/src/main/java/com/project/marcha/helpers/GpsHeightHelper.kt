package com.project.marcha.helpers

import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import android.Manifest
import android.os.Looper

class GpsHeightHelper(
    private val context: Context,
    private val callback: Callback){

    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
    private var locationCallback: LocationCallback? = null
    private var requesting = false

    fun start() {
        if (requesting) return
        requesting = true

        val locationRequest = LocationRequest.create().apply {
            interval = 5000L
            fastestInterval = 2000L
            priority = Priority.PRIORITY_HIGH_ACCURACY
        }

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                val location = result.lastLocation
                location?.let {
                    val altitude = it.altitude.toFloat() // em metros
                    callback.onHeightUpdate(altitude)
                }
            }
        }

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback!!, Looper.getMainLooper())
        }
    }

    fun stop() {
        if (requesting) {
            locationCallback?.let { fusedLocationClient.removeLocationUpdates(it) }
            requesting = false
        }
    }

    interface Callback {
        fun onHeightUpdate(altitude: Float)
    }

}


