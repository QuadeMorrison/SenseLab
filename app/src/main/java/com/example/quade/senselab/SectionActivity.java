package com.example.quade.senselab;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


public class SectionActivity extends AppCompatActivity {

    private List<Data> data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_section);

        Intent intent = getIntent();
        String content = intent.getStringExtra(MainActivity.EXTRA_TITLE);

        TextView tv = new TextView(this);
        tv.setText(content);
        LinearLayout layout = (LinearLayout) findViewById(R.id.section_layout);
        layout.addView(tv);
    }

    /**
     * Adds a new data item to the section and refreshes the view so the user may see it.
     *
     * @param newData The data to be added
     */
    public void addData(Data newData) {
        if (data == null)
            data = new ArrayList<Data>();

        data.add(newData);
    }

    /**
     * Adds a new data item to the section and refreshes the view so the user may see it.
     *
     * @param index Index of Data in list of Data to be changed
     */
    public void updateData(int index) {

    }

    /**
     * Returns the list of data items
     *
     * @return List The list of data
     */
    public List<Data> getData() {
        return data;
    }
}
