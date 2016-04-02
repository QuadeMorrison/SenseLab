package com.cs246.ble.sensortag.service;

import java.util.UUID;

import static java.util.UUID.fromString;

/**
 * Created by Quade on 3/19/16.
 */
public class ServiceGatt {
    public final static UUID

            AMB_TMP_SERV = fromString("f000aa00-0451-4000-b000-000000000000"),
            AMB_TMP_DATA = fromString("f000aa01-0451-4000-b000-000000000000"),
            AMB_TMP_CONF = fromString("f000aa02-0451-4000-b000-000000000000");
}
