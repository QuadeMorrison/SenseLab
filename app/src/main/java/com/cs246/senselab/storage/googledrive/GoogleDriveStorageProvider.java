package com.cs246.senselab.storage.googledrive;

import android.app.Activity;
import android.content.Context;
import android.content.IntentSender;
import android.os.Bundle;
import android.util.Log;

import com.cs246.senselab.storage.StorageProvider;
import com.cs246.senselab.storage.Folder;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.Metadata;
import com.google.android.gms.drive.MetadataChangeSet;
import com.google.android.gms.drive.query.Filters;
import com.google.android.gms.drive.query.Query;
import com.google.android.gms.drive.query.SearchableField;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;

/**
 * Collection of methods that use the Google Drive API to store data in the form of folders and files
 */
public final class GoogleDriveStorageProvider implements StorageProvider {
    private Activity mActivity = null;
    private GoogleDriveClient mClient = null;
    private GoogleDriveFolder folder = null;
    private static final String TAG = GoogleDriveStorageProvider.class.getSimpleName();

    /**
     * Constructor
     *
     * @param aActivity
     */
    public GoogleDriveStorageProvider(Activity aActivity) {
        mActivity = aActivity;
    }

    /**
     * Google Client used to interface with the API. Contains the required methods to connect to the Google Drive
     */
    private class GoogleDriveClient implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
        private GoogleApiClient mGoogleApiClient = null;

        /**
         * All the code written within this function will run when the client connects to the drive
         *
         * @param connectionHint
         */
        @Override
        public void onConnected(Bundle connectionHint) { }

        /**
         * All the code written within this function will run if the connection is interupted
         *
         * @param cause
         */
        @Override
        public void onConnectionSuspended(int cause) { }

        /**
         * Code will run if a connection can't be made
         *
         * @param result
         */
        @Override
        public void onConnectionFailed(ConnectionResult result) {
            Log.d(TAG, "Connection failed");
            if (result.hasResolution()) {
                try {
                    result.startResolutionForResult(mActivity, 1);
                    mGoogleApiClient.connect();
                } catch (IntentSender.SendIntentException e) {
                    e.printStackTrace();
                }
            }
        }

