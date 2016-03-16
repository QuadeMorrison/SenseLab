package com.cs246.senselab;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ViewSwitcher;

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
    private LinearLayout layout = null;
    private Context mContext = this;
    private List<TextField> textFields = null;
    private static final String TAG = DisplaySectionActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_section);

        provider.connect(new StorageProvider.ConnectCallback() {
            @Override
            public void onConnect(StorageProvider provider) {
                layout = (LinearLayout) findViewById(R.id.section_data_layout);
                addTextField = (Button) findViewById(R.id.add_text_field);
                addTextFieldListener();
                getSectionData();
            }
        });
    }

    /**
     * Gets the data fields of this section and loads their contents from the StorageProvider
     */
    private void getSectionData() {
        provider.getFolder().listChildrenAsync(new Folder.ListChildrenCallback() {
            @Override
            public void onChildrenListed(Children children) {
                List<String> ids = children.getIds();

                if (ids != null) {

                    for (String id : ids) {
                        createTextField(id);
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

        TextField textField = new TextField("Click to edit", id);
        textField.readFileContentsAsync();
        layout.addView(textField.getViewSwitcher());
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
                    public void onCreate() {
                        Log.d(TAG, "File created");
                        createTextField(null);
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
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishEditing();
            }
        });
    }

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

    /**
     * An editable text field. The data to be stored on the StorageProvider
     */
    private class TextField {
        private String mContent = null;
        private String mDefaultContent = null;
        private EditText mEditText = null;
        private TextView mTextView = null;
        private ViewSwitcher mViewSwitcher = null;
        private String mId = null;
        private boolean isBeingEdited = false;

        /**
         * Don't let the field be initialized without content
         */
        private TextField() { }

        /**
         * Constructor
         *
         * @param content The content to be displayed when the text field is empty
         * @param id Id that represents TextField on the StorageProvider
         */
        public TextField(String content, String id) {
            mDefaultContent = content;
            mContent = content;
            mId = id;
            initializeEditText();
            initializeTextView();
            initializeViewSwitcher();
        }

        /**
         * Initialize the non-editable textview for the gui
         */
        private void initializeTextView() {
            mTextView = new TextView(mContext);
            setDimensions(mTextView);
            mTextView.setClickable(true);
            mTextView.setText(mContent);
        }

        /**
         * Initialize the editable view for the gui
         */
        private void initializeEditText() {
            mEditText = new EditText(mContext);
            setDimensions(mEditText);
        }

        /**
         * Initialize the view switcher to switch between the textview and the the editable view
         */
        private void initializeViewSwitcher() {
            mViewSwitcher = new ViewSwitcher(mContext);
            setDimensions(mViewSwitcher);
            mViewSwitcher.addView(mTextView);
            mViewSwitcher.addView(mEditText);
        }

        /**
         * Set views dimensions on the gui
         *
         * @param v View who's dimensions are to be set
         */
        private void setDimensions(View v) {
            v.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            ));
        }

        /**
         * Switches the non-editable view, to the editable view and pulls up android keyboard
         */
        public void edit() {
            mViewSwitcher.showNext();
            mEditText.requestFocusFromTouch();
            InputMethodManager lManager = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
            lManager.showSoftInput(mEditText, 0);
            isBeingEdited = true;
        }

        /**
         * Gets the file contents from file stored on Storage Provider
         */
        public void readFileContentsAsync() {
            if (mId != null) {
                provider.readFileAsync(mId, new StorageProvider.FileAccessCallback() {
                    @Override
                    public void onResult(String contents) {
                        mContent = contents;
                        if (!mContent.equals("")) {
                            mTextView.setText(mContent);
                        } else {
                            mTextView.setText(mDefaultContent);
                        }
                        Log.d(TAG, "File successfully read");
                    }
                });
            }
        }

        /**
         * Save textfields contents to file stored on the StorageProvider and switched to non-editable
         * view
         */
        public void finishEditing() {
            String newContent = mEditText.getText().toString();
            if (!newContent.equals("")) {
                mContent = mEditText.getText().toString();
            } else {
                mContent = mDefaultContent;
            }

            mTextView.setText(mContent);
            provider.writeToFileAsync(mId, mContent, new StorageProvider.FileAccessCallback() {
                @Override
                public void onResult(String contents) {
                    isBeingEdited = false;
                    mViewSwitcher.showNext();
                }
            });
        }

        public String getId() { return mId; }

        /**
         * Returns whether or not the textfield is currently being edited
         *
         * @return True if being edited, false otherwise
         */
        public boolean isBeingEdited() { return isBeingEdited; }

        public TextView getTextView() { return mTextView; }

        public ViewSwitcher getViewSwitcher() { return mViewSwitcher; }
    }
}
