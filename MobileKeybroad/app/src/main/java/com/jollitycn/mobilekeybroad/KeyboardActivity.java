package com.jollitycn.mobilekeybroad;

import android.Manifest;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothHidDevice;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.RequiresPermission;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentTransaction;

import com.jasonhong.core.common.Callback;
import com.jollitycn.mobilekeybroad.bluetooth.BluetoothCallback;
import com.jollitycn.mobilekeybroad.bluetooth.BluetoothHidMouseService;
import com.jollitycn.mobilekeybroad.bluetooth.ConnectedDevice;
import com.jollitycn.mobilekeybroad.bluetooth.BluetoothConnectService;
import com.jollitycn.mobilekeybroad.bluetooth.KeyboardReport2;
import com.jollitycn.mobilekeybroad.bluetooth.ModifierKeys;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.jollitycn.mobilekeybroad.bluetooth.BluetoothHidMouseService.bluetoothDevice;
//
//import static com.jollitycn.mobilekeybroad.bluetooth.IntentData.bluetoothAdapter;
//import static com.jollitycn.mobilekeybroad.bluetooth.IntentData.bthid;

public class KeyboardActivity extends AppCompatActivity {
    private static final int REQUEST_ENABLE_BT = 1;
    private static final int MY_PERMISSIONS_REQUEST_BLUETOOTH_CONNECT = 2;
    private static final int MY_PERMISSIONS_REQUEST_BLUETOOTH_SCAN = 3;
    private BluetoothConnectService mService;
    private BluetoothHidMouseService mouseService;
    private Boolean mBound = false;

//     public void onBluetoothNotEnabled() {
//         Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//        startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
//    }
    /** Defines callbacks for service binding, passed to bindService().  */

//    BluetoothConnectService bthid ;
    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance.
            BluetoothConnectService.LocalBinder binder = (BluetoothConnectService.LocalBinder) service;
            mService = binder.getService();
            mBound = true;
            System.out.println("*** onServiceConnected ");
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
////                mService.connect(TARGET_DEVICE_NAME);
//
//            }




        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mBound = false;
        }
    };

    private ServiceConnection bluetoothHidServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance.
            BluetoothHidMouseService.MyBinder binder = (BluetoothHidMouseService.MyBinder) service;
            mouseService = binder.getService();
//            binder.setBluetoothDevice(selectedDevice);
//            mouseService
            mBound = true;
            System.out.println("*** onmouse service  ");
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
////                mService.connect(TARGET_DEVICE_NAME);
//
//            }
//            initBluetooth();

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mBound = false;
        }
    };

    private KeyboardView keyboardView;
    private TouchTrackView  touchTrackView;
    private Keyboard keyboard;

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_BLUETOOTH_CONNECT:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // 权限被用户授予，可以继续你的操作
                    initBluetooth();
                } else {
                    // 权限被用户拒绝，你可以决定是否再次请求或给出提示
                }
                return;
        }
    }
    View fragmentContainer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


//        if (ActivityCompat.checkSelfPermission(KeyboardActivity.this, android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
//            // TODO: Consider calling
//            //    ActivityCompat#requestPermissions
            ActivityCompat.requestPermissions(KeyboardActivity.this,
                    new String[]{android.Manifest.permission.BLUETOOTH_CONNECT, android.Manifest.permission.BLUETOOTH_SCAN},
                    MY_PERMISSIONS_REQUEST_BLUETOOTH_CONNECT);
//        }



//        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
//        setContentView(binding.getRoot());
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
// 如果需要监听其他蓝牙相关的广播，可以在这里继续添加
// filter.addAction(...);
//        private val binder = LocalBinder();
//        btAdapter = BluetoothAdapter.getDefaultAdapter();
        registerReceiver(mReceiver, filter);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_FULLSCREEN | // 隐藏状态栏
                            // 如果需要，还可以添加其他标志，如 View.SYSTEM_UI_FLAG_HIDE_NAVIGATION 来隐藏导航栏
                            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY // 沉浸式模式（可选）
            );
        }

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_keybroad);
//        setContentView(binding.getRoot());
        // 加载键盘布局
        // 隐藏ActionBar
//        getSupportActionBar().hide();




