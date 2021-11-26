package com.example.mymap

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.view.ContentInfoCompat
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    companion object {
        private const val LOCATION_REQUEST_CODE = 1
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        checkPermission()
        main_btnReload.setOnClickListener {
            checkPermission()
        }
    }

    private fun checkConnect(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val info = connectivityManager.activeNetworkInfo
        return if (info == null || !info.isConnected || !info.isAvailable) {
            Toast.makeText(baseContext, "No internet connection", Toast.LENGTH_SHORT).show()
            false
        } else {
            true
        }
        return false
    }

    private fun checkPermission(){
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
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            LOCATION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED && checkConnect()) {
                    var intent = Intent(this,MapsActivity::class.java)
                    startActivity(intent)
                    finishAffinity()
                }else{
                    main_btnReload.visibility = View.VISIBLE
                    main_tvNoti.visibility = View.VISIBLE
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}