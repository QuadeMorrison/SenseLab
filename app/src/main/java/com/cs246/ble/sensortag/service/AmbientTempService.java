package com.cs246.ble.sensortag.service;

/**
 * Created by Quade on 3/19/16.
 */
public class AmbientTempService extends SensortagService {
    private static final String SERVICE_NAME = "Ambient Temperature";

    public AmbientTempService() {
        super();
        mServiceUUID = ServiceGatt.AMB_TMP_SERV;
        mDataUUID = ServiceGatt.AMB_TMP_DATA;
        mConfigUUID = ServiceGatt.AMB_TMP_CONF;
        mName = SERVICE_NAME;

    }

    public String extractData(byte[] data) {
        Double temp = convertByteToTemp(data);
        return String.valueOf(temp);
    }

    private double convertByteToTemp(byte [] data) {
        int offset = 2;
        return ConvertByte.shortUnsignedAtOffset(data, offset) / 128.0;
    }
}
