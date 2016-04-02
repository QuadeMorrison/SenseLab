package com.cs246.ble.sensortag;

import com.cs246.ble.BleService;
import com.cs246.ble.GuiCallback;

/**
 * Created by Quade on 3/28/16.
 */
public class Result {
    BleService mService;
    String mData;

    public Result(BleService service, String data) {
        mService = service;
        mData = data;
    }

    public BleService getService() {
        return mService;
    }

    public String getData() { return mData; }

    public void updateGui(GuiCallback callback) {
        BleConnection.getInstance().updateGui(callback);
    }
}
