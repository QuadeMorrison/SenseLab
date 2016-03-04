package com.cs246.senselab.storage;

/**
 * Holds data in the form of sub-folders and files
 */
public abstract class Folder {
    public String mId;
    public String mName;

    public String getId() { return mId; };
    public String getName() { return mName; };

    /**
     * Callback to do work during certain stages of the Async process of creating a folder
     */
    public interface CreateFolderCallback {
        void onCreate(); // When folder creation is complete
    }

    /**
     * Callback to do work during certain stages of the Async process of listing a folder's
     * children
     */
    public interface ListChildrenCallback {
        void onChildrenListed(Children children);
    }

    protected abstract void initialize(CreateFolderCallback callback);
    public abstract void listChildrenAsync(ListChildrenCallback callback);
    public abstract void createSubFolderAsync(final String name, final CreateFolderCallback callback);
}
