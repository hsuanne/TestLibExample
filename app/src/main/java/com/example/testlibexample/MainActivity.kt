package com.example.testlibexample

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.btlibrary.BTHelper
import com.example.btlibrary.BTHelper.btActivityResultLauncher
import com.example.btlibrary.BTHelper.discoverDevicesARL
import com.example.btlibrary.Constants
import com.example.btlibrary.FoundDevice
import com.example.btlibrary.Toaster
import java.util.Timer
import kotlin.concurrent.schedule

class MainActivity : AppCompatActivity() {
    val btActivityResultLauncher = this.btActivityResultLauncher()
    private lateinit var fDevice: FoundDevice

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Toaster.showToast(this, "Successfully import btlibrary!")

        val bluetoothManager: BluetoothManager = getSystemService(BluetoothManager::class.java)
        val bluetoothAdapter: BluetoothAdapter? = bluetoothManager.adapter

        BTHelper.checkBluetoothSupported(this, bluetoothAdapter)
        BTHelper.checkBluetoothEnable(this, bluetoothAdapter)
        val pairedDevices = BTHelper.getPairedDevices(this, bluetoothAdapter, btActivityResultLauncher)
        println("test pairedDevices: $pairedDevices")

        val requestMultiplePermissions = this.discoverDevicesARL()
        val filter = IntentFilter().apply {
            addAction(BluetoothDevice.ACTION_FOUND)
        }
        registerReceiver(receiver, filter)
        BTHelper.discoverDevices(this, bluetoothAdapter, requestMultiplePermissions)

        BTHelper.enableDiscoverability(this, bluetoothAdapter, btActivityResultLauncher)

        BTHelper.AcceptThread(this, bluetoothAdapter) {
            println("test AcceptThread: $it")
        }.start()

        Timer().schedule(10000L) {
            BTHelper.ConnectThread(this@MainActivity, fDevice, bluetoothAdapter)
        }
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val device: BluetoothDevice? = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
            when(intent.action) {
                BluetoothDevice.ACTION_FOUND -> {
                    // Discovery has found a device. Get the BluetoothDevice
                    // object and its info from the Intent.
                    Log.d("MainActivity BroadcastReceiver onReceive ACTION_FOUND: ", "${device?.name}, ${device?.address}")

                    if (ActivityCompat.checkSelfPermission(
                            this@MainActivity,
                            BTHelper.getBTConnectPermission()
                        ) == PackageManager.PERMISSION_GRANTED) {
                        device?.let {
                            // if device is paired already, do not add to discoveredDevices
                            val deviceName = device.name ?: "Unknown"
                            val deviceHardwareAddress = device.address
                            val deviceSocket = device.createRfcommSocketToServiceRecord(Constants.MY_UUID)
                            val discoveredDevice = FoundDevice(deviceName, deviceHardwareAddress, deviceSocket)
                            if (discoveredDevice.deviceName == "Xiaomi Aaron") fDevice = discoveredDevice
                            println("test discoveredDevice: $discoveredDevice")
                        }
                    } else { // launch activityResultLauncher
                        BTHelper.launchPermissions(btActivityResultLauncher)
                    }
                }
            }
        }
    }

}