package com.cs246.ble.sensortag;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;
import android.widget.Toast;

import com.cs246.ble.GenericBleDevice;
import com.cs246.ble.BleScanner;

import java.util.regex.Pattern;

/**
 * Created by Quade on 3/19/16.
 */
public class SensortagScanner implements BleScanner {
    private static final String TAG = SensortagScanner.class.getName();
    private BluetoothAdapter mBluetoothAdapter;
    private String[] mFilters;
    private boolean mScanning;
    private ScannerCallback mScannerCallback;

    private SensortagScanner() { }

    public SensortagScanner(BluetoothAdapter bluetoothAdater,
                            ScannerCallback callback)
    {
        mBluetoothAdapter = bluetoothAdater;
        mScannerCallback = callback;
    }

    public SensortagScanner(BluetoothAdapter bluetoothAdater,
            String[] filters,
            ScannerCallback callback)
    {
        mBluetoothAdapter = bluetoothAdater;
        mFilters = filters;
        mScannerCallback = callback;
    }

    @Override
    public boolean isScanning() {
        return mScanning;
    }

    @Override
    public void start() {
        if (!mScanning) {
            mScanning = true;
            mBluetoothAdapter.startLeScan(mLeScanCallback);
        }
    }

    @Override
    public void stop() {
        if (mScanning) {
            mScanning = false;
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
        }
    }

    @Override
    public boolean isConnection() { return BleConnection.getInstance().isConnected(); }

    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
            if (mFilters == null || filterByName(device.getName())) {
                Sensortag sensortag = new Sensortag(device, rssi);
                Log.d(TAG, "onLeScan -- device_name: " + device.getName() + ", rssi: " + rssi);

                mScannerCallback.onDeviceFound(sensortag);
            }
        }

        private boolean filterByName(String name) {
            boolean matches = false;

            if (name != null) {
                String pattern = "(.*)" + mFilters[0] + "(.*)";

                for (int i = 1; i < mFilters.length; ++i) {
                    pattern += "|(.*)" + mFilters[i] + "(.*)";
                }

                Pattern deviceNamePattern = Pattern.compile(pattern);
                matches = deviceNamePattern.matcher(name).matches();

                Log.d(TAG, "filterByName -- device_name: " + name + ", " +
                        "pattern: " + pattern + ", " +
                        "matches: " + matches);
            }

            return matches;
        }
    };
}
