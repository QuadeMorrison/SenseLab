package com.cs246.senselab.model;

import android.content.Context;

import com.cs246.ble.ConnectedDevice;
import com.cs246.senselab.storage.Children;
import com.cs246.senselab.storage.Folder;
import com.cs246.senselab.storage.StorageProvider;

import java.util.List;

/**
 * Created by Quade on 4/2/16.
 */
public class LabReport {
    private StorageProvider mProvider;
    private List<String> mSectionNames;
    private List<String> mSectionIds;
    private List<String> mDataNames;
    private List<String> mDataIds;
    private String mLabReport;
    private Context mContext;

    public interface LoadCallback {
        void onLoad(String labReport);
    }

    public LabReport(String title, Context context, StorageProvider provider) {
        mProvider = provider;
        mContext = context;
        mLabReport = title + "\n";
    }

    public void get(String id, final LoadCallback callback) {
        mProvider.getFolder().listChildrenAsync(id, new Folder.ListChildrenCallback() {
            @Override
            public void onChildrenListed(Children children) {
                mSectionIds = children.getIds();
                mSectionNames = children.getNames();
                getSectionContents(0, callback);
            }
        });
    }

    private void getSectionContents(final int sCount, final LoadCallback callback) {
        if (sCount < mSectionIds.size()) {
            mLabReport += "\n" + mSectionNames.get(sCount);

            mProvider.getFolder().listChildrenAsync(mSectionIds.get(sCount), new Folder.ListChildrenCallback() {
                @Override
                public void onChildrenListed(Children children) {
                    mDataIds = children.getIds();
                    mDataNames = children.getNames();
                    getDataContents(0, sCount, callback);
                }
            });
        } else {
            callback.onLoad(mLabReport);
        }
    }

    private void getDataContents(final int dCount, final int sCount, final LoadCallback callback) {
        if (dCount < mDataIds.size()) {
            mProvider.readFileAsync(mDataIds.get(dCount), new StorageProvider.FileAccessCallback() {
                @Override
                public void onResult(String contents) {
                    String data = contents;
                    if (!mDataNames.get(dCount).equals("textField")) {
                        TextTable textTable = new TextTable(mContext, mDataIds.get(dCount), mDataNames.get(dCount), mProvider);
                        textTable.setContentsViaString(contents);
                        data = textTable.makeTable();
                    }

                    mLabReport += "\n " + data;

                    getDataContents(dCount + 1, sCount, callback);
                }
            });
        } else {
            getSectionContents(sCount + 1, callback);
        }
    }
}

