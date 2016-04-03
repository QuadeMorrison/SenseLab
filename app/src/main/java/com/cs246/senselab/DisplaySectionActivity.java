package com.cs246.senselab;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.cs246.senselab.model.TextField;
import com.cs246.senselab.model.TextTable;
import com.cs246.senselab.storage.Children;
import com.cs246.senselab.storage.Folder;
import com.cs246.senselab.storage.StorageProvider;

import java.util.ArrayList;
import java.util.List;

/**
 * Activity that represents section data of a labreport (ie. Hypothesis, Problem, etc)
 */
public class DisplaySectionActivity extends BaseActivity {
    private Button addTextField = null;
    private TableLayout tableLayout = null;
    private List<TextField> textFields = null;
    private static final String TAG = DisplaySectionActivity.class.getName();
    private String sensorData;
    private String fileToPutSensorDataId;
    private final Activity ACTIVITY = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_section);

        provider.connect(new StorageProvider.ConnectCallback() {
            @Override
            public void onConnect(StorageProvider provider) {
                setParentFolder();
                tableLayout = (TableLayout) findViewById(R.id.section_data_tablelayout);
                addTextField = (Button) findViewById(R.id.add_text_field);
                addTextFieldListener();
                getSectionData();
                checkForSensorData();
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, DisplayFolderActivity.class);
        intent.putExtra(EXTRA_FOLDERNAME, parentName);
        intent.putExtra(EXTRA_FOLDERID, parentId);
        intent.putExtra(EXTRA_DISPLAYDATA, parentDisplayData);
        startActivity(intent);
    }

    private void checkForSensorData() {
        Intent intent = getIntent();
        if (intent.hasExtra(EXTRA_DATA)) {
            sensorData = intent.getStringExtra(EXTRA_DATA);
            fileToPutSensorDataId = intent.getStringExtra(EXTRA_DATA_FILE_ID);

            if (fileToPutSensorDataId == null) {
                provider.getFolder().createFileAsync("textField", new Folder.CreateFileCallback() {
                    @Override
                    public void onCreate(String id) {
                        Log.d(TAG, "File created");
                        createTextField(id, sensorData);
                    }
                });
            }
        }
    }

    /**
     * Gets the data fields of this section and loads their contents from the StorageProvider
     */
    private void getSectionData() {
        provider.getFolder().listChildrenAsync(new Folder.ListChildrenCallback() {
            @Override
            public void onChildrenListed(Children children) {
                if (textFields == null) {
                    List<String> ids = children.getIds();
                    List<String> names = children.getNames();

                    for (String name : names) {
                        System.out.println(name);
                    }

                    if (ids != null) {

                        for (int i = 0; i < ids.size(); ++i) {
                            if (names.get(i).equals("textField")) {
                                createTextField(ids.get(i));
                            } else {
                                createTextTable(ids.get(i), names.get(i));
                            }
                        }
                    }
                }
            }
        });
    }

    /**
     * Creates a new data field.
     *
     * @param id The id of the field that represents it on the Storage Provider if it exists
     */
    private void createTextField(String id) {
        if (textFields == null) {
            textFields = new ArrayList<>();
        }

        TextField textField = new TextField(this, provider, "Click to edit", id);

        if (id.equals(fileToPutSensorDataId)) {
            textField.readFileContentsAsync(sensorData);
        } else {
            textField.readFileContentsAsync(null);
        }
        tableLayout.addView(textField.getViewSwitcher());
        setEditTextFieldListener(textField);
        setLayoutClickListener();

        textFields.add(textField);
    }

    private void createTextTable(final String id, String name) {
        final TextTable textTable = new TextTable(this, id, name, provider);
        textTable.readFileContentsAsync(new TextTable.TextTableCallback() {
            @Override
            public void onResult() {
                TextView textView = new TextView(ACTIVITY);

                if (id.equals(fileToPutSensorDataId)) {
                    textTable.addData(sensorData);
                }

                textView.setText(textTable.makeTable());
                textView.setTypeface(Typeface.MONOSPACE);
                tableLayout.addView(textView);
            }
        });
    }

    private void createTextField(String id, String content) {
        if (textFields == null) {
            textFields = new ArrayList<>();
        }

        TextField textField = new TextField(this, provider, "Click to edit", id);
        textField.setText(content);
        tableLayout.addView(textField.getViewSwitcher());
        setEditTextFieldListener(textField);
        setLayoutClickListener();

        textFields.add(textField);
    }

    /**
     * Add a click listener to the "Add Text Field" button, that creates a new text field on the gui
     * and on the Storage Provider
     */
    private void addTextFieldListener() {
        addTextField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                provider.getFolder().createFileAsync("textField", new Folder.CreateFileCallback() {
                    @Override
                    public void onCreate(String id) {
                        Log.d(TAG, "File created");
                        createTextField(id);
                    }
                });
            }
        });
    }

    /**
     * Sets click listener so that when the layout is clicked, any text field that is currently being
     * edited, is saved on the drive.
     */
    private void setLayoutClickListener() {
        LinearLayout layout = (LinearLayout) findViewById(R.id.section_data_layout);
        layout.setOnClickListener(finishEditingClickListener);
        tableLayout.setOnClickListener(finishEditingClickListener);
    }

    private View.OnClickListener finishEditingClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            finishEditing();
        }
    };

    /**
     * Changes the clicked text field into it's editable version
     *
     * @param textField The field to edit
     */
    private void setEditTextFieldListener(final TextField textField) {
        textField.getTextView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishEditing();
                textField.edit();
            }
        });
    }

    /**
     * Loops through textFields and if they are being edited, save the changes and revert to
     * non-editable format
     */
    private void finishEditing() {
        for (TextField t : textFields) {
            if (t.isBeingEdited()) {
                t.finishEditing();
            }
        }
    }

    public void collectDataHandler(View view) {
        Intent intent = new Intent(this, ScanActivity.class);
        intent.putExtra(EXTRA_FOLDERNAME, folderName);
        intent.putExtra(EXTRA_FOLDERID, folderId);
        intent.putExtra(EXTRA_DISPLAYDATA, displayData);
        startActivity(intent);
    }


}