//        for (BluetoothDevice device : BluetoothAdapter.getDefaultAdapter().getBondedDevices()) {
//        }
        keyboard = new Keyboard(this, R.xml.keyboard_layout);

        // 获取 KeyboardView 组件
        keyboardView = findViewById(R.id.keyboard_view);
        fragmentContainer =  findViewById(R.id.fragment_container);
        // 设置键盘布局
        keyboardView.setKeyboard(keyboard);

        // 设置监听器
        keyboardView.setOnKeyboardActionListener(new KeyboardView.OnKeyboardActionListener() {
            @Override
            @RequiresPermission(allOf = {
                    android.Manifest.permission.BLUETOOTH_CONNECT})
            public void onPress(int primaryCode) {
                if (primaryCode == -1) {
                    // 显示鼠标控制Fragment
                    keyboardView.setVisibility(View.GONE);
//                    touchTrackView = mouseControlFragment.getTouchTrackView();
//                    touchTrackView.setCallback(new Callback<MotionPoint>() {
//                        @Override
//                        public void onAction(MotionPoint o) {
//
//                        }
//
//                        @Override
//                        public void onResult(MotionPoint o) {
//
//                        }
//
//                        @Override
//                        public void onError(MotionPoint o, Exception e) {
//
//                        }
//                    });
                    MouseControlFragment mouseControlFragment = new MouseControlFragment();
                    fragmentContainer.setVisibility(View.VISIBLE);


                    //setting callback
                   mouseControlFragment.setCallback(new Callback<TouchTrackView>() {
                       @Override
                       public void onAction(TouchTrackView o) {

                       }

                       @Override
                       public void onResult(TouchTrackView o) {
touchTrackView = o;touchTrackView.setCallback(new Callback<MotionPoint>() {
                               @Override
                               public void onAction(MotionPoint o) {
                                   switch (o.getEvent().getAction()){
                                       case MotionEvent.ACTION_MOVE:
                                       case MotionEvent.ACTION_DOWN:
                                           mouseService.sendMouseEvent(o.getPoint().x,o.getPoint().y, (byte) 0, (byte) 0);
                                           break;

                                       case MotionEvent.ACTION_UP:
                                       case MotionEvent.ACTION_CANCEL:
                                           break;
                                   }
}

                               @Override
                               public void onResult(MotionPoint o) {

                               }

                               @Override
                               public void onError(MotionPoint o, Exception e) {

                               }
                           });
                       }

                       @Override
                       public void onError(TouchTrackView o, Exception e) {

                       }
                   });
                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    transaction.add(R.id.fragment_container, mouseControlFragment);
                    transaction.commit();
                } else {
                    if(mService.getBthid().getConnectedDevices()==null) {
                        registerApp();
                    }else {
//                    touchTrackView = null;
                        sendKeydown(primaryCode);
                        System.out.println("onPress" + primaryCode);
                    }
                }


            }

            @Override
            @RequiresPermission(allOf = {
                    android.Manifest.permission.BLUETOOTH_CONNECT})
            public void onRelease(int primaryCode) {

                System.out.println("onRelease" + primaryCode);
                if (primaryCode != -1) {
                    sendKeyUp(primaryCode);
                }
            }

            @Override
            public void onKey(int primaryCode, int[] keyCodes) {
                // 处理按键事件，例如更新 EditText 的值
                System.out.println("onKey" + primaryCode);
            }

            @Override
            public void onText(CharSequence text) {
                System.out.println("onText" + text);
            }

            @Override
            public void swipeLeft() {
                System.out.println("swipeLeft");
            }

            @Override
            public void swipeRight() {
                System.out.println("swipeRight");
            }

            @Override
            public void swipeDown() {
                System.out.println("swipeDown");
            }

            @Override
            public void swipeUp() {
                System.out.println("swipeUp");
            }
            // 实现其他必要的方法...
        });

        // 显示键盘（如果需要）
        keyboardView.setVisibility(View.VISIBLE);



    }

    // 在 showMouseInterface() 方法中设置 mouseInterfaceView 为可见
