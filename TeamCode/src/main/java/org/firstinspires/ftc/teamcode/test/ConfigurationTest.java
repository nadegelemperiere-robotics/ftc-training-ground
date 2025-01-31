/* -------------------------------------------------------
   Copyright (c) [2025] Nadege LEMPERIERE
   All rights reserved
   -------------------------------------------------------
   Configuration manager tests
   ------------------------------------------------------- */

package org.firstinspires.ftc.teamcode.test;

/* System includes */
import android.os.Environment;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/* Qualcomm includes */
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

/* Acme robotics includes */
import com.acmerobotics.dashboard.FtcDashboard;

/* Tools includes */
import org.firstinspires.ftc.teamcode.core.tools.Logger;

/* Component Under Test includes */
import org.firstinspires.ftc.teamcode.core.configuration.Configuration;
import org.json.JSONException;

@TeleOp(name = "ConfigurationTest", group = "Test")
public class ConfigurationTest extends LinearOpMode {

    private enum Suite {
        NONE,
        MOTOR
    }

    Suite   mCurrentSuite;

    Logger  mLogger;

    public void runOpMode() {


        mLogger = new Logger(telemetry,FtcDashboard.getInstance());

        mLogger.clear();
        mLogger.addLine("----- CONFIGURATION TESTS -----");
        mLogger.update();

        mCurrentSuite = Suite.MOTOR;

        waitForStart();

        while (opModeIsActive()) {

            try {
                this.launch(mCurrentSuite);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }

            mLogger.update();
            sleep(200);
        }

    }

    private void launch(Suite suite) throws NoSuchFieldException, IllegalAccessException {
        if(suite == Suite.MOTOR) { this.motorNominalTest(); }
        else {
            telemetry.addLine("Unknown suite " + suite);
            FtcDashboard.getInstance().getTelemetry().addLine("Unknown suite " + suite);
        }
    }

    private Suite next(Suite suite) {
        if(suite == Suite.MOTOR) { return Suite.MOTOR; }
        return Suite.NONE;
    }

    private void motorNominalTest() {

        mLogger.addLine("MOTOR TEST");

        String confPath = Environment.getExternalStorageDirectory().getPath() + "/FIRST/motor-nominal.json";

        try (FileWriter writer = new FileWriter(confPath)) {
            writer.write(ConfigurationTest.sMotorNominalTestConfiguration);
        } catch (IOException e) {
            mLogger.addLine(e.getMessage());
        }

        Configuration configuration = new Configuration(mLogger);
        try {
            configuration.read(confPath);
            configuration.log();
        } catch (IOException | JSONException e) {
            mLogger.addLine(e.getMessage());
        }

        File file = new File(confPath);
        if (file.exists()) {
            boolean is_deleted = file.delete();
            if(!is_deleted) { mLogger.addLine("Failed to remove file " + confPath); }
        }

    }


    private static final String sMotorNominalTestConfiguration = "{\n"
        + "    \"hardware\" : {\n"
        + "        \"motors\" : {\n"
        + "            \"front-left-wheel\" : {\n"
        + "                \"controllers\" : [\n"
        + "                    {\"hwmap\": \"frontLeft\"}\n"
        + "                ]\n"
        + "             },\n"
        + "            \"front-right-wheel\" : {\n"
        + "                \"controllers\" : [\n"
        + "                    {\"hwmap\": \"frontRight\",\"direction\": \"reverse\"}\n"
        + "                ]\n"
        + "             },\n"
        + "            \"back-left-wheel\" : {\n"
        + "                \"controllers\" : [\n"
        + "                    {\"hwmap\": \"backLeft\"}\n"
        + "                ]\n"
        + "             },\n"
        + "            \"back-right-wheel\" : {\n"
        + "                \"controllers\" : [\n"
        + "                    {\"hwmap\": \"backRight\",\"direction\": \"reverse\"}\n"
        + "                ]\n"
        + "             },\n"
        + "            \"intake-slides\" : {\n"
        + "                \"controllers\" : [\n"
        + "                    {\"hwmap\": \"intakeSlidesLeft\"},\n"
        + "                    {\"hwmap\": \"intakeSlidesRight\",\"direction\": \"reverse\"}\n"
        + "                ],\n"
        + "                \"positions\": {\n"
        + "                    \"min\": 0,\n"
        + "                    \"transfer\": 179,\n"
        + "                    \"retracted\": 250,\n"
        + "                    \"init\": 300,\n"
        + "                    \"max\": 315\n"
        + "                }\n"
        + "             },"
        + "            \"outtake-slides\" : {\n"
        + "                \"controllers\" : [\n"
        + "                    {\"hwmap\": \"outtakeSlidesLeft\",\"direction\": \"reverse\"},\n"
        + "                    {\"hwmap\": \"outtakeSlidesRight\"}\n"
        + "                ],\n"
        + "                \"positions\": {\n"
        + "                    \"min\": 0,\n"
        + "                    \"transfer\": 0,\n"
        + "                    \"retracted\": 1300,\n"
        + "                    \"max\": 3726\n"
        + "                }\n"
        + "             }\n"
        + "         }\n"
        + "     }\n"
        + " }";


}
