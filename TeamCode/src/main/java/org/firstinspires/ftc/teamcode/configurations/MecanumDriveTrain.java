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
            + "\"components\": ["
            + "{"
            + "\"name\": \"right-front\","
            + "\"hardware\": \"rightFrontWheel\","
            + "\"type\": \"motor\","
            + "\"position\": {\"x\": 0.5, \"y\": 0, \"z\": 0.1},"
            + "\"orientation\": {\"roll\": 0, \"pitch\": 90, \"yaw\": 0}"
            + "},"
            + "{"
            + "\"name\": \"left-front\","
            + "\"hardware\": \"leftFrontWheel\","
            + "\"type\": \"motor\","
            + "\"position\": {\"x\": 0.5, \"y\": 0, \"z\": 0.1},"
            + "\"orientation\": {\"roll\": 0, \"pitch\": 90, \"yaw\": 0}"
            + "},"
            + "{"
            + "\"name\": \"right-back\","
            + "\"hardware\": \"rightBackWheel\","
            + "\"type\": \"motor\","
            + "\"position\": {\"x\": 0.5, \"y\": 0, \"z\": 0.1},"
            + "\"orientation\": {\"roll\": 0, \"pitch\": 90, \"yaw\": 0}"
            + "},"
            + "{"
            + "\"name\": \"left-back\","
            + "\"hardware\": \"leftBackWheel\","
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
            + "}"
            + "]"
            + "}";

}