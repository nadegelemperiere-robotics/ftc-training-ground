/* -------------------------------------------------------
   Copyright (c) [2024] FASNY
   All rights reserved
   -------------------------------------------------------
   Chain Train Manual Mode
   ------------------------------------------------------- */

package org.firstinspires.ftc.teamcode;

/* Robotcore includes */
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

/* Local includes */
import org.firstinspires.ftc.teamcode.robots.MecanumDriveTrain;

@TeleOp()
public class MecanumDriveTrainManualMode extends OpMode {
        /** Class managing configuration and control of a basic chain train **/

        MecanumDriveTrain robot = new MecanumDriveTrain(); /** Chain train control object **/

        @Override
        public void init(){
                robot.init(hardwareMap, gamepad1, telemetry);
                telemetry.addData("TeleOp - ","Init done");
        }

        @Override
        public void loop() {
                telemetry.addData("TeleOp", "Updating robot");
                robot.update();
        }

}