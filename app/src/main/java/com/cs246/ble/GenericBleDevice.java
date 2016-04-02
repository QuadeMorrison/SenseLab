package com.cs246.ble;

import android.content.Context;

import java.util.List;

/**
 * Created by Quade on 3/18/16.
 */
public interface GenericBleDevice {
    interface DeviceCallback {
        void onConnect();
    }

    String getName();
    int getRssi();
    List<BleService> getServices();
    void connect(Context context, DeviceCallback callback);
}
