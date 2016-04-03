package com.cs246.senselab;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.cs246.ble.BleService;
import com.cs246.ble.ConnectedDevice;

import java.util.ArrayList;
import java.util.List;

public class ChooseSensorActivity extends BaseActivity {
    private List<String> mServiceNames;
    private ListView mServiceList;
    private final Activity ACTIVITY = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_sensor);
        mServiceList = (ListView) findViewById(R.id.service_list);
        getServiceNames();
        updateList();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, DisplaySectionActivity.class);
        intent.putExtra(EXTRA_FOLDERNAME, folderName);
        intent.putExtra(EXTRA_FOLDERID, folderId);
        intent.putExtra(EXTRA_DISPLAYDATA, displayData);
        startActivity(intent);
    }

    private void getServiceNames() {
        List<BleService> services = ConnectedDevice.getInstance().getDevice().getServices();

        if (mServiceNames == null) {
            mServiceNames = new ArrayList<>();
        }

        for (BleService service : services) {
            mServiceNames.add(service.getName());
        }
    }

    private void updateList() {
        ArrayAdapter<String> adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, mServiceNames);
        mServiceList.setAdapter(adapter);
        setServiceClickListener();
    }

    private void setServiceClickListener() {
        mServiceList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(ACTIVITY, CollectDataActivity.class);
                intent.putExtra(EXTRA_SERVICENAME, mServiceNames.get(position));
                intent.putExtra(EXTRA_FOLDERNAME, folderName);
                intent.putExtra(EXTRA_FOLDERID, folderId);
                intent.putExtra(EXTRA_DISPLAYDATA, displayData);
                startActivity(intent);
            }
        });
    }
}
