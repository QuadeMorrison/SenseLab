package com.cs246.senselab;

import android.app.Activity;
import android.content.Intent;
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
import com.cs246.senselab.storage.Folder;

import com.cs246.senselab.model.TextTable;
import com.cs246.senselab.storage.StorageProvider;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CollectDataActivity extends BaseActivity {
    private GenericBleDevice mDevice;
    private BleService mService;
    private Button readButton;
    private Button notifyButton;
    private Button createTableButton;
    private List<String> mData;
    private ListView dataListView;
    private String mCurrentMeasurment;
    private String mServiceName;
    private final Activity ACTIVITY = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collect_data);

        dataListView = (ListView) findViewById(R.id.data_list);

        Intent intent = getIntent();

        if (intent.hasExtra(EXTRA_SERVICENAME)) {
            mServiceName = intent.getStringExtra(EXTRA_SERVICENAME);
        }

        mDevice = ConnectedDevice.getInstance().getDevice();
        enableService();
        provider.connect(new StorageProvider.ConnectCallback() {

            @Override
            public void onConnect(StorageProvider provider) {
                ConnectedDevice.getInstance().clearData();
                createTableButton = (Button) findViewById(R.id.create_table_button);
                createTableButton.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, ChooseSensorActivity.class);
        intent.putExtra(EXTRA_FOLDERNAME, folderName);
        intent.putExtra(EXTRA_FOLDERID, folderId);
        intent.putExtra(EXTRA_DISPLAYDATA, displayData);
        startActivity(intent);
    }

    private void enableService() {
        for (BleService service : mDevice.getServices()) {
            if (service.getName().equals(mServiceName)) {
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
        ConnectedDevice.getInstance().addData(mCurrentMeasurment);
        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, mData);
        dataListView.setAdapter(adapter);
        setDataClickListener();
    }

    private void setDataClickListener() {
        dataListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                toAddDataActivity(ConnectedDevice.getInstance().getData().get(position));
            }
        });
    }

    private void toAddDataActivity(String data) {
        System.out.println("COLLECT DATA: " + data);
        Intent intent = new Intent(this, AddDataActivity.class);
        intent.putExtra(EXTRA_DATA, data);
        intent.putExtra(EXTRA_SERVICENAME, mServiceName);
        intent.putExtra(EXTRA_FOLDERNAME, folderName);
        intent.putExtra(EXTRA_FOLDERID, folderId);
        intent.putExtra(EXTRA_DISPLAYDATA, displayData);
        startActivity(intent);
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

    public void createTableListener(View view) {
        if (ConnectedDevice.getInstance().getData() != null) {
            final String title = mServiceName;

            provider.getFolder().createFileAsync(title, new Folder.CreateFileCallback() {
                @Override
                public void onCreate(String id) {

                    for (String data : ConnectedDevice.getInstance().getData()) {
                        System.out.println(data);
                    }

                    TextTable textTable = new TextTable(ACTIVITY, id, title, provider);
                    textTable.setTableData(ConnectedDevice.getInstance().getData(), new TextTable.TextTableCallback() {
                        @Override
                        public void onResult() {
                            Intent intent = new Intent(ACTIVITY, DisplaySectionActivity.class);
                            intent.putExtra(EXTRA_FOLDERNAME, folderName);
                            intent.putExtra(EXTRA_FOLDERID, folderId);
                            intent.putExtra(EXTRA_DISPLAYDATA, displayData);
                            startActivity(intent);
                        }
                    });

                }
            });
        }
    }
}
