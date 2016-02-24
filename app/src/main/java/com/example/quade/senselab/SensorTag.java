package com.example.quade.senselab;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jacob on 2/24/2016.
 * This class will encapsualates the necessary elements of a sensor to capture collected data.
 */

public class SensorTag {

    List<String> dataList;

    //the isEmpty Method checks to see that the SensorTag is empty and has no data returns a boolean
    public boolean isEmpty(){

        if(dataList != null || dataList.isEmpty()) {
            return true;
        }
        else{
            return false;
        }
    }

    //The clearData method allows the data within the SensorTag to be erased
    public void clearData(){
        if(! dataList.isEmpty()){
            dataList.clear();
        }
        assert(dataList.isEmpty());
    }

    public void addData(String newData){

        if(dataList == null){
            dataList = new ArrayList<String>();
            dataList.add(newData);
        }
        else{
            dataList.add(newData);
        }
        assert(!dataList.isEmpty());
    }
}

