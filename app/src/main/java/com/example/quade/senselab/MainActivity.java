package com.example.quade.senselab;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.google.android.gms.drive.DriveApi;

public class MainActivity extends BaseDriveActivity {

    public final static String EXTRA_TITLE = "com.example.quade.senselab.TITLE";
    public final static String EXTRA_DRIVEID = "com.example.quade.senselab.DRIVEID";

    private ListView mResultsListView;
    private ResultsAdapter mResultsAdapter;
    private SenseLab senseLab = null;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_main);
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        super.onConnected(connectionHint);
        Button newLabReport = (Button) findViewById(R.id.lab_report_button);
        newLabReport.setVisibility(View.VISIBLE);

        class InitializeSenseLab extends AsyncTask<Void, Void, Void> {
            protected Void doInBackground(Void... Void) {
                initializeGoogleDriveSingleton();
                senseLab = new SenseLab();

                listLabReports();
                return null;
            }
        }

        new InitializeSenseLab().execute();
    }

    private void initializeGoogleDriveSingleton() {
        GoogleDriveSingleton GoogleDrive = GoogleDriveSingleton.getInstance();
        GoogleDrive.setGoogleApiClient(getGoogleApiClient());
    }

    private void listLabReports() {
        senseLab.getFolder().queryForChildren(new Folder.queryForChildrenCallback() {
            @Override
            public void onListChildren(DriveApi.MetadataBufferResult result) {
                initilizeListView(result);
                setListClickListener();
            }
        });
    }

    private void initilizeListView(DriveApi.MetadataBufferResult result) {
        mResultsListView = (ListView) findViewById(R.id.labReportList);
        mResultsAdapter = new ResultsAdapter(this);
        mResultsListView.setAdapter(mResultsAdapter);
        mResultsAdapter.append(result.getMetadataBuffer());
    }

    private void setListClickListener() {
        mResultsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String title = mResultsAdapter.getItem(position).getTitle();

                Intent intent = new Intent(getBaseContext(), SectionActivity.class);
                intent.putExtra(EXTRA_TITLE, title);

                getGoogleApiClient().disconnect();
                startActivity(intent);
            }
        });
    }

    public void toLabReportCreationWizard(View view) {
        if (senseLab != null) {
            Intent intent = new Intent(getBaseContext(), CreateLabReportActivity.class);
            intent.putExtra(EXTRA_DRIVEID, senseLab.getFolder().getDriveId().encodeToString());

            getGoogleApiClient().disconnect();
            startActivity(intent);
        }
    }
}
