package com.cs246.ble.sensortag;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.cs246.ble.BleService;
import com.cs246.ble.GuiCallback;

/**
 * Created by Quade on 3/19/16.
 */
public class BleConnection {
    private static volatile BleConnection mInstance;
    private static boolean mIsConnected;
    private static BluetoothGatt mGatt;
    private static ConnectionCallback connectionCallback;
    private static ResultCallback enableCallback;
    private static ResultCallback readCallback;
    private static ResultCallback notifyCallback;
    private static BleService mService;
    private static GuiCallback mGuiCallback;

    interface ConnectionCallback {
        void onConnect();
    }

    private BleConnection() { }

    public static BleConnection getInstance() {
        if (mInstance == null) {
            synchronized (BleConnection.class) {
                if (mInstance == null) {
                    mInstance = new BleConnection();
                }
            }
        }

        return mInstance;
    }

    public void connect(Context context, BluetoothDevice device, ConnectionCallback callback) {
        if (mIsConnected == true)
        mIsConnected = true;
        mGatt = device.connectGatt(context, false, mGattCallback);
        connectionCallback = callback;
        System.out.println("GATT" + mGatt.toString());
    }

    public void disconnect() {
        if (mIsConnected == true) {
            mIsConnected = false;
            mGatt.disconnect();
        }
    };

    public void setEnableCallback(ResultCallback callback, BleService service) {
        enableCallback = callback;
        mService = service;
    }

    public void setReadCallback(ResultCallback callback, BleService service) {
        readCallback = callback;
        mService = service;
    }

    public void setNotifyCallback(ResultCallback callback, BleService service) {
        notifyCallback = callback;
        mService = service;
    }

    public void updateGui (GuiCallback callback) {
        mGuiCallback = callback;
        mHandler.sendMessage(Message.obtain(null, UPDATE_GUI, null));
    }

    public BluetoothGatt getGatt() {
        return mGatt;
    }

    boolean isConnected() {
        return mIsConnected;
    }

    public BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);
            Log.i("onConnectionStateChange", "Status: " + status);
            switch (newState) {
                case BluetoothProfile.STATE_CONNECTED:
                    Log.i("gattCallback", "STATE_CONNECTED");
                    gatt.discoverServices();
                    break;
                case BluetoothProfile.STATE_DISCONNECTED:
                    Log.e("gattCallback", "STATE_DISCONNECTED");
                    break;
                default:
                    Log.e("gattCallback", "STATE_OTHER");
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);
            Log.i("onDiscoverServices", "Discovered Service");
            connectionCallback.onConnect();
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt,
                                         BluetoothGattCharacteristic characteristic,
                                         int status) {
            super.onCharacteristicRead(gatt, characteristic, status);

            String data = null;

            if (readCallback != null) {
                data = mService.extractData(characteristic.getValue());
                readCallback.onResult(new Result(mService, data));
            }

            Log.d("Sensortag Service", "read -- service_name: " + mService.getName() + ", read_value: " + data);
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt,
                                          BluetoothGattCharacteristic characteristic,
                                          int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);

            if (enableCallback != null) {
                enableCallback.onResult(new Result(mService, mService.getName()));
            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt,
                                            BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicChanged(gatt, characteristic);

            String data = null;

            if (notifyCallback != null) {
                data = mService.extractData(characteristic.getValue());
                notifyCallback.onResult(new Result(mService, data));
            }

            Log.d("Sensortag Service", "notification -- service_name: " + mService.getName() + ", notified_value: " + data);
        }
    };

    private static final int UPDATE_GUI = 101;
    private static Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            BluetoothGattCharacteristic characteristic;
            if (msg.what == UPDATE_GUI) {
                mGuiCallback.update();
            }
        }
    };

}
