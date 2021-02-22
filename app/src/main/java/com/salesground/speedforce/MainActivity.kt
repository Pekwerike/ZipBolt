package com.salesground.speedforce

import android.Manifest.*
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.setContent
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.salesground.speedforce.ui.theme.SpeedForceTheme
import com.salesground.speedforce.viewmodel.MainActivityViewModel

private const val FINE_LOCATION_REQUEST_CODE = 100
class MainActivity : AppCompatActivity() {
    val mainActivityViewModel by viewModels<MainActivityViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SpeedForceTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {

                }
            }
        }
        observeViewModelLiveData()


    }

    fun observeViewModelLiveData(){
        // wifiP2p state changed, either enabled or disabled
        mainActivityViewModel.isWifiP2pEnabled.observe(this, Observer {
            it?.let {

            }
        })
    }

    fun wifiP2pState(isEnabled : Boolean){
        mainActivityViewModel.wifiP2pStateChange(newState = isEnabled)
    }

    // check if SpeedForce has access to device fine location
    private fun checkFineLocationPermission(){
        val isFineLocationPermissionGranted = ContextCompat.checkSelfPermission(
            this,
            permission.ACCESS_FINE_LOCATION
        ) ==
                PackageManager.PERMISSION_GRANTED

        if(isFineLocationPermissionGranted){
            // TODO check if the device location is on, using location manager
            //TODO more resource @ https://developer.android.com/training/location
        }else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(permission.ACCESS_FINE_LOCATION),
                FINE_LOCATION_REQUEST_CODE
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if(requestCode== FINE_LOCATION_REQUEST_CODE && permissions.contains(permission.ACCESS_FINE_LOCATION)){
            if(grantResults.isNotEmpty()){
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    // access to device fine location has been granted to SpeedForce
                    // TODO check if the device location is on, using location manager
                    //TODO more resource @ https://developer.android.com/training/location
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}


