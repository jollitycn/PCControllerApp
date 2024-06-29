package com.jollitycn.mobilekeybroad.bluetooth;

import android.Manifest;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothHidDevice;
import android.bluetooth.BluetoothHidDeviceAppQosSettings;
import android.bluetooth.BluetoothHidDeviceAppSdpSettings;
import android.bluetooth.BluetoothProfile;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Binder;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresPermission;
import androidx.core.app.ActivityCompat;

import com.example.bluetoothconnect.DescriptorCollection;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.concurrent.Executor;

public class BluetoothHidMouseService extends Service {
    // 定义 Binder 类，用于客户端与服务交互
    private   IBinder mBinder = new MyBinder();
    // 定义一个接口，包含客户端需要调用的方法
    public interface MyServiceInterface {
        // 方法定义
//        void doSomething(String param);
          void setBluetoothDevice(BluetoothDevice selectedDevice);
    }

    // 绑定器内部类
    public class MyBinder extends Binder   {
        public BluetoothHidMouseService getService() {
            return BluetoothHidMouseService.this;
        }
//
//        @Override
//        public void setBluetoothDevice(BluetoothDevice selectedDevice) {
//            BluetoothHidMouseService.this.bluetoothDevice = selectedDevice;
//        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        // 当客户端绑定服务时，返回 Binder
        return mBinder;
    }
    private BluetoothHidDevice bluetoothHidDevice;
    public static BluetoothDevice bluetoothDevice;
    // ... 其他必要的成员变量和初始化代码 ...  
    // 假设这个方法返回鼠标的报告描述符

    // 假设这个方法返回鼠标的报告描述符
//    private byte[] getMouseReportDescriptor() {
//        //该报告描述符号由HID Descriptor tool生成
////        以下是一个USB鼠标的报告描述符
//        return new byte[]{
//                (byte) 0x05, (byte) 0x01,                    // USAGE_PAGE (Generic Desktop)
//                (byte) 0x09, (byte) 0x06,                  // USAGE (Keyboard)
//                (byte) 0xa1, (byte) 0x01,                    // COLLECTION (Application)   这就是一个MAIN Item，可以对应上图查看
//                (byte) 0x05, (byte) 0x07,                    //   USAGE_PAGE (Keyboard)
//                (byte) 0x19, (byte) 0xe0,                    //   USAGE_MINIMUM (Keyboard LeftControl)
//                (byte) 0x29, (byte) 0xe7,                    //   USAGE_MAXIMUM (Keyboard Right GUI)
//                (byte) 0x15, (byte) 0x00,                    //   LOGICAL_MINIMUM (0)
//                (byte) 0x25, (byte) 0x01,                    //   LOGICAL_MAXIMUM (1)
//                (byte) 0x75, (byte) 0x01,                    //   REPORT_SIZE (1)
//                (byte) 0x95, (byte) 0x08,                    //   REPORT_COUNT (8)
//                (byte) 0x81, (byte) 0x02,                    //   INPUT (Data,Var,Abs)     这就是一个MAIN Item，可以对应上图查看
//                (byte) 0x95, (byte) 0x01,                    //   REPORT_COUNT (1)
//                (byte) 0x75, (byte) 0x08,                    //   REPORT_SIZE (8)
//                (byte) 0x81, (byte) 0x03,                    //   INPUT (Cnst,Var,Abs)     这就是一个MAIN Item，可以对应上图查看
//                (byte) 0x95, (byte) 0x05,                    //   REPORT_COUNT (5)
//                (byte) 0x75, (byte) 0x01,                    //   REPORT_SIZE (1)
//                (byte) 0x05, (byte) 0x08,                    //   USAGE_PAGE (LEDs)
//                (byte) 0x19, (byte) 0x01,                    //   USAGE_MINIMUM (Num Lock)
//                (byte) 0x29, (byte) 0x05,                    //   USAGE_MAXIMUM (Kana)
//                (byte) 0x91, (byte) 0x02,                    //   OUTPUT (Data,Var,Abs)     这就是一个MAIN Item，可以对应上图查看
//                (byte) 0x95, (byte) 0x01,                    //   REPORT_COUNT (1)
//                (byte) 0x75, (byte) 0x03,                    //   REPORT_SIZE (3)
//                (byte) 0x91, (byte) 0x03,                    //   OUTPUT (Cnst,Var,Abs)     这就是一个MAIN Item，可以对应上图查看
//                (byte) 0x95, (byte) 0x06,                    //   REPORT_COUNT (6)
//                (byte) 0x75, (byte) 0x08,                    //   REPORT_SIZE (8)
//                (byte) 0x15, (byte) 0x00,                    //   LOGICAL_MINIMUM (0)
//                (byte) 0x25, (byte) 0xFF,                    //   LOGICAL_MAXIMUM (255)
//                (byte) 0x05, (byte) 0x07,                    //   USAGE_PAGE (Keyboard)
//                (byte) 0x19, (byte) 0x00,                    //   USAGE_MINIMUM (Reserved (no event indicated))
//                (byte) 0x29, (byte) 0x65,                    //   USAGE_MAXIMUM (Keyboard Application)
//                (byte) 0x81, (byte) 0x00,                    //   INPUT (Data,Ary,Abs)     这就是一个MAIN Item，可以对应上图查看
//                (byte) 0xc0                           // END_COLLECTION             这就是一个MAIN Item，可以对应上图查看
//        };
//    }

