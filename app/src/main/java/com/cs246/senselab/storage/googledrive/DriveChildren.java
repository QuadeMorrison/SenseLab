package com.cs246.senselab.storage.googledrive;

import com.cs246.senselab.storage.Children;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.Metadata;
import com.google.android.gms.drive.MetadataBuffer;

import java.util.ArrayList;
import java.util.List;

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

    @Override
    public List<String> getNames() {
        MetadataBuffer children = mChildren.getMetadataBuffer();
        List<String> names = null;

        if (names == null) {
            names = new ArrayList<>();
        }

        for (Metadata m : children) {
            names.add(m.getTitle());
        }

        return names;
    }

    @Override
    public List<String> getIds() {
        MetadataBuffer children = mChildren.getMetadataBuffer();
        List<String> ids = null;

        if (ids == null) {
            ids = new ArrayList<>();
        }

        for (Metadata m : children) {
            ids.add(m.getDriveId().encodeToString());
        }

        return ids;
    }
}
