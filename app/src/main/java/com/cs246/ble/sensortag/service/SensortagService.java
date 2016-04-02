package com.cs246.ble.sensortag.service;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.util.Log;

import com.cs246.ble.BleService;
import com.cs246.ble.sensortag.BleConnection;
import com.cs246.ble.sensortag.ResultCallback;

import java.util.UUID;
import static java.util.UUID.fromString;

/**
 * Created by Quade on 3/19/16.
 */
public abstract class SensortagService implements BleService {

    protected static final UUID CHAR_CONFIG_UUID = fromString("00002902-0000-1000-8000-00805f9b34fb");
    protected static final String TAG = SensortagService.class.getName();

    protected String mName;
    protected UUID mServiceUUID;
    protected UUID mDataUUID;
    protected UUID mConfigUUID;
    protected boolean mIsEnabled;
    protected boolean mIsNotify;

    SensortagService() { }

    @Override
    public String getName() {
        return mName;
    }

    @Override
    public boolean isNotifyEnabled() {
        return mIsNotify;
    }

    @Override
    public boolean isEnabled() {
        return mIsEnabled;
    }

    @Override
    public void enable(boolean enable, ResultCallback callback) {
        BluetoothGattCharacteristic c = BleConnection.getInstance().getGatt().getService(mServiceUUID)
                .getCharacteristic(mConfigUUID);

        if (enable) {
            c.setValue(new byte[]{0x01});
            mIsEnabled = true;
        } else {
            c.setValue(new byte[] {0x00});
            mIsEnabled = false;
        }

        Log.d(TAG, "enable -- service_name: " + mName + ", char_value: " + c.getValue());
        BleConnection.getInstance().setEnableCallback(callback, this);
        BleConnection.getInstance().getGatt().writeCharacteristic(c);
    }

    @Override
    public void read(ResultCallback callback) {
        if (mIsEnabled) {
            BluetoothGattCharacteristic c = BleConnection.getInstance().getGatt().getService(mServiceUUID)
                    .getCharacteristic(mDataUUID);

            BleConnection.getInstance().setReadCallback(callback, this);
            BleConnection.getInstance().getGatt().readCharacteristic(c);
        }
    }

    @Override
    public void setNotification(boolean notify, ResultCallback callback) {
        if (mIsEnabled) {
            BluetoothGattCharacteristic c = BleConnection.getInstance().getGatt().getService(mServiceUUID)
                    .getCharacteristic(mDataUUID);
            BluetoothGattDescriptor desc = c.getDescriptor(CHAR_CONFIG_UUID);

            if (!mIsNotify) {
                BleConnection.getInstance().getGatt().setCharacteristicNotification(c, true);
                desc.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                mIsNotify = true;
            } else {
                BleConnection.getInstance().getGatt().setCharacteristicNotification(c, false);
                desc.setValue(BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE);
                mIsNotify = false;
            }

            BleConnection.getInstance().setNotifyCallback(callback, this);
            BleConnection.getInstance().getGatt().writeDescriptor(desc);
        }
    }

    @Override
    public boolean equals(Object o) {
        return BleConnection.getInstance().getGatt().getService(mServiceUUID).equals(o);
    }
}
