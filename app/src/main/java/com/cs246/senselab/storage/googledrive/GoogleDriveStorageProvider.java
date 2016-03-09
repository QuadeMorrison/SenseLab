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
    private static final String TAG = GoogleDriveStorageProvider.class.getName();
    private static final String TAG = GoogleDriveStorageProvider.class.getSimpleName();

    public GoogleDriveStorageProvider(Activity aActivity) {
        mActivity = aActivity;
    }

    private class GoogleDriveClient implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
        private GoogleApiClient mGoogleApiClient = null;

        @Override
        public void onConnected(Bundle connectionHint) { }

        @Override
        public void onConnectionSuspended(int cause) { }

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

    @Override
    public void setFolder(String aName, String aId) {
        if (folder == null) {
            folder = new GoogleDriveFolder(aName, aId);
        }
    }

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

    @Override
    public void onPause() {
       if (mClient != null) {
           mClient.getGoogleApiClient().disconnect();
       }
    }

    @Override
    public void onResume() {
        if (mClient != null) {
            mClient.build();
        }
    }

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

    private final GoogleDriveStorageProvider getThis() { return this; }

    public class GoogleDriveFolder extends Folder {

        private GoogleDriveFolder() { }

        public GoogleDriveFolder(String aName) {
            super();
            mName = aName;
        }

        public GoogleDriveFolder(String aName, String aId) {
            super();
            mName = aName;
            mId = aId;
        }

        @Override
        protected void initialize(final CreateFileCallback callback) {
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

        private Query getFolderQuery() {
          return new Query.Builder()
                  .addFilter(Filters.and(Filters.eq(
                          SearchableField.TITLE, mName),
                          Filters.eq(SearchableField.TRASHED, false)))
                  .build();
        }

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

        private void createFolder(final CreateFileCallback callback) {
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

        @Override
        public void listChildrenAsync(final ListChildrenCallback callback) {
            initialize(new CreateFileCallback() {
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

        @Override
        public void createSubFolderAsync(final String aName, final CreateFileCallback callback) {
            initialize(new CreateFileCallback() {
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

        @Override
        public void createFileAsync(final String aName, final CreateFileCallback callback) {
            initialize(new CreateFileCallback() {
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
                                    callback.onCreate();
                                }
                            });
                }
            });
        }
    }
}
