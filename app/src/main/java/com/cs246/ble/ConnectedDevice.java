package com.cs246.ble;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Quade on 3/31/16.
 */
public class ConnectedDevice {
    private static ConnectedDevice mInstance = null;
    private static GenericBleDevice mDevice;
    private static List<String> mData;

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

    public void addData(String data) {
        if (mData == null) {
            mData = new ArrayList<>();
        }

        mData.add(0, data);
    }

    public List<String> getData() { return mData; }

    public void clearData() { if (mData != null) { mData.clear(); } }
}
