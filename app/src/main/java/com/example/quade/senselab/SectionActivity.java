package com.example.quade.senselab;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;


public class SectionActivity extends AppCompatActivity {

    private List<Data> data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_section);
    }

    /**
     * Adds a new data item to the section and refreshes the view so the user may see it.
     *
     * @author Quade Morrison
     * @param newData The data to be added
     */
    public void addData(Data newData) {
        data.add(newData);
    }

    /**
     * Adds a new data item to the section and refreshes the view so the user may see it.
     *
     * @author Quade Morrison
     * @param index Index of Data in list of Data to be changed
     */
    public void updateData(int index) {

    }

    /**
     * Returns the list of data items
     *
     * @author Quade Morrison
     * @return List The list of data
     */
    public List<Data> getData() {
        return data;
    }
}
