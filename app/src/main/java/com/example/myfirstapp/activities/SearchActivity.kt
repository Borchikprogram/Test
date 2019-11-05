package com.example.myfirstapp.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager

import com.example.myfirstapp.R
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.util.Log
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class SearchActivity : BaseActivity(1) {
    private val TAG = "SearchActivity"
    lateinit var mapFragment : SupportMapFragment
    lateinit var googleMap: GoogleMap
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rasp)
        setupBottomNavigation()

        Log.d(TAG, "onCreate")

        mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(OnMapReadyCallback {
            googleMap = it
            googleMap.isMyLocationEnabled = true
            val location1 = LatLng(53.415460, 58.985342)
            googleMap.addMarker(MarkerOptions().position(location1).title("Дом пионеров"))
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location1,15f))

            val location2 = LatLng(53.440106, 58.995249)
            googleMap.addMarker(MarkerOptions().position(location2).title("Дворец спорта калибровщих"))

        })

        }
    }

