package com.example.quade.senselab;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class CreateLabReportActivity extends AppCompatActivity {
    private SenseLab senseLab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_lab_report);
    }

    public void createLabReport(View view) {
        EditText et = (EditText) findViewById(R.id.lab_report_name);
        String folderName = et.getText().toString();
        senseLab.getFolder().createFolder(folderName);

        Intent intent = new Intent(getBaseContext(), MainActivity.class);
        startActivity(intent);
    }
}
