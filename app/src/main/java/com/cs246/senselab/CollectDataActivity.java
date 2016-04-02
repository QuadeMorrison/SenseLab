package com.cs246.senselab;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.cs246.ble.BleService;
import com.cs246.ble.ConnectedDevice;
import com.cs246.ble.GenericBleDevice;
import com.cs246.ble.GuiCallback;
import com.cs246.ble.sensortag.Result;
import com.cs246.ble.sensortag.ResultCallback;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CollectDataActivity extends BaseActivity {
    GenericBleDevice mDevice;
    BleService mService;
    Button readButton;
    Button notifyButton;
    List<String> mData;
    ListView dataListView;
    String mCurrentMeasurment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collect_data);

        dataListView = (ListView) findViewById(R.id.data_list);

        mDevice = ConnectedDevice.getInstance().getDevice();
        enableService();
    }

    private void enableService() {
        for (BleService service : mDevice.getServices()) {
            if (service.getName().equals("Ambient Temperature")) {
                mService = service;
                service.enable(true, new ResultCallback() {
                    @Override
                    public void onResult(Result result) {
                        result.updateGui(new GuiCallback() {
                            @Override
                            public void update() {
                                initializeButtons();
                            }
                        });
                        mDevice.getName();
                    }
                });
            }
        }
    }

    private void initializeButtons() {
        readButton = (Button) findViewById(R.id.read_button);
        notifyButton = (Button) findViewById(R.id.notify_button);

        readButton.setVisibility(View.VISIBLE);
        notifyButton.setVisibility(View.VISIBLE);
    }

    private void updateDataList() {
        if (mData == null) {
            mData = new ArrayList<>();
        }

        String timeStamp = new SimpleDateFormat("HH:mm:ss").format(new Date());
        mData.add(0, mCurrentMeasurment + " --- " + timeStamp);
        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, mData);
        dataListView.setAdapter(adapter);
    }

    private void setDataClickListener() {
        dataListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });
    }

    public void readDataListener(View view) {
        mService.read(new ResultCallback() {
            @Override
            public void onResult(Result result) {
                mCurrentMeasurment = result.getData();
                result.updateGui(mGuiCallback);
            }
        });
    }

    public void setNotifyListener(View view) {
        if (mService.isNotifyEnabled()) {
            notifyButton.setText("NOTIFY");
            mService.setNotification(true, new ResultCallback() {
                @Override
                public void onResult(Result result) {
                }
            });
        } else {
            notifyButton.setText("STOP NOTIFY");
            mService.setNotification(false, new ResultCallback() {
                @Override
                public void onResult(Result result) {
                    mCurrentMeasurment = result.getData();
                    result.updateGui(mGuiCallback);
                }
            });
        }
    }

    GuiCallback mGuiCallback = new GuiCallback() {
        @Override
        public void update() {
            updateDataList();
        }
    };
}
