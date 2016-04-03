package com.cs246.senselab;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cs246.senselab.model.TextTable;
import com.cs246.senselab.storage.Children;
import com.cs246.senselab.storage.Folder;
import com.cs246.senselab.storage.StorageProvider;

import java.util.ArrayList;
import java.util.List;

public class AddDataActivity extends BaseActivity {
    private final Activity ACTVITIY = this;
    private Button addTextField;
    private LinearLayout layout;
    private String data;
    private String mServiceName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_data);

        Intent intent = getIntent();

        if (intent.hasExtra(EXTRA_SERVICENAME)) {
            mServiceName = intent.getStringExtra(EXTRA_SERVICENAME);
        }

        provider.connect(new StorageProvider.ConnectCallback() {
            @Override
            public void onConnect(StorageProvider provider) {
                setParentFolder();
                layout = (LinearLayout) findViewById(R.id.add_data_layout);
                addTextField = (Button) findViewById(R.id.add_text_field);
                addTextField.setVisibility(View.VISIBLE);
                initializeData();
                getSectionData();
            }
        });
    }

    private void initializeData() {
        Intent intent = getIntent();
        if (intent.hasExtra(EXTRA_DATA)) {
            data = intent.getStringExtra(EXTRA_DATA);
        };
    }

    /**
     * Gets the data fields of this section and loads their contents from the StorageProvider
     */
    private void getSectionData() {
        provider.getFolder().listChildrenAsync(new Folder.ListChildrenCallback() {
            @Override
            public void onChildrenListed(Children children) {
                final List<String> ids = children.getIds();
                final List<String> names = children.getNames();

                if (ids != null) {

                    for (int i = 0; i < ids.size(); ++i) {
                        final String id = ids.get(i);
                        final String name = names.get(i);

                        provider.readFileAsync(id, new StorageProvider.FileAccessCallback() {

                            @Override
                            public void onResult(String contents) {
                                TextView tv = new TextView(ACTVITIY);
                                if (name.equals("textField")) {
                                    tv.setText(contents);
                                } else {
                                    tv = checkForTable(id, name, contents, tv);
                                }
                                if (!tv.getText().equals("")) {
                                    tv.setHint(id);
                                    tv.setOnClickListener(textViewListener);
                                    layout.addView(tv);
                                }
                            }
                        });
                    }
                }
            }
        });
    }

    private TextView checkForTable(String id, String name, String contents, TextView textView) {

        if (name.equals(mServiceName)) {
            TextTable textTable = new TextTable(ACTVITIY, id, name, provider);
            textTable.setContentsViaString(contents);
            textView.setText(textTable.makeTable());
            textView.setTypeface(Typeface.MONOSPACE);
        }

        return textView;
    }

    private View.OnClickListener textViewListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            TextView tv = (TextView) v;
            String textViewId = tv.getHint().toString();
            sendDataToSection(data, textViewId);
        }
    };

    private void sendDataToSection(String data, String id) {
        System.out.println("ADD DATA ACTIVITY: " + data);
        Intent intent = new Intent(this, DisplaySectionActivity.class);
        intent.putExtra(EXTRA_DATA_FILE_ID, id);
        intent.putExtra(EXTRA_DATA, data);
        intent.putExtra(EXTRA_FOLDERNAME, folderName);
        intent.putExtra(EXTRA_FOLDERID, folderId);
        intent.putExtra(EXTRA_DISPLAYDATA, displayData);
        startActivity(intent);
    }

    public void addTextFieldListener(View view) {
        sendDataToSection(data, null);
    }
}
