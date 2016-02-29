package com.example.quade.senselab;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class SensorActivity extends AppCompatActivity {

    SensorTag mysensor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_sensor);
    }

    //the clearSensorData class erases all the data that the sensor contains within it
    public void clearSensorData(){
        //if the sensor is not empty clear the data
        if (!mysensor.isEmpty()) {
            mysensor.clearData();
        }
        //make sure the data was cleared successfully
        assert(mysensor.isEmpty());
    }

}
