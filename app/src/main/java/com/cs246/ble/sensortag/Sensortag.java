package com.cs246.ble.sensortag;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.cs246.ble.BleService;
import com.cs246.ble.GenericBleDevice;
import com.cs246.ble.sensortag.service.AmbientTempService;
import com.cs246.ble.sensortag.service.HumidityService;
import com.cs246.ble.sensortag.service.SensortagService;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Quade on 3/19/16.
 */
public class Sensortag implements GenericBleDevice {
    private BluetoothDevice mDevice;
    private int mRssi;
    private BleConnection mConnection;
    private List<BleService> services;

    private Sensortag() { }

    public Sensortag(BluetoothDevice device, int rssi) {
        mDevice = device;
        mRssi = rssi;
        mConnection = BleConnection.getInstance();

        if (services == null) {
            services = new ArrayList<>();
        }

        services.add(new AmbientTempService());
        services.add(new HumidityService());
    }

    @Override
    public String getName() {
        return mDevice.getName();
    }

    @Override
    public int getRssi() {
        return mRssi;
    }

    @Override
    public List<BleService> getServices() {
        return services;
    }

    @Override
    public void connect(Context context, final DeviceCallback callback) {
        mConnection.connect(context, mDevice, new BleConnection.ConnectionCallback() {
            @Override
            public void onConnect() {
                callback.onConnect();
            }
        });
    }
}
