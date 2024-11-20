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
            + "\"drive\": \"tank\","
            + "\"fwd-in-per-tick\":\"0.025679\","
            + "\"lat-in-per-tick\":\"0.025679\","
            + "\"track-width-ticks\":\"1648.8405974891646\","
            + "\"ks\":\"0.5532344924978014\","
            + "\"kv\":\"0.004382910398567615\","
            + "\"ka\":\"0.004382910398567615\","
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