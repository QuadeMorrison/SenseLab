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
    private List<String> names;
    private List<String> ids;

    public DriveChildren(DriveApi.MetadataBufferResult aChildren) {
        mChildren = aChildren;
        MetadataBuffer children = mChildren.getMetadataBuffer();
        initialize(children);
    }

    public DriveApi.MetadataBufferResult getChildren() {
        return mChildren;
    }

    public void setChildren(DriveApi.MetadataBufferResult aChildren) {
        mChildren = aChildren;
    }

    private void initialize(MetadataBuffer children) {
        if (names == null) {
            names = new ArrayList<>();
        }

        for (Metadata m : children) {
            names.add(m.getTitle());
        }


        if (ids == null) {
            ids = new ArrayList<>();
        }

        for (Metadata m : children) {
            ids.add(m.getDriveId().encodeToString());
        }
    }

    /**
     * Returns a list of the names of the children
     *
     * @return Children Names
     */
    @Override
    public List<String> getNames() { return names; }

    /**
     * Returns a list of the ids that represent the children on the google drive
     *
     * @return Children Ids
     */
    @Override
    public List<String> getIds() { return ids; }
}