//    private void showMouseInterface() {
//        mouseInterfaceView.setVisibility(View.VISIBLE);
//        // 可以添加其他逻辑来调整 mouseInterfaceView 的大小和位置
//    }

    @RequiresPermission(allOf = {
            android.Manifest.permission.BLUETOOTH_CONNECT})
    private void requestBluetooth() {
        if (!BluetoothAdapter.getDefaultAdapter().isEnabled()) {
            // 蓝牙未开启，弹出对话框请求用户开启蓝牙
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
    }


    //    private void requestBluetooth() {
//        if (!BluetoothAdapter.getDefaultAdapter().isEnabled()) {
//            // 蓝牙未开启，弹出对话框请求用户开启蓝牙
//            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
//            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
//        }
//    }
    ModifierKeys keyboardReport2 = new ModifierKeys();

    @RequiresPermission(allOf = {
            android.Manifest.permission.BLUETOOTH_CONNECT})
    private void sendKeydown(int primaryCode) {
        // if keyCode is 0, then key will be 0


// 获取KeyEventMap
        Map<Integer, Integer> keyEventMap = KeyboardReport2.getKeyEventMap(); // 注意这里假设了Kotlin提供了一个getter方法
        int keyCode = primaryCode;
// 由于companion object中的常量是静态的，你可以直接访问它
        int key = keyEventMap.getOrDefault(keyCode, 0);
        byte keyByte = (byte) (key & 0xFF); // 安全地将int转换为byte
        keyboardReport2.setKey(keyByte);
//        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
//            // TODO: Consider calling
//            //    ActivityCompat#requestPermissions
//            // here to request the missing permissions, and then overriding
//            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//            //                                          int[] grantResults)
//            // to handle the case where the user grants the permission. See the documentation
//            // for ActivityCompat#requestPermissions for more details.
//            return;
//        }
        mService.getBthid().sendReport(ConnectedDevice.Companion.getDevice(), KeyboardReport2.ID, keyboardReport2.getBytes());
    }

    //    boolean paused = false;
//    InputMethodManager imm;
    public void onResume() {
        super.onResume();
//        paused = false;
//                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0)
//        imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT,0)
//        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
    }

//    override fun onWindowFocusChanged(isFocused: Boolean) {
//        val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
//        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED,0)
//    }

    public void onStart() {
        super.onStart();


        // 创建一个Intent来绑定到你的服务
        Intent intent = new Intent(this, BluetoothConnectService.class);
        // 调用bindService方法来绑定服务
        bindService(intent, connection, Context.BIND_AUTO_CREATE);
          Intent mouseintent = new Intent(this, BluetoothHidMouseService.class);
                intent.putExtra(BluetoothDevice.EXTRA_DEVICE, selectedDevice);
        bindService(mouseintent, bluetoothHidServiceConnection, Context.BIND_AUTO_CREATE);
//        }
    }

    @RequiresPermission(allOf = {
            android.Manifest.permission.BLUETOOTH_CONNECT,
            android.Manifest.permission.BLUETOOTH_SCAN})
    private void initBluetooth() {


//        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        int state = BluetoothAdapter.getDefaultAdapter().getState();


        switch (state) {
            case 12://STATE_ON 蓝牙是打开的
                connectOrSelect();
                break;
            case 10://STATE_OFF没有打开蓝牙
                requestBluetooth();
                break;
        }

        System.out.println("Bluetooth：：：：：" + state);
    }

    public void onStop() {
        super.onStop();
    }

    public void onRestart() {
        super.onRestart();
        showDeviceSelectionDialog();
    }

    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
        unbindService(connection);
        unbindService(bluetoothHidServiceConnection);
        mBound = false;
    }

    @Override
    @RequiresPermission(allOf = {
            android.Manifest.permission.BLUETOOTH_CONNECT,
            android.Manifest.permission.BLUETOOTH_SCAN})
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == RESULT_OK) {
                connectOrSelect();

            } else {
                // 用户未打开蓝牙，执行相应的处理
//                bluetoothAdapter.cancelDiscovery();
            }
        }
    }
    @RequiresPermission(allOf = {
            android.Manifest.permission.BLUETOOTH_CONNECT,
            android.Manifest.permission.BLUETOOTH_SCAN})
    private void connectOrSelect() {
        boolean isConnected = false;
//        isConnected = checkConnected();
        if (!isConnected) {
            System.err.println("no device connect,please choose one ");
            bluetoothAdapter.startDiscovery();
            showDeviceSelectionDialog();
        }
    }

    @RequiresPermission(allOf = {
            android.Manifest.permission.BLUETOOTH_CONNECT})
    private boolean checkConnected() {
        boolean isConnected = false;
        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
        try {
            for (BluetoothDevice device : pairedDevices) {

                // 使用反射调用BluetoothDevice的isConnected()方法
                Method isConnectedMethod = BluetoothDevice.class.getDeclaredMethod("isConnected", (Class[]) null);
                isConnectedMethod.setAccessible(true);
                isConnected = (boolean) isConnectedMethod.invoke(device);
                if (isConnected) {// 处理connectedDevices列表，如展示给用户或进行其他操作
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    //自动连接到
                    selectedDevice  = device;
                    bluetoothDevice = selectedDevice;
                    Toast.makeText(KeyboardActivity.this, "已经连接到: " + device.getName(), Toast.LENGTH_SHORT).show();
//
                    registerApp();
                    isConnected = true;
                    break;
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
            // 处理反射调用中的异常
        }

        return isConnected;
    }

    // 创建一个数组来存储设备名称
    List<String> deviceNames = new ArrayList();
    List<BluetoothDevice> deviceArray = new ArrayList();
    BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    // ... 其他代码 ...
    @RequiresPermission(allOf = {
            android.Manifest.permission.BLUETOOTH_CONNECT,
            android.Manifest.permission.BLUETOOTH_SCAN})
    private void showDeviceSelectionDialog() {

        if (bluetoothAdapter == null) {
            // 设备不支持蓝牙
            return;
        }

        // 获取已配对的设备
        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();

        if (pairedDevices.isEmpty()) {
            // 没有已配对的设备
            Toast.makeText(this, "没有已配对的蓝牙设备", Toast.LENGTH_SHORT).show();
            return;
        }


        int i = 0;
        for (BluetoothDevice device : pairedDevices) {
            try {
                // 使用反射调用BluetoothDevice的isConnected()方法
                Method isConnectedMethod = BluetoothDevice.class.getDeclaredMethod("isConnected", (Class[]) null);
                isConnectedMethod.setAccessible(true);
                boolean isConnected = (boolean) isConnectedMethod.invoke(device);
                    if (isConnected) {// 处理connectedDevices列表，如展示给用户或进行其他操作
                        deviceNames.add(device.getName() + "-" + (isConnected ? "CONNECTED" : ""));
                    }else{
                        deviceNames.add(device.getName()) ;
                    };
                deviceArray.add(device) ;
//                    }
            } catch (Exception e) {
                // 处理反射调用中的异常
            }
            i++;
        }

        // 创建适配器
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, deviceNames);

        // 创建并显示对话框
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("选择蓝牙设备");
        builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
            @Override
            @RequiresPermission(allOf = {
                    Manifest.permission.BLUETOOTH_CONNECT})
            public void onClick(DialogInterface dialog, int which) {
                // 用户点击了某个设备
                  selectedDevice = deviceArray.get(which);
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                Toast.makeText(KeyboardActivity.this, "选择了设备: " + selectedDevice.getName(), Toast.LENGTH_SHORT).show();
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    Toast.makeText(KeyboardActivity.this, "正在连接: " + selectedDevice.getName(), Toast.LENGTH_SHORT).show();
                    registerApp();
//                }
//                }

                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void registerApp() {
        BluetoothHidMouseService.bluetoothDevice = selectedDevice;
        mService.connect(selectedDevice);
        mouseService.onStartCommand(getIntent(),0,0);
    }

    public static BluetoothDevice selectedDevice;
    // ... 其他代码 ...
    @RequiresPermission(allOf = {
            android.Manifest.permission.BLUETOOTH_CONNECT,
            android.Manifest.permission.BLUETOOTH_SCAN})
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            // 当发现蓝牙设备时
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // 从Intent中获取蓝牙设备
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // 在这里你可以处理找到的设备，比如添加到列表中显示给用户
                // ...
                deviceNames.add(0,device.getName());
                deviceArray.add(0,device);
            }

            // 当蓝牙扫描完成时
            else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                // 在这里你可以停止扫描相关的UI操作或处理扫描完成后的逻辑
                // ...
//              .cancelDiscovery();
            }

            // 你还可以监听其他蓝牙相关的广播，比如蓝牙状态改变等
            // ...
        }
    };

    @RequiresPermission(allOf = {
            android.Manifest.permission.BLUETOOTH_CONNECT})
    public void sendAllKeyUp() {
        keyboardReport2.reset();
        if (!mService.getBthid().sendReport(ConnectedDevice.Companion.getDevice(), KeyboardReport2.ID, keyboardReport2.getBytes())) {
            Log.e(" ", "Report wasn't sent");
        }
    }


    @RequiresPermission(allOf = {
            android.Manifest.permission.BLUETOOTH_CONNECT})
    public boolean sendKeyUp(Integer keyCode) {
        var key = KeyboardReport2.getKeyEventMap().get(keyCode) != null ? KeyboardReport2.getKeyEventMap().get(keyCode) : 0;
        // scan code is press code, press code + 0x80 will be release code
        key = key + 0x80;
        keyboardReport2.setKey((byte) key);// = key.toByte();
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
//            // TODO: Consider calling
//            //    ActivityCompat#requestPermissions
//            // here to request the missing permissions, and then overriding
//            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//            //                                          int[] grantResults)
//            // to handle the case where the user grants the permission. See the documentation
//            // for ActivityCompat#requestPermissions for more details.
//            return false;
//        }
        mService.getBthid().sendReport(
                ConnectedDevice.Companion.getDevice(), KeyboardReport2.ID, keyboardReport2.getBytes()
        );
        return true;
    }
    @RequiresPermission(allOf = {
            android.Manifest.permission.BLUETOOTH_CONNECT})
    public void sendModifierKeyDown(Integer keyCode) {
        if (keyCode == KeyEvent.KEYCODE_ALT_LEFT || keyCode == KeyEvent.KEYCODE_ALT_RIGHT)
            keyboardReport2.setLeftAlt(true);
        if (keyCode == KeyEvent.KEYCODE_CTRL_LEFT || keyCode == KeyEvent.KEYCODE_CTRL_RIGHT)
            keyboardReport2.setLeftControl(true);
        if (keyCode == KeyEvent.KEYCODE_SHIFT_LEFT || keyCode == KeyEvent.KEYCODE_SHIFT_RIGHT)
            keyboardReport2.setLeftShift(true);
        if (keyCode == KeyEvent.KEYCODE_WINDOW) keyboardReport2.setLeftGui(true);
        if (keyCode == KeyEvent.KEYCODE_CAPS_LOCK) keyboardReport2.setLeftControl(true);
        mService.getBthid().sendReport(
                ConnectedDevice.Companion.getDevice(), KeyboardReport2.ID, keyboardReport2
                        .getBytes());
    }
    @RequiresPermission(allOf = {
            android.Manifest.permission.BLUETOOTH_CONNECT})
    public boolean sendKeyDown(Integer keyCode) {
        if (isShiftPressed) {
            keyboardReport2.setLeftShift(true);
            isShiftPressedWithOthers = true;
        }
        if (isCtrlPressed) keyboardReport2.setLeftControl(true);
        if (isAltPressed) keyboardReport2.setLeftAlt(true);
        if (isWindowPressed) keyboardReport2.setLeftGui(true);
        if (isCapsLockPressed) keyboardReport2.setRightControl(true);

        int key = 0;

        // replace ALt Space to Alt Tab
        if ((keyCode == KeyEvent.KEYCODE_SPACE || keyCode == KeyEvent.KEYCODE_TAB) && isAltPressed) {
            Integer mapKey = KeyboardReport2.getKeyEventMap().get(KeyEvent.KEYCODE_TAB);
            key = mapKey != null ? mapKey : 0;
        } else {
            // if keyCode is 0, then key will be 0
            key = KeyboardReport2.getKeyEventMap().get(keyCode) != null ? KeyboardReport2.getKeyEventMap().get(keyCode) : 0;
        }

        keyboardReport2.setKey((byte) key);
        mService.getBthid().sendReport(
                ConnectedDevice.Companion.getDevice(), KeyboardReport2.ID, keyboardReport2.getBytes()
        );
        return true;
    }
    @RequiresPermission(allOf = {
            android.Manifest.permission.BLUETOOTH_CONNECT})
  public   boolean onKeyUp(Integer keyCode ,KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_CAPS_LOCK) { isCapsLockPressed = false; }
        if (keyCode == KeyEvent.KEYCODE_WINDOW) { isWindowPressed = false ;}
        if (keyCode == KeyEvent.KEYCODE_ALT_LEFT || keyCode == KeyEvent.KEYCODE_ALT_RIGHT) { isAltPressed = false; }
        if (keyCode == KeyEvent.KEYCODE_CTRL_LEFT || keyCode == KeyEvent.KEYCODE_CTRL_RIGHT) { isCtrlPressed = false ;}
        if (keyCode == KeyEvent.KEYCODE_SHIFT_LEFT || keyCode == KeyEvent.KEYCODE_SHIFT_RIGHT) {
            if (!isShiftPressedWithOthers) {
                sendModifierKeyDown(KeyEvent.KEYCODE_SHIFT_LEFT);
                sendKeyUp(KeyEvent.KEYCODE_SHIFT_LEFT);
            }
            isShiftPressed = false;
        }

        // alt tab switch window is alt key down, tab key down, tab up, alt up
        if (((keyCode == KeyEvent.KEYCODE_SPACE || keyCode == KeyEvent.KEYCODE_TAB) && isAltPressed) || ((keyCode == KeyEvent.KEYCODE_TAB) && isWindowPressed)) {
            sendKeyUp(KeyEvent.KEYCODE_TAB);
//            println(" send alt space $keyCode")
        }
