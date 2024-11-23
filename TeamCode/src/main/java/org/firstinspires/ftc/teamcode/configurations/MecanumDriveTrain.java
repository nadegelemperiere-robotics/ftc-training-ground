/* -------------------------------------------------------
   Copyright (c) [2024] FASNY
   All rights reserved
   -------------------------------------------------------
   Mecanum Drive Train configuration
   ------------------------------------------------------- */

package org.firstinspires.ftc.teamcode.configurations;


import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;

public class MecanumDriveTrain {
    /**
     * Class managing configuration and control of a basic chain train
     **/

    public static String s_Configuration = "{"
            + "\"name\": \"MecanumDriveTrain\","
            + "\"drive\": \"mecanum\","
            + "\"roadrunner\": {"
                + "\"inPerTick\" :\"0.01838175\n\","
                + "\"lateralInPerTick\":\"0.015811966\n\","
                + "\"trackWidthTicks\":\"1648.8405974891646\","
                + "\"kS\":\"0.4921592103058434\","
                + "\"kV\":\"0.004414507494850345\","
                + "\"kA\":\"0.0003\","
                + "\"maxAngVel\":\"3.14159265\","
                + "\"maxAngAccel\":\"3.14159265\","
                + "\"axialGain\":\"6.0\","
                + "\"lateralGain\":\"6.0\","
                + "\"headingGain\":\"6.0\","
                + "\"axialVelGain\":\"0.0\","
                + "\"lateralVelGain\":\"0.0\","
                + "\"headingVelGain\":\"0.0\","
                + "\"maxWheelVel\":\"50.0\","
                + "\"minProfileAccel\":\"-30.0\","
                + "\"maxProfileAccel\":\"50.0\""
            + "},"
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