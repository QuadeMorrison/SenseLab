package com.cs246.ble;

import android.content.Context;
import android.widget.ArrayAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Quade on 3/19/16.
 */
public class BleDeviceManager {
    List<String> mNames;

    public BleDeviceManager(List<GenericBleDevice> devices) {
        mNames = getNames(devices);
    }

    public List<String> getNames(List<GenericBleDevice> devices) {
        if (mNames == null) {
            mNames = new ArrayList<>();

            for (GenericBleDevice d : devices) {
                mNames.add(d.getName());
            }
        }

        return mNames;
    }

    public ArrayAdapter getArrayAdapter(Context context) {
        return new ArrayAdapter(context, android.R.layout.simple_list_item_1, mNames);
    }
}
