package com.cs246.senselab;

import android.content.Context;
import android.os.Bundle;
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

public class DisplaySectionActivity extends BaseActivity {
    Button addTextField = null;
    LinearLayout layout = null;
    Context mContext = this;
    List<TextField> textFields = null;

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

    private void addTextFieldListener() {
        addTextField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                provider.getFolder().createFileAsync("textField", new Folder.CreateFileCallback() {
                    @Override
                    public void onCreate() {

                        createTextField(null);
                    }
                });
            }
        });
    }

    private void setLayoutClickListener() {
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishEditing();
            }
        });
    }

    private void setEditTextFieldListener(final TextField textField) {
        textField.getTextView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishEditing();
                textField.edit();
            }
        });
    }

    private void finishEditing() {
        for (TextField t : textFields) {
            if (t.isBeingEdited) {
                t.finishEditing();
            }
        }
    }

    private class TextField {
        private String mContent = null;
        private String mDefaultContent = null;
        private EditText mEditText = null;
        private TextView mTextView = null;
        private ViewSwitcher mViewSwitcher = null;
        private String mId = null;
        private boolean isBeingEdited = false;

        private TextField() { }

        public TextField(String content, String id) {
            mDefaultContent = content;
            mContent = content;
            mId = id;
            initializeEditText();
            initializeTextView();
            initializeViewSwitcher();
        }

        private void initializeTextView() {
            mTextView = new TextView(mContext);
            setDimensions(mTextView);
            mTextView.setClickable(true);
            mTextView.setText(mContent);
        }

        private void initializeEditText() {
            mEditText = new EditText(mContext);
            setDimensions(mEditText);
        }

        private void initializeViewSwitcher() {
            mViewSwitcher = new ViewSwitcher(mContext);
            setDimensions(mViewSwitcher);
            mViewSwitcher.addView(mTextView);
            mViewSwitcher.addView(mEditText);
        }

        private void setDimensions(View v) {
            v.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            ));
        }

        public void edit() {
            mViewSwitcher.showNext();
            mEditText.requestFocusFromTouch();
            InputMethodManager lManager = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
            lManager.showSoftInput(mEditText, 0);
            isBeingEdited = true;
        }

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
                    }
                });
            }
        }

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

        public boolean isBeingEdited() { return isBeingEdited; }

        public TextView getTextView() { return mTextView; }

        public ViewSwitcher getViewSwitcher() { return mViewSwitcher; }
    }
}
