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
            + "\"roadrunner\": {"
                + "\"inPerTick\" :\"0.01838175\n\","
                + "\"trackWidthTicks\":\"1648.8405974891646\","
                + "\"kS\":\"0.4921592103058434\","
                + "\"kV\":\"0.004414507494850345\","
                + "\"kA\":\"0.0003\","
                + "\"maxAngVel\":\"3.14159265\","
                + "\"maxAngAccel\":\"3.14159265\","
                + "\"turn\":\"1.0\","
                + "\"turnVelGain\":\"0.0\","
                + "\"ramseteZeta\":\"0.7\","
                + "\"ramseteBBar\":\"2.0\","
                + "\"maxWheelVel\":\"50.0\","
                + "\"minProfileAccel\":\"-30.0\","
                + "\"maxProfileAccel\":\"50.0\""
            + "},"
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