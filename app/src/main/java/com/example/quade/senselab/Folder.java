package com.example.quade.senselab;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.Metadata;
import com.google.android.gms.drive.MetadataChangeSet;
import com.google.android.gms.drive.query.Filters;
import com.google.android.gms.drive.query.Query;
import com.google.android.gms.drive.query.SearchableField;

/**
 * Created by Quade on 2/25/16.
 */
public class Folder {

    private String name;
    private DriveId driveId;
    private GoogleApiClient googleApiClient;

    public Folder(String name, GoogleApiClient googleApiClient) {
        this.name = name;
        this.googleApiClient = googleApiClient;
        doesFolderExist();
    }

    public DriveId getTheDriveId() {
        System.out.print(driveId + "\n");
       return driveId;
    }

    /**
     * Creates a folder in the users root google drive directory. If the folder already exists then
     * nothing is created.
     */
    public void initializeFolder() {
        MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                .setTitle(name).build();
        Drive.DriveApi.getRootFolder(googleApiClient).createFolder(
                googleApiClient, changeSet).setResultCallback(initializeFolderCallback);
    }

    /**
     * Callback for file creation. Assigns folder a driveId upon creation.
     */
    final ResultCallback<DriveFolder.DriveFolderResult> initializeFolderCallback = new ResultCallback<DriveFolder.DriveFolderResult>() {
        @Override
        public void onResult(DriveFolder.DriveFolderResult result) {
            if (!result.getStatus().isSuccess()) {
                //showMessage("Error while trying to create the folder");
                return;
            }

            driveId = result.getDriveFolder().getDriveId();
        }
    };

    /**
     * Searches user's google drive directory for a folder of folder.name. If not found, creates
     * the folder.
     */
    private void doesFolderExist() {
        DriveApi.MetadataBufferResult result = Drive.DriveApi.query(googleApiClient, getFolderQuery()).await();

        if (!result.getStatus().isSuccess()) {
            //showMessage("Cannot create folder in the root.");
        } else {
            boolean isFound = false;
            for (Metadata m : result.getMetadataBuffer()) {
                if (m.getTitle().equals(name)) {
                    driveId = m.getDriveId();
                    isFound = true;
                    break;
                }
            }

            if (!isFound) {
                initializeFolder();
            }

            result.getMetadataBuffer().release();
        }

    }

    /**
     * Helper function for doesFolderExist(). Returns the query object needed to search users
     * drive directory for Folder.name
     *
     * @return Query Drive API object containing logic to search users drive for folder
     */
    private Query getFolderQuery() {
        return new Query.Builder()
                .addFilter(Filters.and(Filters.eq(
                                SearchableField.TITLE, name),
                        Filters.eq(SearchableField.TRASHED, false)))
                .build();

    }

    public void queryForChildren(ResultCallback<DriveApi.MetadataBufferResult> metadataCallback) {
        DriveFolder folder = driveId.asDriveFolder();
        folder.listChildren(googleApiClient).setResultCallback(metadataCallback);
    }

    public void createFolder(String name) {
        DriveFolder folder = driveId.asDriveFolder();
        MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                .setTitle(name).build();
        folder.createFolder(googleApiClient, changeSet)
                .setResultCallback(createFolderCallback);
    }

    final ResultCallback<DriveFolder.DriveFolderResult> createFolderCallback = new
            ResultCallback<DriveFolder.DriveFolderResult>() {

                @Override
                public void onResult(DriveFolder.DriveFolderResult result) {
                    if (!result.getStatus().isSuccess()) {
                        //showMessage("Problem while trying to create a folder");
                        return;
                    }
                }
            };

    public String getName() { return name; }
}
