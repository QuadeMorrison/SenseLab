package com.example.quade.senselab;

import com.google.android.gms.common.api.GoogleApiClient;

/**
 * Created by Quade on 2/28/16.
 */
public final class GoogleDriveSingleton {
    private static GoogleDriveSingleton instance = null;
    private static GoogleApiClient mGoogleApiClient = null;

    private GoogleDriveSingleton() { }

    public static GoogleDriveSingleton getInstance() {
        if (instance == null) {
            instance = new GoogleDriveSingleton();
        }

        return instance;
    }

    public GoogleApiClient getGoogleApiClient() {
        return mGoogleApiClient;
    }

    public void setGoogleApiClient(GoogleApiClient aGoogleApiClient) {
       mGoogleApiClient = aGoogleApiClient;
    }
}
