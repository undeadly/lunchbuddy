package com.coryroy.lunchbuddy

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private val LOGTAG = "Maps"

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var viewModel: AppViewModel
    private lateinit var map: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        // Get location
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        viewModel = AppViewModel(
                cost =  intent.getIntExtra(COST, COST_DEFAULT),
                distance = intent.getIntExtra(DISTANCE, DISTANCE_DEFAULT))

        savedInstanceState?.let {bundle ->
            with (viewModel) {
                cost = bundle.getInt(COST)
                distance = bundle.getInt(DISTANCE)
            }
        }

        Log.d(LOGTAG, "distance:${viewModel.distance}, cost:${viewModel.cost}")

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 101)

        } else {
            subscribeToLocationUpdates()
        }

    }

    private fun subscribeToLocationUpdates() {
        fusedLocationClient.lastLocation.addOnSuccessListener {
            Log.d(LOGTAG, "got location: $it")
            updateMap(it)
        }
    }

    private fun updateMap(it: Location) {
        // Add a marker for where we are and move the camera
        val here = LatLng(it.latitude, it.longitude)
        viewModel.latlng = here
        map.addMarker(MarkerOptions().position(here).title("You are here!"))
        map.moveCamera(CameraUpdateFactory.newLatLng(here))
        map.addCircle(CircleOptions().center(viewModel.latlng).radius(viewModel.distance.toDouble()).strokeColor(R.color.colorPrimary).visible(true))
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (grantResults.isNotEmpty()) subscribeToLocationUpdates()
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        Log.d(LOGTAG, "onMapReady")
        map = googleMap
        map.isIndoorEnabled = true
        map.setMaxZoomPreference(20.0f)
        map.setMinZoomPreference(15.0f)
    }
}
