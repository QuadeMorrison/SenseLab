package com.cs246.senselab.storage;

/**
 * Encapsulates different storage APIs to easily access and modify data
 */
public interface StorageProvider {

    /**
     * Callback to ensure that the <code>StorageProvider</code> does not do any work until it has
     * connected to the API service
     */
    interface ConnectCallback {
        void onConnect(StorageProvider provider);
    }

    void setFolder(String aName, String aId);
    //Folder getFolder();
    void connect(ConnectCallback callback);
    void onResume();
    void onPause();
    Folder getFolder();
}
