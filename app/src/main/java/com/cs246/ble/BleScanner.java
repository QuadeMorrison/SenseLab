package com.cs246.ble;

/**
 * Created by Quade on 3/18/16.
 */
public interface BleScanner {
    interface ScannerCallback {
        void onDeviceFound(GenericBleDevice device);
    }

    boolean isConnection();
    boolean isScanning();
    void start();
    void stop();
}
