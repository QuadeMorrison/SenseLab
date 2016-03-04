package com.cs246.senselab.storage.googledrive;

import com.cs246.senselab.storage.Children;
import com.google.android.gms.drive.DriveApi;

/**
 * Encapsulates the data of folder children within the GoogleDrive API
 */
public class DriveChildren implements Children<DriveApi.MetadataBufferResult> {
    DriveApi.MetadataBufferResult mChildren = null;

    public DriveChildren(DriveApi.MetadataBufferResult aChildren) {
        mChildren = aChildren;
    }

    public DriveApi.MetadataBufferResult getChildren() {
        return mChildren;
    }

    public void setChildren(DriveApi.MetadataBufferResult aChildren) {
        mChildren = aChildren;
    }
}
