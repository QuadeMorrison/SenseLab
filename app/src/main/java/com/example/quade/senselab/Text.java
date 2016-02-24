package com.example.quade.senselab;

/**
 * Text
 *
 * Created by Quade on 2/24/16.
 */
public class Text implements Data {

    private String content;

    Text() {

    }

    @Override
    public void modifyData(String data) {
        content = data;
    }
}
