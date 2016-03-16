package com.cs246.senselab;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.cs246.senselab.storage.StorageProvider;
import com.cs246.senselab.storage.Folder;

/**
 * Activity that handles the creation of different parts of the lab reports
 */
public class CreationWizardActivity extends BaseActivity {
    private Button finishCreation = null;
    private EditText nameField = null;
    private static final String TAG = CreationWizardActivity.class.getName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_creation_wizard);

        Intent intent = getIntent();

        provider.connect(new StorageProvider.ConnectCallback() {
            @Override
            public void onConnect(StorageProvider provider) {

                provider.setFolder(folderName, folderId);

                nameField = (EditText) findViewById(R.id.name_of_field);
                finishCreation = (Button) findViewById(R.id.finish_creation_button);

                finishCreation.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        createDataTypeClickHandler(v);
                    }
                });

                setDisplay();
            }
        });

    }

    /**
     * Changes button and hint to represent the part of the lab report being created
     */
    private void setDisplay() {
        nameField.setHint("Enter the name of the " + displayData);
        nameField.setVisibility(View.VISIBLE);
        finishCreation.setText("Add " + displayData);
        finishCreation.setVisibility(View.VISIBLE);
    }

    /**
     * Retrieves name of datatype to be created, and creates it
     *
     * @param v Create DataType button
     */
    private void createDataTypeClickHandler(View v) {
        String name = nameField.getText().toString();
        final Intent intent = new Intent(this, DisplayFolderActivity.class);
        intent.putExtra(EXTRA_FOLDERID, folderId);
        intent.putExtra(EXTRA_FOLDERNAME, folderName);
        intent.putExtra(EXTRA_DISPLAYDATA, displayData);
        System.out.print(folderId);

        provider.getFolder().createSubFolderAsync(name, new Folder.CreateFileCallback() {
            @Override
            public void onCreate() {
                startActivity(intent);
                Log.d(TAG, "The data folder was created.");
            }
        });
    }
}
