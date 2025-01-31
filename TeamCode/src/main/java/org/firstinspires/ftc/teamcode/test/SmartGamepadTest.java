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

/* Components includes */
import org.firstinspires.ftc.teamcode.core.components.SmartGamepad;

@TeleOp(name = "SmartGamepadTest", group = "Test")
public class SmartGamepadTest extends LinearOpMode {

    private enum Suite {
        NONE,
        A,
        B
    }

    Suite           mCurrentSuite;

    SmartGamepad    mGamepad1;
    SmartGamepad    mGamepad2;

    public void runOpMode() {

        telemetry.clear();
        telemetry.addLine("----- SMART GAMEPAD TESTS -----");
        telemetry.update();
        FtcDashboard.getInstance().getTelemetry().clear();
        FtcDashboard.getInstance().getTelemetry().addLine("----- SMART GAMEPAD TESTS -----");
        FtcDashboard.getInstance().getTelemetry().update();

        mCurrentSuite = Suite.A;

        mGamepad1 = new SmartGamepad(gamepad1,null);
        mGamepad2 = new SmartGamepad(gamepad2,null);

        waitForStart();

        while (opModeIsActive()) {

            try {

                this.launch(mCurrentSuite);

                if (mGamepad1.buttons.a.pressedOnce()) {
                    mCurrentSuite = this.next(mCurrentSuite);
                }
            }
            catch(Exception e) {
                telemetry.addLine(e.getMessage());
            }
        }

    }

    private void launch(Suite suite) throws NoSuchFieldException, IllegalAccessException {
        if(suite == Suite.A) { this.aTest(); }
        else {
            telemetry.addLine("Unknown suite " + suite);
            FtcDashboard.getInstance().getTelemetry().addLine("Unknown suite " + suite);
        }
    }

    private Suite next(Suite suite) {
        if(suite == Suite.A) { return Suite.B; }
        else if (suite == Suite.B) { return Suite.A; }
        return Suite.NONE;
    }

    private void aTest() throws NoSuchFieldException, IllegalAccessException {

        telemetry.addLine("--> A button test\n");
        FtcDashboard.getInstance().getTelemetry().addLine("--> A button test");

        telemetry.addData("A PRESSED",mGamepad2.buttons.a.pressed());
        telemetry.addData("A PRESSED ONCE",mGamepad2.buttons.a.pressedOnce());

    }



}
