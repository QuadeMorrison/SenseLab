package com.cs246.senselab.storage.googledrive;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cs246.senselab.storage.Children;
import com.cs246.senselab.storage.ChildrenAdapter;
import com.google.android.gms.drive.Metadata;
import com.google.android.gms.drive.widget.DataBufferAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapts DriveChildren into a usable ListView
 */
public class DriveChildrenAdapter implements ChildrenAdapter {
    private DataBufferAdapter adapter = null;
    private List<String> folderIdList = null;
    private List<String> folderNameList = null;

    public DriveChildrenAdapter(Context aContext) {
        adapter = new ResultsAdapter(aContext);
    }

    /**
     * Converts children into Metadata so that it can be used by the DataBufferAdapter
     *
     * @param aChildren Object to be adapted
     */
    @Override
    public void append(Children aChildren) {
        DriveChildren mChildren = (DriveChildren) aChildren;
        adapter.append(mChildren.getChildren().getMetadataBuffer());
    }

    @Override
    public List<String> getIdList() { return folderIdList; }

    @Override
    public List<String> getNameList() { return folderNameList; }

    @Override
    public DataBufferAdapter getDataAdapter() { return adapter; }

    /**
     * Ensures that there is no data leaks from the Data Adapter
     */
    @Override
    public void clear() {
        if (adapter != null) {
            adapter.clear();
        }
    }

    /**
     * The DataBuffer Adapter used to adapt the children into a ListView
     */
    private class ResultsAdapter extends DataBufferAdapter<Metadata> {

        public ResultsAdapter(Context context) {
            super(context, android.R.layout.simple_list_item_1);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = View.inflate(getContext(),
                        android.R.layout.simple_list_item_1, null);
            }

            Metadata metadata = getItem(position);
            addData(metadata);
            setTitleText(convertView, metadata);

            return convertView;
        }

        /**
         * Adds children's Id and Name to their respective lists
         *
         * @param metadata
         */
        private void addData(Metadata metadata) {
            if (folderIdList == null) {
                folderIdList = new ArrayList<>();
                folderNameList = new ArrayList<>();
            }
            folderIdList.add(metadata.getDriveId().encodeToString());
            folderNameList.add(metadata.getTitle());
        }

        private void setTitleText(View convertView, Metadata metadata) {
            TextView titleTextView =
                    (TextView) convertView.findViewById(android.R.id.text1);
            titleTextView.setText(metadata.getTitle());
        }
    }
}
