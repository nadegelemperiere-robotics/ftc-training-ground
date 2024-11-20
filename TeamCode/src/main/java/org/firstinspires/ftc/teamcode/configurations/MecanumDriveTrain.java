/* -------------------------------------------------------
   Copyright (c) [2024] FASNY
   All rights reserved
   -------------------------------------------------------
   Mecanum Drive Train configuration
   ------------------------------------------------------- */

package org.firstinspires.ftc.teamcode.configurations;


public class MecanumDriveTrain {
    /**
     * Class managing configuration and control of a basic chain train
     **/

    public static String s_Configuration = "{"
            + "\"name\": \"MecanumDriveTrain\","
            + "\"drive\": \"mecanum\","
            + "\"components\": ["
                + "{"
                    + "\"name\": \"right-front-wheel\","
                    + "\"hardware\": \"rightFrontWheel\","
                    + "\"type\": \"motor\","
                    + "\"reverse\": \"false\","
                    + "\"position\": {\"x\": 0.5, \"y\": 0, \"z\": 0.1},"
                    + "\"orientation\": {\"roll\": 0, \"pitch\": 90, \"yaw\": 0}"
                + "},"
                + "{"
                    + "\"name\": \"left-front-wheel\","
                    + "\"hardware\": \"leftFrontWheel\","
                    + "\"type\": \"motor\","
                    + "\"reverse\": \"false\","
                    + "\"position\": {\"x\": 0.5, \"y\": 0, \"z\": 0.1},"
                    + "\"orientation\": {\"roll\": 0, \"pitch\": 90, \"yaw\": 0}"
                + "},"
                + "{"
                    + "\"name\": \"right-back-wheel\","
                    + "\"hardware\": \"rightBackWheel\","
                    + "\"reverse\": \"false\","
                    + "\"type\": \"motor\","
                    + "\"position\": {\"x\": 0.5, \"y\": 0, \"z\": 0.1},"
                    + "\"orientation\": {\"roll\": 0, \"pitch\": 90, \"yaw\": 0}"
                + "},"
                + "{"
                    + "\"name\": \"left-back-wheel\","
                    + "\"hardware\": \"leftBackWheel\","
                    + "\"reverse\": \"true\","
                    + "\"type\": \"motor\","
                    + "\"position\": {\"x\": 0.5, \"y\": 0, \"z\": 0.1},"
                    + "\"orientation\": {\"roll\": 0, \"pitch\": 90, \"yaw\": 0}"
                + "},"
                + "{"
                    + "\"name\": \"camera\","
                    + "\"hardware\": \"webcam\","
                    + "\"type\": \"camera\","
                    + "\"position\": {\"x\": 0.5, \"y\": 0, \"z\": 0.1},"
                    + "\"orientation\": {\"roll\": 0, \"pitch\": 90, \"yaw\": 0}"
                + "},"
                + "{"
                    + "\"name\": \"imu\","
                    + "\"hardware\": \"imu\","
                    + "\"type\": \"imu\","
                    + "\"position\": {\"x\": 0, \"y\": 0, \"z\": 0},"
                    + "\"orientation\": {\"roll\": 0, \"pitch\": 0, \"yaw\": 90}"
                + "}"
            + "]"
            + "}";

}