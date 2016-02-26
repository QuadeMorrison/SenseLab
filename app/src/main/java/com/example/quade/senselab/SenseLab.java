package com.example.quade.senselab;

import com.google.android.gms.common.api.GoogleApiClient;

/**
 * Created by Quade on 2/25/16.
 */
public class SenseLab {
    private Folder folder;

    public SenseLab(GoogleApiClient googleApiClient) {
        folder = new Folder("SenseLab", googleApiClient);
    }

    public Folder getFolder() { return folder; }
}
