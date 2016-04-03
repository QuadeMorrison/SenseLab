package com.cs246.ble.sensortag.service;

/**
 * Created by Quade on 4/2/16.
 */
public class HumidityService extends SensortagService {
    private static final String SERVICE_NAME = "Humidity";

    public HumidityService() {
        super();
        mServiceUUID = ServiceGatt.HUMIDITY_SERV;
        mDataUUID = ServiceGatt.HUMIDITY_DATA;
        mConfigUUID = ServiceGatt.HUMIDITY_CONF;
        mName = SERVICE_NAME;

    }

    public String extractData(byte[] data) {
        Double humidity = convertByteToHumidity(data);
        return String.format("%.2f%%", humidity);
    }

    private double convertByteToHumidity(byte [] data) {
        int offset = 2;
        double a = ConvertByte.shortUnsignedAtOffset(data, offset) / 128.0;

        return 100f * (a / 65535f) * 100;
    }
}
