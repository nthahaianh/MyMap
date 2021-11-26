package com.example.mymap

import android.Manifest
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.activity_maps.*
import java.util.*

class MapsActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMapClickListener,GoogleMap.OnMapLongClickListener {

    companion object {
        private const val LOCATION_REQUEST_CODE = 1
    }

    val hanoi = LatLng(21.029634028797496, 105.85289362818004)
    private lateinit var mMap: GoogleMap
    private var currentLocation: Location? = null
    private var destinationLocation: Location? = null
    private var fusedLocationClient: FusedLocationProviderClient? = null
    var currentMarker: Marker? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
//        setUpMap()
        map_ivNavigation.setOnClickListener {
            if(map_tvAddress==null || map_tvAddress.text.toString() == ""){
                Toast.makeText(baseContext,"Select a place",Toast.LENGTH_SHORT).show()
            }else{
                map_llSearch.visibility = View.GONE
                map_llAddress.visibility = View.VISIBLE
            }
        }
        map_btnSubmit.setOnClickListener {
            Toast.makeText(baseContext, "Navigation", Toast.LENGTH_SHORT).show()
        }
        map_ivSearch.setOnClickListener {
            map_llAddress.visibility = View.GONE
            map_llSearch.visibility = View.VISIBLE
        }
        map_btnSearch.setOnClickListener {
            searchLocation(map_etSearch.text.toString())
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.setOnMapLongClickListener(this)
        mMap.setOnMapClickListener(this)
        if(Build.VERSION.SDK_INT>Build.VERSION_CODES.M){
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                    LOCATION_REQUEST_CODE
                )
                return
            }
        }
        mMap.isMyLocationEnabled = true
        mMap.uiSettings.isMapToolbarEnabled = false
//        mMap.addMarker(MarkerOptions().position(hanoi).title("Marker in Hanoi"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(hanoi))
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(hanoi,12f))
//        val latLng = LatLng(currentLocation?.latitude!!,currentLocation?.longitude!!)
//        addMarkerOnMap(latLng)

        mMap.setOnMarkerDragListener(object : GoogleMap.OnMarkerDragListener {
            override fun onMarkerDrag(p0: Marker) {
            }

            override fun onMarkerDragEnd(p0: Marker) {
                if (currentMarker != null) {
                    currentMarker?.remove()
                }
                val newPosition = LatLng(p0.position.latitude, p0.position.longitude)
                addMarkerOnMap(newPosition)
            }

            override fun onMarkerDragStart(p0: Marker) {
            }

        })
    }

    private fun addMarkerOnMap(currentLatLong: LatLng) {
        var address = getAdd(currentLatLong.latitude, currentLatLong.longitude)
        val markerOptions = MarkerOptions()
            .position(currentLatLong)
            .draggable(true)
            .title("$currentLatLong")
            .snippet(address)
            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
            .alpha(0.7f)
        map_tvAddress.setText(address)
        mMap.moveCamera(CameraUpdateFactory.newLatLng(currentLatLong))
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLong, 15f))
        currentMarker = mMap.addMarker(markerOptions)
//        currentMarker?.showInfoWindow()
    }

    private fun getAdd(latitude: Double, longitude: Double): String? {
        val geoCoder = Geocoder(this, Locale.getDefault())
        try {
            val address = geoCoder.getFromLocation(latitude, longitude, 1)
            return address[0].getAddressLine(0)
        } catch (e: Exception) {
            e.stackTrace
        }
        return "---"
    }

    private fun searchLocation(searchString: String) {
        var addressList:List<Address>?=null
        if (searchString==""){
            Toast.makeText(baseContext, "Empty", Toast.LENGTH_SHORT).show()
        }else{
            val geoCoder = Geocoder(this)
            try {
                addressList = geoCoder.getFromLocationName(searchString,1)
            } catch (e: Exception) {
                e.stackTrace
            }
            if (addressList?.size!! >0){
                val address = addressList?.get(0)
                val searchPosition = address?.let { LatLng(it.latitude,address.longitude) }
                if (searchPosition != null) {
                    if (currentMarker != null) {
                        currentMarker?.remove()
                    }
                    addMarkerOnMap(searchPosition)
                    map_llSearch.visibility = View.GONE
                    map_llAddress.visibility = View.VISIBLE
                }
            }else{
                Toast.makeText(baseContext, "No result", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onMapLongClick(point: LatLng) {
        if (currentMarker != null) {
            currentMarker?.remove()
        }
        val newPosition = LatLng(point.latitude, point.longitude)
        addMarkerOnMap(newPosition)
    }

    override fun onMapClick(p0: LatLng) {
        map_llSearch.visibility = View.GONE
        map_llAddress.visibility = View.GONE
    }
}