//        if (isWindowPressed || isCtrlPressed || isShiftPressed || isCapsLockPressed || isAltPressed){
//            sendKeyUp(keyCode)
//        }
        else {
//            println(" send key up $keyCode")
//            sendKeyUp(keyCode)


            sendAllKeyUp();
//            if (isAltPressed) sendModifierKeyDown(KeyEvent.KEYCODE_ALT_LEFT)
//            if (isCapsLockPressed) sendModifierKeyDown(KeyEvent.KEYCODE_CTRL_LEFT)
//            if (isCtrlPressed) sendModifierKeyDown(KeyEvent.KEYCODE_CTRL_LEFT)
//            if (isShiftPressed) sendModifierKeyDown(KeyEvent.KEYCODE_SHIFT_LEFT)
//            if (isWindowPressed) sendModifierKeyDown(KeyEvent.KEYCODE_WINDOW)
            // after win r, release win
            isWindowPressed = false;
            isCtrlPressed = false;
            isShiftPressed = false;
            isCapsLockPressed = false;
            isAltPressed = false;
        }


//        return super.onKeyUp(keyCode, event)
        return true;
    }
    @RequiresPermission(allOf = {
            android.Manifest.permission.BLUETOOTH_CONNECT,
            android.Manifest.permission.BLUETOOTH_SCAN})
    public   boolean onKeyDown(Integer keyCode ,KeyEvent event) {
//                   println("--- keycode is $keyCode")
        if (keyCode == KeyEvent.KEYCODE_CAPS_LOCK) {
            isCapsLockPressed = true;
            sendModifierKeyDown(keyCode);
//                if (!isCapsLockPressed) sendModifierKeyDown(keyCode)
        }
        if (keyCode == KeyEvent.KEYCODE_WINDOW) {
            isWindowPressed = true;
            sendModifierKeyDown(keyCode);
//                if (!isWindowPressed) sendModifierKeyDown(keyCode)
        }
        if (keyCode == KeyEvent.KEYCODE_ALT_LEFT || keyCode == KeyEvent.KEYCODE_ALT_RIGHT) {
            isAltPressed = true;
            // alt tab swith window, alt down, tab down, tab up, alt up, keey alt down
            // alt key down
            sendModifierKeyDown(keyCode);
            // hold alt, only send one alt key down
//                if (!isAltPressed) sendModifierKeyDown(keyCode)
        }
        if (keyCode == KeyEvent.KEYCODE_CTRL_LEFT || keyCode == KeyEvent.KEYCODE_CTRL_RIGHT) {
            isCtrlPressed = true;
            sendModifierKeyDown(keyCode);
//                if (!isCtrlPressed) sendModifierKeyDown(keyCode)
        }
        if (keyCode == KeyEvent.KEYCODE_SHIFT_LEFT || keyCode == KeyEvent.KEYCODE_SHIFT_RIGHT) {
            // shift pressed and hold only send one shift key event when released for shift switch input method
            isShiftPressed = true;
            isShiftPressedWithOthers = false;
        }

        if (!List.of(KeyEvent.KEYCODE_CAPS_LOCK, KeyEvent.KEYCODE_ALT_LEFT, KeyEvent.KEYCODE_ALT_RIGHT, KeyEvent.KEYCODE_WINDOW,
                KeyEvent.KEYCODE_CTRL_LEFT, KeyEvent.KEYCODE_CTRL_RIGHT, KeyEvent.KEYCODE_SHIFT_LEFT, KeyEvent.KEYCODE_SHIFT_RIGHT).contains(keyCode))
            sendKeyDown(keyCode);
//                avoid continuous backspace
        if (keyCode == KeyEvent.KEYCODE_DEL) sendKeyUp(keyCode);


//        return super.onKeyDown(keyCode, event)
        return true;
    }

    boolean isWindowPressed;

    boolean isCapsLockPressed;
   boolean isShiftPressedWithOthers;
    boolean isShiftPressed;
    boolean isCtrlPressed;
    boolean isAltPressed;
}