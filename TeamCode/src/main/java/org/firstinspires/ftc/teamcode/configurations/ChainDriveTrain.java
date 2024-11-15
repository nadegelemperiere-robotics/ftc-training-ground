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
            + "\"name\": \"right\","
            + "\"hardware\": \"rightWheel\","
            + "\"type\": \"motor\","
            + "\"position\": {\"x\": 0.5, \"y\": 0, \"z\": 0.1},"
            + "\"orientation\": {\"roll\": 0, \"pitch\": 90, \"yaw\": 0}"
            + "},"
            + "{"
            + "\"name\": \"left\","
            + "\"hardware\": \"leftWheel\","
            + "\"type\": \"motor\","
            + "\"position\": {\"x\": 0.5, \"y\": 0, \"z\": 0.1},"
            + "\"orientation\": {\"roll\": 0, \"pitch\": 90, \"yaw\": 0}"
            + "}"
            + "]"
            + "}";

}