    BluetoothHidDeviceAppSdpSettings sdpSettings = new BluetoothHidDeviceAppSdpSettings(
            "Bluetooth HID Mouse",
            "Bluetooth HID Mouse",
            "Fixed Point",
            BluetoothHidDevice.SUBCLASS1_MOUSE,
//                DescriptorCollection.MOUSE_KEYBOARD_COMBO
            DescriptorCollection.INSTANCE.getMOUSE_RELATIVE_WITH_SCROLL()
    );

    BluetoothHidDeviceAppQosSettings qosSettings = new BluetoothHidDeviceAppQosSettings(
            BluetoothHidDeviceAppQosSettings.SERVICE_BEST_EFFORT,
            800,
            9,
            0,
            11250,
            BluetoothHidDeviceAppQosSettings.MAX
    );
    BluetoothAdapter bluetoothAdapter;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // 初始化蓝牙适配器，检查蓝牙是否开启等...  

//        if (intent != null) {
            // 获取基本数据类型参数
//            int myInt = intent.getIntExtra("key_int", defaultValue);
//            String myString = intent.getStringExtra("key_string");

////            Intent intent = getIntent();
//            bluetoothDevice =  intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
//            if (bluetoothDevice != null) {
//                // 使用 device 对象...
//            }
            // 获取可序列化对象参数
//            BluetoothDevice myObject = (BluetoothDevice) intent.getSerializableExtra("key_object");

            // 获取Parcelable对象参数
//            BluetoothDevice myParcelable = intent.getParcelableExtra("key_parcelable");

            // ... 您的服务逻辑 ...
//        }
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
//        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
//        if (bluetoothAdapter != null) {
        // 假设你有一个方法来获取或创建HID报告描述符
//        byte[] reportDescriptor = getMouseReportDescriptor();
        // 创建并注册蓝牙HID应用

        // 注册HID服务
//            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
//        intent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300); // 可选，设置设备可被发现的时长
//            if (bluetoothAdapter.startActivityForResult(null, REQUEST_ENABLE_BT, intent) > 0) {
        // 这里只是请求蓝牙开启和可被发现，并不是直接注册HID服务
        // HID服务的注册通常涉及底层操作或特定API（如果有的话）

        // 假设你有一个方法来注册HID服务，并传入报告描述符和ServiceListener
        // 这个方法可能依赖于Android版本和厂商API，或者需要自定义实现
        bluetoothAdapter.getProfileProxy(this, mProfileServiceListener, BluetoothProfile.HID_DEVICE);

