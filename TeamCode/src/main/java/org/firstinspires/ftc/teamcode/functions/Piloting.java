/* -------------------------------------------------------
   Copyright (c) [2024] FASNY
   All rights reserved
   -------------------------------------------------------
   Robot piloting logic
   ------------------------------------------------------- */

package org.firstinspires.ftc.teamcode.functions;

/* System includes */
import java.io.IOException;

/* Qualcomm includes */
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Gamepad;

/* Ftc Controller includes */
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.Telemetry;

/* Local includes */
import org.firstinspires.ftc.teamcode.robot.Drive;

public class Piloting {
    /** Class managing configuration and control of a basic chain train **/

    public enum Centricity {
        FIELD,
        ROBOT
    };

    protected Drive                   m_drive;

    protected Boolean                 m_has_been_released = false;
    protected Boolean                 m_is_started = false;

    protected Gamepad                 m_controller;
    protected Centricity              m_centricity;

    protected Telemetry               m_logger;
    
    public  static  Piloting    Builder(Configuration config, Centricity centricity, Telemetry logger) throws IOException {

        Piloting result = new Piloting();

        if (config.drive().equals("mecanum")) {
            result = new MecanumPiloting(logger, centricity);
        }
        else if (config.drive().equals("tank")) {
            result = new TankPiloting(logger, centricity);
        }

        return result;
    }

    public Piloting() {
    }


    public void configure(HardwareMap map, Gamepad gamepad, Configuration config) throws IOException {
    }

    public void update() {

    }


}

class TankPiloting extends Piloting {
    
    public TankPiloting(Telemetry logger, Centricity centricity) throws IOException {

        m_logger = logger;
        m_drive = new Drive(logger);
        m_centricity = centricity;
    }

    @Override
    public void configure(HardwareMap map, Gamepad gamepad, Configuration config) throws IOException {

        m_drive.configure(map,config);
        m_controller = gamepad;

        if(m_drive.wheel("left") == null) {
            m_logger.addLine("Piloting - Missing left wheel for omniwheels configuration");
            throw new IOException("Piloting configuration failed");
        }
        if(m_drive.wheel("right") == null) {
            m_logger.addLine("Piloting - Missing right  wheel for omniwheels configuration");
            throw new IOException("Piloting configuration failed");
        }
        
    }

    @Override
    public void update(){

        if (m_controller.a) {
            if(m_has_been_released) {
                m_is_started = !m_is_started;
                m_has_been_released = false;
            }
        }
        else {
            m_has_been_released = true;
        }

        if(m_is_started) {

            double angular_velocity     =  m_controller.left_stick_x;
            double forward_velocity     = -m_controller.left_stick_y;
            double right_wheel_velocity = (forward_velocity + angular_velocity);
            double left_wheel_velocity  = -(forward_velocity - angular_velocity);
            double norm = Math.abs(forward_velocity) + Math.abs(angular_velocity);
            double max = Math.max(Math.abs(forward_velocity),Math.abs(angular_velocity));
            if (norm != 0) {
                right_wheel_velocity = right_wheel_velocity / norm * max;
                left_wheel_velocity = left_wheel_velocity / norm * max;
            }

            m_drive.wheel("right-wheel").setPower(right_wheel_velocity);
            m_drive.wheel("left-wheel").setPower(left_wheel_velocity);

            m_logger.addLine(String.format("\n===> PILOTING "));
            m_logger.addLine(String.format("AFLF  %6.1f %6.1f %6.1f %6.1f",
                    angular_velocity,
                    forward_velocity,
                    left_wheel_velocity,
                    right_wheel_velocity));
        }
        else
        {
            m_drive.wheel("right-wheel").setPower(0);
            m_drive.wheel("left-wheel").setPower(0);

        }
    }

}

class MecanumPiloting extends Piloting {

    public MecanumPiloting(Telemetry logger, Centricity centricity) throws IOException {

        m_logger  = logger;
        m_drive   = new Drive(logger);
        m_centricity = centricity;
    }

