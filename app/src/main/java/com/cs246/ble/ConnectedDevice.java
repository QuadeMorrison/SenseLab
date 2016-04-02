package com.cs246.ble;

/**
 * Created by Quade on 3/31/16.
 */
public class ConnectedDevice {
    private static ConnectedDevice mInstance = null;
    GenericBleDevice mDevice;

    private ConnectedDevice() { }

    public static ConnectedDevice getInstance() {
        if (mInstance == null) {
            synchronized (ConnectedDevice.class) {
                if (mInstance == null) {
                    mInstance = new ConnectedDevice();
                }
            }
        }

        return mInstance;
    }

    public void setDevice(GenericBleDevice device) { mDevice = device; }
    public GenericBleDevice getDevice() { return mDevice; }
}
