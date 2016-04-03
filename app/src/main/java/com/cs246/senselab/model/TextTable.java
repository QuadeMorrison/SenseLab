package com.cs246.senselab.model;

import android.content.Context;
import android.widget.TextView;

import com.cs246.senselab.storage.StorageProvider;
import com.cs246.senselab.storage.googledrive.GoogleDriveStorageProvider;

import java.util.List;
import java.util.ArrayList;

/**
 * Created by Quade on 4/2/16.
 */
public class TextTable {

    //member variables:
    private String mTitle;
    private String mId;
    private List<String> mDataList;
    private StorageProvider mProvider;

    //getters and setters
    public String getTitle(){return mTitle;}
    //public List<String> getData{ return mDataList; }
    public void setTitle(String newTitle){mTitle = newTitle;}
    public void setData(List<String> newDataList) {mDataList = newDataList;}

    //Constructors:
    public TextTable(Context context, String id, String title, StorageProvider provider) {
        mTitle = title;
        mDataList = new ArrayList();
        mId = id;
        mProvider = provider;
    }

    public TextTable(String newTitle, List<String> newDataList) {
        mTitle = newTitle; mDataList = newDataList;
    }

    public void setContentsViaString(String contents) {
        String[] dataArray = contents.split("\\s+");
        for (String data : dataArray) {
            mDataList.add(data);
        }
    }

    /**
     * Gets the file contents from file stored on Storage Provider
     */
    public void readFileContentsAsync(final TextTableCallback callback) {
        if (mId != null) {
            mProvider.readFileAsync(mId, new StorageProvider.FileAccessCallback() {
                @Override
                public void onResult(String contents) {
                    String[] dataArray = contents.split("\\s+");
                    for (String data : dataArray) {
                        mDataList.add(data);
                    }

                    callback.onResult();
                }
            });
        }
    }

    //function to add one data item to the data table
    public void addData(String newData){
        //add the new data item to the list.
        mDataList.add(0, newData);
        setTableData(mDataList);
    }

    public List<String> getDataList() {
        return mDataList;
    }

    //make a string that represents the text table
    public String makeTable(){

        //find the maximum width needed for the text table
        //this width is only of the data string and the spaces
        int tableWidth = mTitle.length();

        for (int i = 0 ; i < mDataList.size() ; i++ ) {
            if (mDataList.get(i).length() > tableWidth){
                tableWidth = mDataList.get(i).length();
            }
        }

        //add extra space for visual awesomeness
        tableWidth += 2;

        //make the horizontal dividers
        String horizontalDivider = "|-------------+";
        String topBottom = "----------------";

        for(int i = 0; i <= tableWidth ; i++) {
            horizontalDivider += "-";
            topBottom += "-";
        }

        horizontalDivider += "|\n";
        topBottom += "\n";

        //initialize the table
        String tableText = topBottom;

        //add the title to the top of the table
        tableText += "| Measurement | " + mTitle;
        for(int j = 0; j < (tableWidth - mTitle.length()); j++) {
            tableText += " ";
        }
        tableText += "|\n";


        //build the table
        for(int i = 0; i < mDataList.size(); i++ ) {

            //add the new line to the table text and add a horizontal divider
            tableText += horizontalDivider;

            //add the data string to the new line in the table
            String newLine = "";
            if(i < 9){
                newLine = "|      "+ String.valueOf(i+1) + "      " + "| ";
            }
            else if (i < 99){
                newLine = "|      "+ String.valueOf(i+1) + "     " + "| ";
            }
            else{
                newLine = "|      "+ String.valueOf(i+1) + "    " + "| ";
            }
            newLine += mDataList.get(i);

            //add any extra spacing needed to fill the rest of the line
            for(int j = 0; j < (tableWidth - mDataList.get(i).length()); j++) {
                newLine += " ";
            }
            newLine += "|\n";

            tableText += newLine;
        }

        //end the table
        tableText += topBottom;

        return tableText;
    }

    public interface TextTableCallback {
        void onResult();
    }

    private void setTableData(List<String> dataList) {
        String content = "";

        for (String data : dataList) {
            content += data + " ";
            System.out.println("SET TABLE DATA: " + data);
        }

        System.out.println(content);

        mProvider.writeToFileAsync(mId, content, new StorageProvider.FileAccessCallback() {
            @Override
            public void onResult(String contents) {
                System.out.println(contents);
            }
        });
    }

    public void setTableData(List<String> dataList, final TextTableCallback callback) {
        String content = "";

        for (String data : dataList) {
            content += data + " ";
            System.out.println("SET TABLE DATA: " + data);
        }

        System.out.println(content);

        mProvider.writeToFileAsync(mId, content, new StorageProvider.FileAccessCallback() {
            @Override
            public void onResult(String contents) {
                System.out.println(contents);
                callback.onResult();
            }
        });
    }
}
