package com.example.wificonnect2

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.net.wifi.WifiConfiguration
import android.net.wifi.WifiManager
import android.net.wifi.WifiNetworkSpecifier
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    val TAG = MainActivity::class.java.simpleName

    var connectivityManager: ConnectivityManager? = null
    var callback: ConnectivityManager.NetworkCallback? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnConnect.setOnClickListener {
            connectToWifi(etSSID.text.toString())
        }

        btnDisconnect.setOnClickListener {
            disconnect()
        }
    }


    @RequiresApi(Build.VERSION_CODES.Q)
    fun connectToWifi(ssid: String) {

        val wifiManager = applicationContext.getSystemService(WIFI_SERVICE) as WifiManager?
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            try {


                val conf = WifiConfiguration()
                conf.SSID = "\"" + ssid + "\""
                conf.hiddenSSID = true // Put this line to hidden SSID

                conf.status = WifiConfiguration.Status.ENABLED
                conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP)
                conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP)
                conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE)
                conf.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP)
                conf.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP)
                conf.allowedProtocols.set(WifiConfiguration.Protocol.RSN)


                // Connect Network


                // Connect Network
                val wifiManager =
                    applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
                assert(wifiManager != null)

                val netId: Int = wifiManager.addNetwork(conf)

                val list = wifiManager.configuredNetworks
                for (i in list) {
                    if (i.SSID != null && i.SSID == "\"" + ssid + "\"") {
                        wifiManager.disconnect()
                        wifiManager.enableNetwork(i.networkId, true)
                        wifiManager.reconnect()
                        break
                    }
                }


            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else {

            val builder = WifiNetworkSpecifier.Builder().setSsid(ssid).setIsHiddenSsid(true)

            val wifiNetworkSpecifier = builder.build()

            val networkRequestBuilder1 = NetworkRequest.Builder()
            networkRequestBuilder1.addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            networkRequestBuilder1.setNetworkSpecifier(wifiNetworkSpecifier)

            val nr = networkRequestBuilder1.build()
            connectivityManager =
                this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

            callback = object : ConnectivityManager.NetworkCallback() {
                override fun onLost(network: Network) {
                    super.onLost(network)
                    Toast.makeText(this@MainActivity, "i am Disconnected", Toast.LENGTH_LONG).show()

                }

                override fun onLosing(network: Network, maxMsToLive: Int) {
                    super.onLosing(network, maxMsToLive)
                    Toast.makeText(this@MainActivity, "i am Loosing", Toast.LENGTH_LONG).show()

                }

                override fun onAvailable(network: Network) {
                    super.onAvailable(network)
                    Toast.makeText(this@MainActivity, "i am available", Toast.LENGTH_LONG).show()

                }
            }

            connectivityManager?.requestNetwork(nr, callback!!)

        }


    }

    private fun disconnect() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            val wm = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
            val netId: Int = wm.connectionInfo.networkId
            wm.disableNetwork(netId)
            wm.disconnect()

            Toast.makeText(this, "Wifi disconnected : ${wm.disconnect()}", Toast.LENGTH_LONG).show()

        } else {
            connectivityManager?.unregisterNetworkCallback(callback!!)
        }
    }

}