package com.cs246.senselab.model;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.cs246.senselab.storage.StorageProvider;

import java.security.Provider;

/**
 * An editable text field. The data to be stored on the StorageProvider
 */
public class TextField {
    private String mContent = null;
    private String mDefaultContent = null;
    private EditText mEditText = null;
    private TextView mTextView = null;
    private ViewSwitcher mViewSwitcher = null;
    private String mId = null;
    private boolean isBeingEdited = false;
    private Context mContext;
    private StorageProvider mProvider;

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
    public TextField(Context context, StorageProvider provider, String content, String id) {
        mContext = context;
        mProvider = provider;
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
            mProvider.readFileAsync(mId, new StorageProvider.FileAccessCallback() {
                @Override
                public void onResult(String contents) {
                    mContent = contents;
                    if (!mContent.equals("")) {
                        mTextView.setText(mContent);
                    } else {
                        mTextView.setText(mDefaultContent);
                    }
                    Log.d("readFileContentsAsync", "File successfully read");
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
        mProvider.writeToFileAsync(mId, mContent, new StorageProvider.FileAccessCallback() {
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
