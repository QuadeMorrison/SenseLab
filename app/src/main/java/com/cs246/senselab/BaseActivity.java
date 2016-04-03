package com.cs246.senselab;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.cs246.senselab.storage.Folder;
import com.cs246.senselab.storage.StorageProvider;
import com.cs246.senselab.storage.googledrive.GoogleDriveStorageProvider;

/**
 * Created by Quade on 3/3/16.
 */
public class BaseActivity extends AppCompatActivity {
    protected StorageProvider provider = null;
    protected String folderName = null;
    protected String folderId = null;
    protected String displayData = null;
    protected String parentName = null;
    protected String parentId = null;
    protected String parentDisplayData = null;

    public final static String EXTRA_FOLDERNAME = "com.cs246.senselab.FOLDERNAME";
    public final static String EXTRA_FOLDERID = "com.cs246.senselab.FOLDERID";
    public final static String EXTRA_DISPLAYDATA = "com.cs246.senselab.DISPLAYDATA";
    public final static String EXTRA_DATA = "com.cs246.senselab.DATA";
    public final static String EXTRA_DATA_FILE_ID = "com.cs246.senselab.DATA_FILE_ID";
    public final static String EXTRA_SERVICENAME = "com.cs246.senselab.SERVICENAME";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        provider = new GoogleDriveStorageProvider(this);
        Intent intent = getIntent();
        initializeFolder(intent);
        initializeDisplayData(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (provider != null) {
            provider.onResume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (provider != null) {
            provider.onPause();
        }
    }

    private void initializeFolder(Intent intent) {
        if (intent.hasExtra(EXTRA_FOLDERID) && intent.hasExtra(EXTRA_FOLDERID)) {
            folderName = intent.getStringExtra(EXTRA_FOLDERNAME);
            folderId = intent.getStringExtra(EXTRA_FOLDERID);
        } else {
            folderName = "SenseLab";
        }

        provider.setFolder(folderName, folderId);
    }

    private void initializeDisplayData(Intent intent) {
        if (intent.hasExtra(EXTRA_DISPLAYDATA)) {
            displayData = intent.getStringExtra(EXTRA_DISPLAYDATA);
        } else {
            displayData = "Lab Report";
        }
    }

    protected void setParentFolder() {
        provider.getFolder().listParentsAsync(new Folder.ListParentsCallback() {
            @Override
            public void onParentsListed(String parent, String id) {
                parentName = parent;
                parentId = id;

                if (displayData.equals("Section")) {
                    parentDisplayData = "Lab Report";
                } else if (displayData.equals("Section Data")) {
                    parentDisplayData = "Section";
                }
            }
        });
    }
}
