/* -------------------------------------------------------
   Copyright (c) [2024] FASNY
   All rights reserved
   -------------------------------------------------------
   Chain Drive Train configuration
   ------------------------------------------------------- */

package org.firstinspires.ftc.teamcode.configurations;

public class ChainDriveTrain {
    /**
     * Class managing configuration and control of a basic chain train
     **/

    public static String s_Configuration = "{"
            + "\"name\": \"ChainDriveTrain\","
            + "\"components\": ["
                + "{"
                    + "\"name\": \"right-wheel\","
                    + "\"hardware\": \"rightWheel\","
                    + "\"type\": \"motor\","
                    + "\"reverse\": \"false\","
                    + "\"position\": {\"x\": 0.5, \"y\": 0, \"z\": 0.1},"
                    + "\"orientation\": {\"roll\": 0, \"pitch\": 90, \"yaw\": 0}"
                + "},"
                + "{"
                    + "\"name\": \"left-wheel\","
                    + "\"hardware\": \"leftWheel\","
                    + "\"type\": \"motor\","
                    + "\"reverse\": \"false\","
                    + "\"position\": {\"x\": 0.5, \"y\": 0, \"z\": 0.1},"
                    + "\"orientation\": {\"roll\": 0, \"pitch\": 90, \"yaw\": 0}"
                + "},"
                + "{"
                    + "\"name\": \"imu\","
                    + "\"hardware\": \"imu\","
                    + "\"type\": \"imu\","
                    + "\"logo\": \"up\","
                    + "\"usb\": \"left\""
                + "}"
            + "]"
            + "}";

}