        /**
         * Creates the Google Api Client, and requests to connect with it
         */
        public void build() {
            if (mGoogleApiClient == null) {
                mGoogleApiClient = new GoogleApiClient.Builder(mActivity)
                        .addApi(Drive.API)
                        .addScope(Drive.SCOPE_FILE)
                        .addConnectionCallbacks(this)
                        .addOnConnectionFailedListener(this)
                        .build();
            }

            mGoogleApiClient.connect();
        }
        public GoogleApiClient getGoogleApiClient() { return mGoogleApiClient; }
    }

    /**
     * Specify the folder that the storage provider represents
     *
     * @param aName Given name of the folder
     * @param aId The ID that represents this folder within the Drive API
     */
    @Override
    public void setFolder(String aName, String aId) {
        if (folder == null) {
            folder = new GoogleDriveFolder(aName, aId);
        }
    }

    /**
     * When called issues request to connect to Google Drive Client
     *
     * @param callback The code specified in the onConnect method within this callback is run upon
     *                 connection to the client
     */
    @Override
    public void connect(final ConnectCallback callback) {
        if (mClient == null) {
            mClient = new GoogleDriveClient() {
                @Override
                public void onConnected(Bundle connectionHint) {
                    callback.onConnect(getThis());
                }
            };
        }
    }

    /**
     * Intended to be called in the activities onPause method
     */
    @Override
    public void onPause() {
       if (mClient != null) {
           mClient.getGoogleApiClient().disconnect();
       }
    }

    /**
     * Intended to be called in the activities onResume method
     */
    @Override
    public void onResume() {
        if (mClient != null) {
            mClient.build();
        }
    }

    /**
     * Opens the file on the users Google Drive and retrieves the files contents
     *
     * @param fileId Id that Google Drive refers to the file
     * @param callback The onResult method passes the file contents to the user
     */
    @Override
    public void readFileAsync(String fileId, final FileAccessCallback callback) {

        if(fileId == null) {
            Log.d(TAG, "The FileID does not exist.");
        }

        DriveFile file = DriveId.decodeFromString(fileId).asDriveFile();
        file.open(mClient.getGoogleApiClient(), DriveFile.MODE_READ_ONLY, null)
                .setResultCallback(new ResultCallback<DriveApi.DriveContentsResult>() {
                    @Override
                    public void onResult(DriveApi.DriveContentsResult driveContentsResult) {
                        try {
                            DriveContents contents = driveContentsResult.getDriveContents();
                            BufferedReader reader = new BufferedReader(new InputStreamReader(contents.getInputStream()));
                            StringBuilder builder = new StringBuilder();
                            String line;
                            while ((line = reader.readLine()) != null) {
                                builder.append(line);
                            }
                            String contentsAsString = builder.toString();
                            callback.onResult(contentsAsString);

                            contents.discard(mClient.getGoogleApiClient());
                        } catch (IOException e) {
                            System.out.println(e);
                        }
                    }
                });
    }

    /**
     * Overwrites the contents of a given file with a given string
     *
     * @param fileId Id of the file that is to be overwritten
     * @param contents String to write to file
     * @param callback Passes written contents back to the user
     */
    @Override
    public void writeToFileAsync(String fileId, final String contents, final FileAccessCallback callback) {
        DriveFile file = DriveId.decodeFromString(fileId).asDriveFile();
        file.open(mClient.getGoogleApiClient(), DriveFile.MODE_WRITE_ONLY, null)
                .setResultCallback(new ResultCallback<DriveApi.DriveContentsResult>() {
                    @Override
                    public void onResult(DriveApi.DriveContentsResult driveContentsResult) {
                        try {
                            DriveContents driveContents = driveContentsResult.getDriveContents();
                            OutputStream outputStream = driveContents.getOutputStream();
                            outputStream.write(contents.getBytes());
                            driveContents.commit(mClient.getGoogleApiClient(), null)
                                    .setResultCallback(new ResultCallback<Status>() {
                                        @Override
                                        public void onResult(Status status) {
                                            callback.onResult(contents);
                                        }
                                    });
                        } catch (IOException e) {
                            System.out.println(e);
                        }
                    }
                });
    }

    public GoogleDriveFolder getFolder() { return folder; }

    /**
     * Used to refer to this within nested classes
     *
     * @return this
     */
    private final GoogleDriveStorageProvider getThis() { return this; }

    /**
     * Holds methods for operating on folders using the Google Drive API
     */
    public class GoogleDriveFolder extends Folder {

        /**
         * Do not allow the user to initialize a folder without a name
         */
        private GoogleDriveFolder() { }

        /**
         * Initialize with folder name. Folder may or may not already exist on the drive
         *
         * @param name Name of folder to be created
         */
        public GoogleDriveFolder(String name) {
            super();
            mName = name;
        }

        /**
         * Initialize with folder name and id of folder that already exists within the google drive.
         *
         * @param name Name of the folder
         * @param id Id that google drive refers to folder as
         */
        public GoogleDriveFolder(String name, String id) {
            super();
            mName = name;
            mId = id;
        }

        /**
         * Checks if folder exists or not, and either creates it if it does not, or retrieves the id
         *
         * @param callback
         */
        @Override
        protected void initialize(final CreateFolderCallback callback) {
           if (mId == null) {
               Drive.DriveApi.query(mClient.getGoogleApiClient(), getFolderQuery())
                       .setResultCallback(new ResultCallback<DriveApi.MetadataBufferResult>() {
                           @Override
                           public void onResult(DriveApi.MetadataBufferResult result) {
                               if (!result.getStatus().isSuccess()) {
                                   System.out.print("Could not create Folder\n");
                               }

                               if (!doesExist(result)) {
                                   createFolder(callback);
                               } else {
                                   callback.onCreate();
                               }

                               result.release();
                           }
                       });
           } else {
               callback.onCreate();
           }
        }

        /**
         * Retrieves the query to search for folder
         *
         * @return A query to find the folder on the users drive
         */
        private Query getFolderQuery() {
          return new Query.Builder()
                  .addFilter(Filters.and(Filters.eq(
                          SearchableField.TITLE, mName),
                          Filters.eq(SearchableField.TRASHED, false)))
                  .build();
        }

        /**
         * Searches for the folder based on the retrieved contents from the users drive
         *
         * @param result All the files and folders found within a folder on the users Google Drive
         * @return true if folder is found, false otherwise
         */
        private boolean doesExist(DriveApi.MetadataBufferResult result) {
            boolean isFound = false;

            for (Metadata m : result.getMetadataBuffer()) {
                if (m.getTitle().equals(mName)) {
                    mId = m.getDriveId().encodeToString();
                    isFound = true;
                    break;
                }
            }

            result.release();
            return isFound;
        }

        /**
         * Creates the folder that is to represent the StorageProvider
         *
         * @param callback Allows the user to specify a method to do work after the folder is created
         */
        private void createFolder(final CreateFolderCallback callback) {
            MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                    .setTitle(mName).build();
            Drive.DriveApi.getRootFolder(mClient.getGoogleApiClient())
                    .createFolder(mClient.getGoogleApiClient(), changeSet)
                    .setResultCallback(new ResultCallback<DriveFolder.DriveFolderResult>() {
                        @Override
                        public void onResult(DriveFolder.DriveFolderResult driveFolderResult) {
                            mId = driveFolderResult.getDriveFolder().getDriveId().encodeToString();
                            callback.onCreate();
                        }
                    });
        }

        /**
         * Retrieves data about all the children of the folder represented by this
         *
         * @param callback Allows the user to specify a method to do work after the children have been listed
         */
        @Override
        public void listChildrenAsync(final ListChildrenCallback callback) {
            initialize(new CreateFolderCallback() {
                @Override
                public void onCreate() {
                    DriveFolder folder = DriveId.decodeFromString(mId).asDriveFolder();

                    folder.listChildren(mClient.getGoogleApiClient())
                            .setResultCallback(new ResultCallback<DriveApi.MetadataBufferResult>() {
                                @Override
                                public void onResult(DriveApi.MetadataBufferResult result) {
                                    if (!result.getStatus().isSuccess()) {
                                        //showMessage("Problem while trying to retrieve folder children");
                                    }
                                    callback.onChildrenListed(new DriveChildren(result));
                                }
                            });
                }
            });
        }

        /**
         * Create a folder within the current folder represented by this
         *
         * @param aName The name of the sub-folder to be created
         * @param callback Allows the user to specify a method to do work after the folder has been created
         */
        @Override
        public void createSubFolderAsync(final String aName, final CreateFolderCallback callback) {
            initialize(new CreateFolderCallback() {
                @Override
                public void onCreate() {
                    MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                            .setTitle(aName).build();
                    DriveFolder parent = DriveId.decodeFromString(mId).asDriveFolder();
                    parent.createFolder(mClient.getGoogleApiClient(), changeSet)
                            .setResultCallback(new ResultCallback<DriveFolder.DriveFolderResult>() {
                                @Override
                                public void onResult(DriveFolder.DriveFolderResult driveFolderResult) {
                                    callback.onCreate();
                                }
                            });
                }
            });
        }

        /**
         * Create a file within the current folder represented by this
         *
         * @param aName The name of the file to be created
         * @param callback Allows the user to specify a method to do work after the file has been created
         */
        @Override
        public void createFileAsync(final String aName, final CreateFileCallback callback) {
            initialize(new CreateFolderCallback() {
                @Override
                public void onCreate() {
                    MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                            .setTitle(aName)
                            .setMimeType("text/plain")
                            .build();

                    DriveFolder parent = DriveId.decodeFromString(mId).asDriveFolder();
                    parent.createFile(mClient.getGoogleApiClient(), changeSet, null)
                            .setResultCallback(new ResultCallback<DriveFolder.DriveFileResult>() {
                                @Override
                                public void onResult(DriveFolder.DriveFileResult driveFileResult) {
                                    String id = driveFileResult.getDriveFile().getDriveId().encodeToString();
                                    callback.onCreate(id);
                                }
                            });
                }
            });
        }
    }
}
