package com.example.quade.senselab;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.drive.DriveId;

public class CreateLabReportActivity extends BaseDriveActivity {
    Folder senseLab = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_lab_report);

    }

    @Override
    public void onConnected(Bundle connectionHint) {
        super.onConnected(connectionHint);
        Button finishCreateLabReport = (Button) findViewById(R.id.finish_create_lab_report);
        finishCreateLabReport.setVisibility(View.VISIBLE);

        GoogleDriveSingleton.getInstance().setGoogleApiClient(getGoogleApiClient());
        Intent intent = getIntent();
        DriveId driveId = DriveId.decodeFromString(intent.getStringExtra(MainActivity.EXTRA_DRIVEID));
        senseLab = new Folder("SenseLab", driveId);
    }

    public void createLabReport(View view) {
        EditText et = (EditText) findViewById(R.id.lab_report_name);
        String folderName = et.getText().toString();

       class createLabReportFolder extends AsyncTask<String, Void, Void> {
            protected Void doInBackground(String... folderName) {
                senseLab.createFolder(folderName[0]);

                Intent intent = new Intent(getBaseContext(), MainActivity.class);
                getGoogleApiClient().disconnect();
                startActivity(intent);
                return null;
            }
        }

        new createLabReportFolder().execute(folderName);
    }

}
