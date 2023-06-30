package com.example.ar_custom_widget.utils

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

object GeoPermissionHelper {
    private const val CAMERA_PERMISSION = Manifest.permission.CAMERA
    private const val LOCATION_PERMISSION = Manifest.permission.ACCESS_FINE_LOCATION
    private const val PERMISSION_REQUEST_CODE = 1001
    private lateinit var activityResultLauncher: ActivityResultLauncher<Array<String>>

    private lateinit var fragment: Fragment
    private lateinit var onPermissionsGranted: () -> Unit
    private lateinit var onPermissionsDenied: () -> Unit

    fun requestPermissions (
        fragment: Fragment,
        onPermissionGranted: ()-> Unit,
        onPermissionDenied: ()-> Unit
    ) {
        this.fragment = fragment

        activityResultLauncher = fragment.registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()) { result ->
            var allAreGranted = true

            for(b in result.values) {
                allAreGranted = allAreGranted && b
            }

            if(allAreGranted) {
                onPermissionGranted()
            }
        }
        if(permissionsGranted()) {
            onPermissionGranted()
        }
    }

    private fun isCameraPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            fragment.requireContext(),
            CAMERA_PERMISSION
        ) == PackageManager.PERMISSION_GRANTED
    }
    private fun isLocationPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            fragment.requireContext(),
            LOCATION_PERMISSION
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun permissionsGranted(): Boolean {
        if(!isCameraPermissionGranted() && !isLocationPermissionGranted()) {
            activityResultLauncher.launch(arrayOf(
                CAMERA_PERMISSION, LOCATION_PERMISSION
            ))
            return false;
        }
        return true
    }

    fun handlePermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            val permissionsMap = permissions.zip(grantResults.toList()).toMap()

            val cameraPermissionGranted = permissionsMap[CAMERA_PERMISSION] == PackageManager.PERMISSION_GRANTED
            val locationPermissionGranted = permissionsMap[LOCATION_PERMISSION] == PackageManager.PERMISSION_GRANTED

            if (cameraPermissionGranted && locationPermissionGranted) {
                onPermissionsGranted()
            } else {
                onPermissionsDenied()
            }
        }
    }

}