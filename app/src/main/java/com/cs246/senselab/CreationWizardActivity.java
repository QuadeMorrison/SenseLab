package com.cs246.senselab;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.cs246.senselab.storage.StorageProvider;
import com.cs246.senselab.storage.Folder;

public class CreationWizardActivity extends BaseActivity {
    private Button finishCreation = null;
    private EditText nameField = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_creation_wizard);

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(android.R.id.content, new TitleCreationWizardFragment());
        fragmentTransaction.commit();

        Intent intent = getIntent();
        finishCreation = (Button) findViewById(R.id.finish_creation_button);
        nameField = (EditText) findViewById(R.id.name_of_field);

        provider.connect(new StorageProvider.ConnectCallback() {
            @Override
            public void onConnect(StorageProvider provider) {
                provider.setFolder(folderName, folderId);

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

    private void setDisplay() {
        nameField.setHint("Enter the name of the " + displayData);
        nameField.setVisibility(View.VISIBLE);
        finishCreation.setText("Add " + displayData);
        finishCreation.setVisibility(View.VISIBLE);
    }

    private void createDataTypeClickHandler(View v) {
        String name = nameField.getText().toString();
        final Intent intent = new Intent(this, DisplayFolderActivity.class);
        intent.putExtra(EXTRA_FOLDERID, folderId);
        intent.putExtra(EXTRA_FOLDERNAME, folderName);
        intent.putExtra(EXTRA_DISPLAYDATA, displayData);
        System.out.print(folderId);
        System.out.print("Outside\n");

        provider.getFolder().createSubFolderAsync(name, new Folder.CreateFolderCallback() {
            @Override
            public void onCreate() {
                System.out.print("Inside\n");
                startActivity(intent);
            }
        });
    }
}
