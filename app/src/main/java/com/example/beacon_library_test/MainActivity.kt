package com.example.beacon_library_test

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import org.altbeacon.beacon.BeaconManager
import org.altbeacon.beacon.MonitorNotifier
import org.altbeacon.beacon.Region
import java.security.Permissions

class MainActivity : AppCompatActivity(){

    val monitoringObserver = Observer<Int> { state ->
        if (state == MonitorNotifier.INSIDE) {
            Log.d(BEACON_TAG, "Detected beacons(s)")
        }
        else {
            Log.d(BEACON_TAG, "Stopped detecting beacons")
        }
    }

    var PERMISSIONS = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_BACKGROUND_LOCATION
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_LOCATION_PERMISSIONS -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() &&
                            grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    doBeaconWorkWithAskingPermissions()

                    // Permission is granted. Continue the action or workflow
                    // in your app.
                } else {
                    Toast.makeText(applicationContext, "No permissions no beacon, sorry", Toast.LENGTH_SHORT).show()
                    // Explain to the user that the feature is unavailable because
                    // the features requires a permission that the user has denied.
                    // At the same time, respect the user's decision. Don't link to
                    // system settings in an effort to convince the user to change
                    // their decision.
                }
                return
            }

            // Add other 'when' lines to check for other
            // permissions this app might request.
            else -> {
                // Ignore all other requests.
            }
        }
    }

    fun doBeaconWorkWithAskingPermissions(){
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            val beaconManager =  BeaconManager.getInstanceForApplication(this)
            val region = Region("all-beacons-region", null, null, null)
            // Set up a Live Data observer so this Activity can get monitoring callbacks
            // observer will be called each time the monitored regionState changes (inside vs. outside region)

            //beaconManager.startMonitoringBeaconsInRegion(region)
            beaconManager.getRegionViewModel(region).regionState.observe(this, monitoringObserver)
            beaconManager.startMonitoring(region)
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                requestPermissions(PERMISSIONS, REQUEST_LOCATION_PERMISSIONS)
            }
        }

    }

    companion object {
        const val REQUEST_LOCATION_PERMISSIONS = 1
        const val REQUEST_ENABLE_BT = 0
        const val BEACON_TAG = "BEACON_FINDS"
    }
}

