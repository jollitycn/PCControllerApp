package com.jollitycn.mobilekeybroad.bluetooth

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.bluetooth.*
import android.content.AttributionSource
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.core.app.NotificationCompat
import com.example.bluetoothconnect.DescriptorCollection

class BluetoothConnectService: Service() {

    // Binder given to clients.
    private val binder = LocalBinder()

    lateinit var bthid: BluetoothHidDevice
    lateinit var bluetoothAdapter: BluetoothAdapter
//    lateinit var bthid : BluetoothHidDevice
//    lateinit var bluetoothAdapter: BluetoothAdapter
    /** Method for clients.  */

    // use BluetoothProfile.ServiceListener.onServiceConneted to get BluetoothHidDevice proxy object, no matter if bluetooth is connected or not
    //BluetoothHidDevice.Callback can listen bluetooth connection state change, pass it to BluetoothHidDevice.registerApp
    // after registerApp, once bluetooth connection is changed, callback will be called,
    // get BluetoothHidDevice from BluetoothProfile.ServiceListener, get BluetoothDevice from registerApp
    // send message need BluetoothHidDevice and BluetoothDevice with sendReport
    // registerApp contain sdp_record, there's keyboard report id in sdp_record's last parameter descriptor

//    fun connect(TARGET_DEVICE_NAME: String): Pair<BluetoothHidDevice, BluetoothAdapter> {
//    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun connect(device : BluetoothDevice)  {
        println("*** connect in BluetoothConnectService")
//        val bluetoothAdapter =  BluetoothManager().adapter;
//        val bluetoothManager: BluetoothManager = applicationContext.getSystemService(BluetoothManager::class.java)
//        val bluetoothAdapter = bluetoothManager.adapter
         bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
//        if (bluetoothAdapter?.isEnabled == false) {
//            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
//            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)
//        }

        bluetoothAdapter.getProfileProxy(this,
                object : BluetoothProfile.ServiceListener {
                    override fun onServiceDisconnected(profile: Int) {
                        println("--- Disconnect, profile is $profile")
                    }

                    override fun onServiceConnected(profile: Int, proxy: BluetoothProfile?) {
                        println("--- connected, profile is $profile")
                        if (profile != BluetoothProfile.HID_DEVICE) {
                            return
                        }
                        // get bthid
                        bthid = proxy as BluetoothHidDevice
                        println("--- got hid proxy object ")
                        val btcallback = BluetoothCallback(this@BluetoothConnectService, bthid, bluetoothAdapter, device.name)
                        if (ActivityCompat.checkSelfPermission(
                                applicationContext,
                                Manifest.permission.BLUETOOTH_CONNECT
                            ) != PackageManager.PERMISSION_GRANTED
                        ) {
                            // TODO: Consider calling
                            //    ActivityCompat#requestPermissions
                            // here to request the missing permissions, and then overriding
                            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                            //                                          int[] grantResults)
                            // to handle the case where the user grants the permission. See the documentation
                            // for ActivityCompat#requestPermissions for more details.
                            return
                        }
                        //System.out.prince
                        bthid.registerApp(sdpRecord, null, qosOut, { it.run() }, btcallback)
//                            bthid.registerApp(
//                                    Constants.SDP_RECORD, null, Constants.QOS_OUT, Executor { obj: Runnable -> obj.run() }, btcallback
//                            )

//                        IntentData.bthid = bthid
//                        IntentData.bluetoothAdapter = bluetoothAdapter

//                        val d = IntentData(bthid, bluetoothAdapter)
//                        val intent = Intent()
//                        intent.action = "service"
//                        intent.putExtra("d","d")
//                        sendBroadcast(intent)


                    }
                }
                , BluetoothProfile.HID_DEVICE)


//        return Pair(bthid, bluetoothAdapter)


    }

    private val sdpRecord by lazy {
        BluetoothHidDeviceAppSdpSettings(
                "Bluetooth HID Keyboard",
                "Bluetooth HID Keyboard",
                "Fixed Point",
                BluetoothHidDevice.SUBCLASS1_COMBO,
//                DescriptorCollection.MOUSE_KEYBOARD_COMBO
                DescriptorCollection.KeyboardDescriptor
        )
    }

    private val qosOut by lazy {
        BluetoothHidDeviceAppQosSettings(
                BluetoothHidDeviceAppQosSettings.SERVICE_BEST_EFFORT,
                800,
                9,
                0,
                11250,
                BluetoothHidDeviceAppQosSettings.MAX
        )
    }

    /**
     * Class used for the client Binder. Because we know this service always
     * runs in the same process as its clients, we don't need to deal with IPC.
     */
    inner class LocalBinder : Binder() {
        // Return this instance of LocalService so clients can call public methods.
        fun getService(): BluetoothConnectService = this@BluetoothConnectService
    }

    override fun onBind(p0: Intent?): IBinder? {
        return binder
    }

//    in bind service, onCreate and onStartCommand will not run, only startForegroundService will call them
    override fun onCreate() {
        super.onCreate()
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            val channelId = "newChannelId"
//            val channelName = "channelName"
//            val channel =
//                NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_LOW)
//            val manager =
//                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//            manager.createNotificationChannel(channel)
//            val notification: Notification =
//                NotificationCompat.Builder(this, channelId).setAutoCancel(true)
//                    .setCategory(Notification.CATEGORY_SERVICE).setOngoing(true)
//                    .setPriority(NotificationManager.IMPORTANCE_LOW).build()
//            startForeground(1, notification)
//        }
//        audioRecord = OnlyAudioRecorder.instance
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

//        audioRecord.startRecord()

        val name=intent?.getStringExtra("name")
        Toast.makeText(
            applicationContext, "Service has started running in the background",
            Toast.LENGTH_SHORT
        ).show()
        if (name != null) {
            Log.d("Service Name",name)
        }
        Log.d("Service Status","Starting Service")
        for (i in 1..10)
        {
            Thread.sleep(100)
            Log.d("Status", "Service $i")
        }
//        stopSelf()
        return START_STICKY
    }

    override fun stopService(name: Intent?): Boolean {
        Log.d("Stopping","Stopping Service")

        return super.stopService(name)
    }

    override fun onDestroy() {

//        audioRecord.stopRecord()

        Toast.makeText(
            applicationContext, "Service execution completed",
            Toast.LENGTH_SHORT
        ).show()
        Log.d("Stopped","Service Stopped")
        super.onDestroy()
    }

}