        // 这里你需要实现一个BluetoothProfile.ServiceListener来处理注册成功或失败的情况  

        // 注册成功后，你可以通过bluetoothHidDevice发送鼠标事件  

        return START_STICKY; // 让服务在被杀死后重启
    }

    private BluetoothHidMouseService mouseService = this;



    // 发送鼠标移动或点击事件的方法（示例）  
//    public void sendMouseEvent(int x, int y, int buttonState) {
//        // 根据x、y坐标和buttonState构建鼠标事件的数据包
//        // 然后通过bluetoothHidDevice发送数据包
//    }

    // ... 其他必要的方法，如onBind、onDestroy等 ...  

    // 实现BluetoothProfile.ServiceListener来处理注册结果  
    private final BluetoothProfile.ServiceListener mProfileServiceListener = new BluetoothProfile.ServiceListener() {
        @Override
        @RequiresPermission(allOf = {
                android.Manifest.permission.BLUETOOTH_CONNECT})
        public void onServiceConnected(int profile, BluetoothProfile proxy) {
            if (profile == BluetoothProfile.HID_DEVICE) {
                bluetoothHidDevice = (BluetoothHidDevice) proxy;
                // 注册成功后的处理逻辑
//                bluetoothHidDevice.registerApp(sdpSettings, new BluetoothProfile.ServiceListener() {
                System.out.println("--- got hid proxy object ");
                BluetoothCallback btcallback = new BluetoothCallback(mouseService, bluetoothHidDevice, bluetoothAdapter, bluetoothDevice.getName());

                bluetoothHidDevice.registerApp(sdpSettings, null, qosSettings, new Executor() {
                    @Override
                    public void execute(Runnable command) {

                    }
                }, btcallback);

            }
        }

        @Override
        public void onServiceDisconnected(int profile) {
            if (profile == BluetoothProfile.HID_DEVICE) {
                bluetoothHidDevice = null;
                // 注册失败或断开连接后的处理逻辑
            }
        }
    };

    // 假设这是一个鼠标报告的字节数组，其中：
// byte 0: 报告ID（如果有的话）
// byte 1-2: X轴移动（例如，使用有符号的短整数）
// byte 3-4: Y轴移动（例如，使用有符号的短整数）
// byte 5: 按钮状态（例如，左键、右键、中键等）
// byte 6: 滚轮滚动（可选）
    private byte[] createMouseReport(short x, short y, byte buttons, byte wheel) {
        byte[] report = new byte[7];
        // 假设我们不使用报告ID，所以第一个字节为0
        report[0] = 0;
        // 设置X轴和Y轴移动（这里需要处理字节顺序，例如使用ByteBuffer）
        ByteBuffer buffer = ByteBuffer.allocate(4);
        buffer.order(ByteOrder.LITTLE_ENDIAN); // 假设是小端字节序
        buffer.putShort(x);
        buffer.putShort(y);
        report[1] = buffer.get(0);
        report[2] = buffer.get(1);
        report[3] = buffer.get(2);
        report[4] = buffer.get(3);
        // 设置按钮状态（例如，1表示左键按下，2表示右键按下，可以组合使用）
        report[5] = buttons;
        // 设置滚轮滚动（可选）nnmm

        report[6] = wheel;
        return report;
    }

// ... 在你的服务中 ...

    // 发送鼠标事件的方法
    public void sendMouseEvent(int x, int y, byte buttons, byte wheel) {
//            if (bluetoothHidDevice != null && bluetoothHidDevice.isConnected()) {
        if (bluetoothHidDevice != null) {
            byte[] report = createMouseReport((short)x,(short) y, buttons, wheel);
            // 发送HID报告
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
            bluetoothHidDevice.sendReport(bluetoothDevice, (int) System.currentTimeMillis(), report);
        } else {
            // 处理蓝牙HID设备未连接或为空的情况
        }
    }

}