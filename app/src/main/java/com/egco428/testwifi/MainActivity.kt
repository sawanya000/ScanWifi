package com.egco428.testwifi

import android.Manifest
import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.net.wifi.WifiManager

import android.net.wifi.ScanResult
import android.widget.Button
import android.widget.ListView


import kotlinx.android.synthetic.main.activity_main.*
import android.widget.Toast
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.app.ComponentActivity
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import androidx.core.content.ContextCompat


class MainActivity : AppCompatActivity() {
    private var wifiManager: WifiManager? = null
    private val PERMISSION_ID = 42
    private var results: List<ScanResult>? = null
    private val arrayList = arrayListOf<String>()
    private var adapter: ArrayAdapter<*>? = null

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setWifiManager()
        checkWifi()
        requestPermission()
        scanBtn.setOnClickListener { scanWifi() }
    }

    private fun checkWifi() {
        setListView()
        if (!wifiManager!!.isWifiEnabled) {
            Toast.makeText(this, "WiFi is disabled ... We need to enable it", Toast.LENGTH_LONG)
                .show()
            wifiManager!!.isWifiEnabled = true
        }
    }

    private fun setWifiManager() {
        wifiManager = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
    }

    private fun setListView() {
        adapter = ArrayAdapter(this, R.layout.simple_list_item, arrayList)
        wifiList.adapter = adapter
    }

    private fun requestPermission(): Boolean {
        return checkPermission(
            Manifest.permission.CHANGE_WIFI_STATE,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.CHANGE_WIFI_MULTICAST_STATE
        )
    }

    private fun checkPermission(vararg perm: String): Boolean {
        val havePermissions = perm.toList().all {
            ContextCompat.checkSelfPermission(
                this,
                it
            ) == PackageManager.PERMISSION_GRANTED
        }
        if (!havePermissions) {
            ActivityCompat.requestPermissions(this, perm, PERMISSION_ID)
            return false
        }
        return true
    }

    private fun scanWifi() {
        arrayList.clear()
        wifiManager!!.startScan()
        registerReceiver(wifiReceiver, IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION))
        Toast.makeText(this, "Scanning WiFi ...", Toast.LENGTH_SHORT).show()
    }

    var wifiReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            results = wifiManager!!.scanResults
            unregisterReceiver(this)

            for (scanResult in results!!) {
                var wifi_ssid = ""
                wifi_ssid = scanResult.SSID
                Log.d("WIFIScannerActivity", "WIFI SSID: $wifi_ssid")

                var wifi_ssid_first_nine_characters = ""

                if (wifi_ssid.length > 8) {
                    wifi_ssid_first_nine_characters = wifi_ssid.substring(0, 9)
                } else {
                    wifi_ssid_first_nine_characters = wifi_ssid
                }
                Log.d("WIFIScannerActivity", "WIFI SSID 9: $wifi_ssid_first_nine_characters")

                // Display only WIFI that matched "WIFI_NAME"
//                if (wifi_ssid_first_nine_characters == "WIFI_NAME") {
                Log.d(
                    "WIFIScannerActivity",
                    "scanResult.SSID: " + scanResult.SSID + ", scanResult.capabilities: " + scanResult.capabilities
                )
                arrayList.add(scanResult.SSID + " - " + scanResult.capabilities)
                adapter!!.notifyDataSetChanged()
//                }

                wifi_ssid = ""
                wifi_ssid_first_nine_characters = ""
            }
        }
    }

}
