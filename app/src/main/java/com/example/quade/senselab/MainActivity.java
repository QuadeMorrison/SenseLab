package com.example.quade.senselab;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.DriveApi.MetadataBufferResult;

public class MainActivity extends BaseDriveActivity {

    public final static String EXTRA_TITLE = "com.example.quade.senselab.TITLE";

    private ListView mResultsListView;
    private ResultsAdapter mResultsAdapter;
    private SenseLab senseLab;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_main);
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        super.onConnected(connectionHint);
        new createSenseLabFolder().execute(getGoogleApiClient());
    }

    private class createSenseLabFolder extends AsyncTask<GoogleApiClient, Void, Void> {
        protected Void doInBackground(GoogleApiClient... googleApiClient) {
            senseLab = new SenseLab(getGoogleApiClient());
            senseLab.getFolder().queryForChildren(fillListCallback);
            return null;
        }
    }

    final private ResultCallback<MetadataBufferResult> fillListCallback = new
            ResultCallback<MetadataBufferResult>() {
                @Override
                public void onResult(MetadataBufferResult result) {
                    if (!result.getStatus().isSuccess()) {
                        showMessage("Problem while retrieving files");
                        return;
                    }

                    initilizeListView(result);
                    setListClickListener();
                }
            };

    private void initilizeListView(MetadataBufferResult result) {
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

                startActivity(intent);
            }
        });
    }

    public void toLabReportCreationWizard(View view) {
        Intent intent = new Intent(getBaseContext(), CreateLabReportActivity.class);
        startActivity(intent);
    }
}
