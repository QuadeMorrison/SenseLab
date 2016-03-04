package com.cs246.senselab;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.cs246.senselab.storage.Children;
import com.cs246.senselab.storage.ChildrenAdapter;
import com.cs246.senselab.storage.StorageProvider;
import com.cs246.senselab.storage.googledrive.DriveChildrenAdapter;
import com.cs246.senselab.storage.Folder;

public class DisplayFolderActivity extends BaseActivity {
    private Context context = this;
    private ChildrenAdapter mChildrenAdapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_folder);

        provider.connect(new StorageProvider.ConnectCallback() {
            @Override
            public void onConnect(StorageProvider provider) {
                initializeAddDataTypeButton();
                listFolderContents();
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (mChildrenAdapter != null)
            mChildrenAdapter.clear();
    }

    private void initializeAddDataTypeButton() {
        Button addDisplayData = (Button) findViewById(R.id.add_new_button);
        addDisplayData.setText("ADD NEW " + displayData);
        addDisplayData.setVisibility(View.VISIBLE);
        addDisplayData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toCreationWizardClickListener(v);
            }
        });
    }

    private void listFolderContents() {
        provider.getFolder().listChildrenAsync(new Folder.ListChildrenCallback() {
            @Override
            public void onChildrenListed(Children children) {
                mChildrenAdapter = new DriveChildrenAdapter(context);
                ListView list = (ListView) findViewById(R.id.senselab_list);
                mChildrenAdapter.append(children);
                list.setAdapter(mChildrenAdapter.getDataAdapter());
                setListClickListener(list, mChildrenAdapter, children);
            }
        });
    }

    private void setListClickListener(final ListView list, final ChildrenAdapter adapter, final Children children) {
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String newFolderId = adapter.getIdList().get(position);
                String newFolderName = adapter.getNameList().get(position);
                String newDataType = "Section";

                Intent intent = new Intent(getBaseContext(), DisplayFolderActivity.class);
                intent.putExtra(EXTRA_FOLDERID, newFolderId);
                intent.putExtra(EXTRA_FOLDERNAME, newFolderName);
                intent.putExtra(EXTRA_DISPLAYDATA, newDataType);

                startActivity(intent);
            }
        });
    }

    public void toCreationWizardClickListener(View v) {
        Intent intent = new Intent(this, CreationWizardActivity.class);
        intent.putExtra(EXTRA_FOLDERNAME, folderName);
        intent.putExtra(EXTRA_FOLDERID, folderId);
        intent.putExtra(EXTRA_DISPLAYDATA, displayData);
        startActivity(intent);
    }
}
