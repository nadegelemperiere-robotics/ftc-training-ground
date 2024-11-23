package org.firstinspires.ftc.teamcode.roadrunner.tuning;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.PoseVelocity2d;
import com.acmerobotics.roadrunner.Vector2d;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.teamcode.functions.Configuration;
import org.firstinspires.ftc.teamcode.robot.Drive;
import org.firstinspires.ftc.teamcode.roadrunner.Drawing;
import org.firstinspires.ftc.teamcode.roadrunner.MecanumDrive;
import org.firstinspires.ftc.teamcode.roadrunner.TankDrive;
import org.firstinspires.ftc.teamcode.configurations.MecanumDriveTrain;

import java.io.IOException;

@TeleOp(name = "Localization Test", group = "RoadRunner")
public class LocalizationTest extends LinearOpMode {
    @Override
    public void runOpMode() throws InterruptedException {
        telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());

        /* Parse robot configuration */
        Configuration config = new Configuration();
        Drive drive = new Drive();
        try {
            config.read(MecanumDriveTrain.s_Configuration);
            drive.configure(hardwareMap,config);
        } catch (Exception e) {

        }

        if (TuningOpModes.DRIVE_CLASS.equals(MecanumDrive.class)) {
            MecanumDrive md = new MecanumDrive(hardwareMap, new Pose2d(0, 0, 0),drive);

            waitForStart();

            while (opModeIsActive()) {
                md.setDrivePowers(new PoseVelocity2d(
                        new Vector2d(
                                -gamepad1.left_stick_y,
                                -gamepad1.left_stick_x
                        ),
                        -gamepad1.right_stick_x
                ));

                md.updatePoseEstimate();

                double period = md.timer.update();
                telemetry.addData("ms", period * 1000);
                if(md.last_pose != null) {
                    telemetry.addData("delta_x", md.pose.position.x - md.last_pose.position.x);
                }
                telemetry.addData("x", md.pose.position.x);
                telemetry.addData("y", md.pose.position.y);
                telemetry.addData("heading (deg)", Math.toDegrees(md.pose.heading.toDouble()));
                telemetry.addData("heading mes (deg)",md.lazyImu.get().getRobotYawPitchRollAngles().getYaw(AngleUnit.DEGREES));

                telemetry.update();

                md.last_pose = md.pose;
                TelemetryPacket packet = new TelemetryPacket();
                packet.fieldOverlay().setStroke("#3F51B5");
                Drawing.drawRobot(packet.fieldOverlay(), md.pose);
                FtcDashboard.getInstance().sendTelemetryPacket(packet);
            }
        } else if (TuningOpModes.DRIVE_CLASS.equals(TankDrive.class)) {
            TankDrive td = new TankDrive(hardwareMap, new Pose2d(0, 0, 0),drive);

            waitForStart();

            while (opModeIsActive()) {
                td.setDrivePowers(new PoseVelocity2d(
                        new Vector2d(
                                -gamepad1.left_stick_y,
                                0.0
                        ),
                        -gamepad1.right_stick_x
                ));

                td.updatePoseEstimate();

                telemetry.addData("x", td.pose.position.x);
                telemetry.addData("y", td.pose.position.y);
                telemetry.addData("heading (deg)", Math.toDegrees(td.pose.heading.toDouble()));
                telemetry.update();

                TelemetryPacket packet = new TelemetryPacket();
                packet.fieldOverlay().setStroke("#3F51B5");
                Drawing.drawRobot(packet.fieldOverlay(), td.pose);
                FtcDashboard.getInstance().sendTelemetryPacket(packet);
            }
        } else {
            throw new RuntimeException();
        }
    }
}
