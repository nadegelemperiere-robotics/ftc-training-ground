/* -------------------------------------------------------
   Copyright (c) [2025] Nadege LEMPERIERE
   All rights reserved
   -------------------------------------------------------
   Logging manager tests
   ------------------------------------------------------- */

package org.firstinspires.ftc.teamcode.test;

/* Qualcomm includes */
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

/* Acme robotics includes */
import com.acmerobotics.dashboard.FtcDashboard;

/* Component Under Test includes */
import org.firstinspires.ftc.teamcode.core.tools.Logger;

@TeleOp(name = "Logger Test", group = "Test")
public class LoggerTest extends LinearOpMode {

    private enum Suite {
        NONE,
        CONSTRUCTOR
    }

    Suite   mCurrentSuite;
    Suite   mNextSuite;
    
    Logger  mLogger;

    public void runOpMode() {

        telemetry.clear();
        telemetry.addLine("----- LOGGER TESTS -----");
        telemetry.update();
        FtcDashboard.getInstance().getTelemetry().clear();
        FtcDashboard.getInstance().getTelemetry().addLine("----- LOGGER TESTS -----");
        FtcDashboard.getInstance().getTelemetry().update();
        mCurrentSuite = Suite.NONE;
        mNextSuite = Suite.CONSTRUCTOR;

        waitForStart();

        while (opModeIsActive()) {

            if(mNextSuite != mCurrentSuite) {
                this.launch(mNextSuite);
                mNextSuite = mCurrentSuite;
            }




            sleep(200);
        }

    }

    private void launch(Suite suite) {
        if(suite == Suite.CONSTRUCTOR) { this.constructorTest(); }
        else {
            telemetry.addLine("Unknown suite " + suite);
            FtcDashboard.getInstance().getTelemetry().addLine("Unknown suite " + suite);
        }
    }

    private void constructorTest() {

        telemetry.addLine("--> Constructor test\n");
        FtcDashboard.getInstance().getTelemetry().addLine("--> Constructor test");

        telemetry.addLine("----> Driver station only");
        FtcDashboard.getInstance().getTelemetry().addLine("----> Driver station only");

        mLogger = new Logger(telemetry, null);
        for (Logger.Target target : Logger.Target.values()) {
           mLogger.addLine(target, "------> Line shall only appear on driver station");
           mLogger.addData(target, "------> DSO", "Data shall only appear on driver station");
        }

        telemetry.addLine("----> Dashboard only");
        FtcDashboard.getInstance().getTelemetry().addLine("----> Dashboard only");

        mLogger = new Logger(null, FtcDashboard.getInstance());
        for (Logger.Target target : Logger.Target.values()) {
            mLogger.addLine(target, "------> Line shall only appear on dashboard");
            mLogger.addData(target, "------> DSH", "shall only appear on dashboard");
        }

        telemetry.addLine("----> Nowhere");
        FtcDashboard.getInstance().getTelemetry().addLine("----> Nowhere");

        mLogger = new Logger(null, null);
        for (Logger.Target target : Logger.Target.values()) {
            mLogger.addLine(target, "------> Line shall not appear");
            mLogger.addData(target, "------> NWR", "shall not appear");
        }

        telemetry.addLine("----> Driver station and dashboard");
        FtcDashboard.getInstance().getTelemetry().addLine("----> Driver station and dashboard");

        mLogger = new Logger(telemetry, FtcDashboard.getInstance());
        for (Logger.Target target : Logger.Target.values()) {
            mLogger.addLine(target, "------> Line shall appear both on driver station and on dashboard");
            mLogger.addData(target, "------> BTH", "shall appear both on driver station and on dashboard");
        }

        for (Logger.Target target : Logger.Target.values()) {
            mLogger.update(target);
        }


    }



}
