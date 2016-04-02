package com.cs246.ble;

import com.cs246.ble.sensortag.ResultCallback;

/**
 * Created by Quade on 3/19/16.
 */
public interface BleService {
    String getName();
    void read(ResultCallback callback);
    void enable(boolean enable, ResultCallback callback);
    boolean isEnabled();
    void setNotification(boolean notify, ResultCallback callback);
    boolean isNotifyEnabled();
    String extractData(byte [] data);
}
