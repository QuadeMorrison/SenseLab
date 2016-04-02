package com.cs246.ble.sensortag.service;

/**
 * Created by Quade on 3/19/16.
 */
class ConvertByte {

    public static Integer shortUnsignedAtOffset(byte[] data, int offset) {
        Integer lowerByte = (int) data[offset] & 0xFF;
        Integer upperByte = (int) data[offset+1] & 0xFF;
        return (upperByte << 8) + lowerByte;
    }
}