    @Override
    public void configure(HardwareMap map, Gamepad gamepad, Configuration config) throws IOException {

        m_drive.configure(map,config);
        m_controller = gamepad;

        if (m_drive.wheel("left-front-wheel") == null) {
            m_logger.addLine("Piloting - Missing left front wheel for omniwheels configuration");
            throw new IOException("Piloting configuration failed");
        }
        if (m_drive.wheel("right-front-wheel") == null) {
            m_logger.addLine("Piloting - Missing right front wheel for omniwheels configuration");
            throw new IOException("Piloting configuration failed");
        }
        if (m_drive.wheel("left-back-wheel") == null) {
            m_logger.addLine("Piloting - Missing left back wheel for omniwheels configuration");
            throw new IOException("Piloting configuration failed");
        }
        if (m_drive.wheel("right-back-wheel") == null) {
            m_logger.addLine("Piloting - Missing right back wheel for omniwheels configuration");
            throw new IOException("Piloting configuration failed");
        }

        m_drive.wheel("left-back-wheel").setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        m_drive.wheel("left-front-wheel").setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        m_drive.wheel("right-back-wheel").setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        m_drive.wheel("right-front-wheel").setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

    }

    @Override
    public void update(){

        if (m_controller.a) {
            if(m_has_been_released) {
                m_is_started = !m_is_started;
                m_has_been_released = false;
            }
        }
        else {
            m_has_been_released = true;
        }

        if(m_is_started) {

            m_logger.addLine(String.format("\n===> PILOTING "));

            double y = -m_controller.left_stick_y; // Forward/Back (Y-axis is reversed)
            double x = m_controller.left_stick_x * 1.1;  // Strafe Left/Right
            double rotation = m_controller.right_stick_x; // Rotate Left/Right

            if (m_centricity == Centricity.FIELD) {
                double botHeading = m_drive.imu().get().getRobotYawPitchRollAngles().getYaw(AngleUnit.RADIANS);
                m_logger.addLine(String.format("\n===> HD %6.1f",botHeading));
                // Rotate the movement direction counter to the bot's rotation
                double rotX = x * Math.cos(-botHeading) - y * Math.sin(-botHeading);
                double rotY = x * Math.sin(-botHeading) + y * Math.cos(-botHeading);
                x = rotX;
                y = rotY;
            }

            // Calculate the power for each motor
            double frontLeftPower = y + x + rotation;
            double frontRightPower = y - x - rotation;
            double backLeftPower = y - x + rotation;
            double backRightPower = y + x - rotation;

            // Normalize the motor power values so no value exceeds 1.0
            double maxPower = Math.max(Math.abs(y) + Math.abs(x) + Math.abs(rotation), 1);

            frontLeftPower /= maxPower;
            frontRightPower /= maxPower;
            backLeftPower /= maxPower;
            backRightPower /= maxPower;

            // Set the motor powers
            m_drive.wheel("left-front-wheel").setPower(frontLeftPower);
            m_drive.wheel("right-front-wheel").setPower(frontRightPower);
            m_drive.wheel("left-back-wheel").setPower(backLeftPower);
            m_drive.wheel("right-back-wheel").setPower(backRightPower);


            m_logger.addLine(String.format("PWR LF RF LB RB  %6.1f %6.1f %6.1f %6.1f",
                    frontLeftPower,
                    frontRightPower,
                    backLeftPower,
                    backRightPower));
            m_logger.addLine(String.format("ECP LF RF LB RB  %d %d %d %d",
                    m_drive.encoder("left-front-wheel").getPositionAndVelocity().position,
                    m_drive.encoder("right-front-wheel").getPositionAndVelocity().position,
                    m_drive.encoder("left-back-wheel").getPositionAndVelocity().position,
                    m_drive.encoder("right-back-wheel").getPositionAndVelocity().position));
            m_logger.addLine(String.format("ECV LF RF LB RB  %d %d %d %d",
                    m_drive.encoder("left-front-wheel").getPositionAndVelocity().velocity,
                    m_drive.encoder("right-front-wheel").getPositionAndVelocity().velocity,
                    m_drive.encoder("left-back-wheel").getPositionAndVelocity().velocity,
                    m_drive.encoder("right-back-wheel").getPositionAndVelocity().velocity));

        }
        else
        {
            m_drive.wheel("left-front-wheel").setPower(0);
            m_drive.wheel("right-front-wheel").setPower(0);
            m_drive.wheel("left-back-wheel").setPower(0);
            m_drive.wheel("right-back-wheel").setPower(0);
        }
    }

}
