package com.cs246.senselab;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.cs246.ble.BleDeviceManager;
import com.cs246.ble.BleScanner;
import com.cs246.ble.BleService;
import com.cs246.ble.ConnectedDevice;
import com.cs246.ble.GenericBleDevice;
import com.cs246.ble.sensortag.SensortagScanner;

import java.util.ArrayList;
import java.util.List;

public class ScanActivity extends BaseActivity {

    private final Activity ACTIVITY = this;
    private static final String[] FILTERS = { "CC2650", "SensorTag" };
    private BluetoothAdapter mBluetoothAdapter;
    private BleScanner mBleScanner;
    private final static int REQUEST_ENABLE_BT = 1;
    private List<GenericBleDevice> mDevices;
    private ListView mDeviceList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        mDeviceList = (ListView) findViewById(R.id.deviceList);

        final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        mBleScanner = new SensortagScanner(mBluetoothAdapter, FILTERS,
                new BleScanner.ScannerCallback() {
                    @Override
                    public void onDeviceFound(GenericBleDevice device) {
                        updateDeviceList(device);
                    }
                });

        if (ConnectedDevice.getInstance().getDevice() != null) {
            redirectChooseSensorActivity();
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, DisplaySectionActivity.class);
        intent.putExtra(EXTRA_FOLDERNAME, folderName);
        intent.putExtra(EXTRA_FOLDERID, folderId);
        intent.putExtra(EXTRA_DISPLAYDATA, displayData);
        startActivity(intent);
    }

    private void updateDeviceList(GenericBleDevice device) {
        if (mDevices == null) {
            mDevices = new ArrayList<>();
        }

        mDevices.add(device);

        BleDeviceManager manager = new BleDeviceManager(mDevices);
        mDeviceList.setAdapter(manager.getArrayAdapter(ACTIVITY));
        setDeviceListClickListener();
    }

    private void setDeviceListClickListener() {
        mDeviceList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final GenericBleDevice device = mDevices.get(position);
                ProgressDialog prog = new ProgressDialog(ACTIVITY);
                prog.setIndeterminate(true);
                prog.setCancelable(false);
                prog.setMessage((String) "Connecting to " + device.getName() + "...");
                prog.show();

                device.connect(ACTIVITY, new GenericBleDevice.DeviceCallback() {
                    @Override
                    public void onConnect() {
                        mBleScanner.stop();
                        ConnectedDevice.getInstance().setDevice(device);

                        for (BleService service : device.getServices()) {
                            System.out.println("SCAN ACTIVITY: " + service.getName());
                        }

                        redirectChooseSensorActivity();
                    }
                });
            }
        });
    }

    private void redirectChooseSensorActivity() {
        Intent intent = new Intent(this, ChooseSensorActivity.class);
        intent.putExtra(EXTRA_FOLDERNAME, folderName);
        intent.putExtra(EXTRA_FOLDERID, folderId);
        intent.putExtra(EXTRA_DISPLAYDATA, displayData);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Check if device supports bluetooth LE
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, "BLE Not Supported",
                    Toast.LENGTH_SHORT).show();
            finish();
        }

        // Make sure the users bluetooth is turned on
        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mBluetoothAdapter != null && mBluetoothAdapter.isEnabled()) {
            mBleScanner.stop();
        }
    }

    public void scanClickListener(View view) {
        Button scanButton = (Button) findViewById(R.id.scan_button);

        if (!mBleScanner.isScanning()) {
            scanButton.setText("STOP SCANNING");
            mBleScanner.start();
        } else {
            scanButton.setText("SCAN FOR DEVICE");
            mBleScanner.stop();
        }
    }
}
