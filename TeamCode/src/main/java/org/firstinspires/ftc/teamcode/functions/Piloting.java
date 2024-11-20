/* -------------------------------------------------------
   Copyright (c) [2024] FASNY
   All rights reserved
   -------------------------------------------------------
   Robot piloting logic
   ------------------------------------------------------- */

package org.firstinspires.ftc.teamcode.functions;

/* System includes */
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

/* Qualcomm includes */
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;

/* Ftc Controller includes */
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.robots.Component;

/* Local includes */
import org.firstinspires.ftc.teamcode.tools.Angles;

public class Piloting {
    /** Class managing configuration and control of a basic chain train **/

    public enum Centricity {
        FIELD,
        ROBOT
    };

    protected Map<String, Double>     m_ticks_per_rotation;
    protected List<Component>         m_motors;
    protected Component               m_imu_c;
    protected Map<String, DcMotor>    m_wheels;
    protected IMU                     m_imu;

    protected Boolean                 m_has_been_released = false;
    protected Boolean                 m_is_started = false;

    protected Gamepad                 m_controller;
    protected Centricity              m_centricity;

    protected Telemetry               m_logger;
    
    public  static  Piloting    Builder(Configuration config, Centricity centricity, Telemetry logger) throws IOException {

        Piloting result = new Piloting();

        List<Component> wheels = new ArrayList<Component>();

        List<Component> motors = config.get("type","motor");
        for (Component component : motors) {
            if(component.m_name.contains("-wheel")) {
                wheels.add(component);
            }
        }

        List<Component> imu = config.get("type","imu");

        if(wheels.size() != 2 && wheels.size() != 4) {
            logger.addLine("Piloting - Can not manage robots with " + wheels.size() + " wheels");
            throw new IOException("Piloting configuration failed");
        }
        if (wheels.size() == 4) {
            logger.addLine("Piloting - Creating MecanumPiloting");
            result = new MecanumPiloting(logger,centricity,wheels, imu.get(0));
        }
        if (wheels.size() == 2) {
            logger.addLine("Piloting - Creating TankPiloting");
            result = new TankPiloting(logger,centricity,wheels, imu.get(0));
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
    
    public TankPiloting(Telemetry logger, Centricity centricity, List<Component> motors, Component Imu) {

        m_logger = logger;
        m_motors = motors;
        m_imu_c  = Imu;
        m_centricity = centricity;
        m_wheels = new HashMap<String,DcMotor>();
        m_ticks_per_rotation = new HashMap<String,Double>();
    }

    @Override
    public void configure(HardwareMap map, Gamepad gamepad, Configuration config) throws IOException {

        m_wheels.clear();
        m_controller = gamepad;
        m_imu = map.get(IMU.class, "imu");

        for (Component component : m_motors) {
            m_wheels.put(component.m_name, map.dcMotor.get(component.m_hardware));
            if (component.m_reverse.equals("true")) {
                m_wheels.get(component.m_name).setDirection(DcMotor.Direction.REVERSE);
            }
            else {
                m_wheels.get(component.m_name).setDirection(DcMotor.Direction.FORWARD);
            }
        }

        if(!m_wheels.containsKey("left")) {
            m_logger.addLine("Piloting - Missing left wheel for omniwheels configuration");
            throw new IOException("Piloting configuration failed");
        }
        if(!m_wheels.containsKey("right")) {
            m_logger.addLine("Piloting - Missing right  wheel for omniwheels configuration");
            throw new IOException("Piloting configuration failed");
        }
        
        for (Map.Entry<String, DcMotor> wheel : m_wheels.entrySet()) {
            wheel.getValue().resetDeviceConfigurationForOpMode();
            wheel.getValue().setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            m_ticks_per_rotation.put(wheel.getKey(), wheel.getValue().getMotorType().getTicksPerRev());
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

            m_wheels.get("right-wheel").setPower(right_wheel_velocity);
            m_wheels.get("left-wheel").setPower(left_wheel_velocity);

            m_logger.addLine(String.format("\n===> PILOTING "));
            m_logger.addLine(String.format("AFLF  %6.1f %6.1f %6.1f %6.1f",
                    angular_velocity,
                    forward_velocity,
                    left_wheel_velocity,
                    right_wheel_velocity));
        }
        else
        {
            for (DcMotor wheel : m_wheels.values())
            {
                wheel.setPower(0);
            }
        }
    }

}

class MecanumPiloting extends Piloting {

    public MecanumPiloting(Telemetry logger, Centricity centricity, List<Component> motors, Component Imu) {
        m_logger  = logger;
        m_motors  = motors;
        m_imu_c   = Imu;
        m_centricity = centricity;
        m_wheels = new HashMap<String,DcMotor>();
        m_ticks_per_rotation = new HashMap<String,Double>();
    }

    @Override
    public void configure(HardwareMap map, Gamepad gamepad, Configuration config) throws IOException {

        m_wheels.clear();
        m_controller = gamepad;

        m_imu = map.get(IMU.class, m_imu_c.m_hardware);
        RevHubOrientationOnRobot.LogoFacingDirection logo_direction = RevHubOrientationOnRobot.LogoFacingDirection.UP;
        RevHubOrientationOnRobot.UsbFacingDirection usb_direction = RevHubOrientationOnRobot.UsbFacingDirection.FORWARD;;

        Angles.Direction usb_orientation = Angles.determineFacingDirection(
                m_imu_c.m_orientation.get(0),
                m_imu_c.m_orientation.get(1),
                m_imu_c.m_orientation.get(2));

        Angles.Direction logo_orientation = Angles.determineFacingDirection(
                m_imu_c.m_orientation.get(0),
                m_imu_c.m_orientation.get(1) + 90,
                m_imu_c.m_orientation.get(2));

        if (logo_orientation == Angles.Direction.UP)     { logo_direction = RevHubOrientationOnRobot.LogoFacingDirection.UP;}
        if (logo_orientation == Angles.Direction.DOWN)   { logo_direction = RevHubOrientationOnRobot.LogoFacingDirection.DOWN;}
        if (logo_orientation == Angles.Direction.LEFT)   { logo_direction = RevHubOrientationOnRobot.LogoFacingDirection.LEFT;}
        if (logo_orientation == Angles.Direction.RIGHT)  { logo_direction = RevHubOrientationOnRobot.LogoFacingDirection.RIGHT;}
        if (logo_orientation == Angles.Direction.FRONT)  { logo_direction = RevHubOrientationOnRobot.LogoFacingDirection.FORWARD;}
        if (logo_orientation == Angles.Direction.BACK)   { logo_direction = RevHubOrientationOnRobot.LogoFacingDirection.BACKWARD;}
        if (usb_orientation == Angles.Direction.UP)      { usb_direction = RevHubOrientationOnRobot.UsbFacingDirection.UP;}
        if (usb_orientation == Angles.Direction.DOWN)    { usb_direction = RevHubOrientationOnRobot.UsbFacingDirection.DOWN;}
        if (usb_orientation == Angles.Direction.LEFT)    { usb_direction = RevHubOrientationOnRobot.UsbFacingDirection.LEFT;}
        if (usb_orientation == Angles.Direction.RIGHT)   { usb_direction = RevHubOrientationOnRobot.UsbFacingDirection.RIGHT;}
        if (usb_orientation == Angles.Direction.FRONT)   { usb_direction = RevHubOrientationOnRobot.UsbFacingDirection.FORWARD;}
        if (usb_orientation == Angles.Direction.BACK)    { usb_direction = RevHubOrientationOnRobot.UsbFacingDirection.BACKWARD;}

        IMU.Parameters parameters = new IMU.Parameters(
            new RevHubOrientationOnRobot(
                logo_direction,
                usb_direction));
        m_imu.initialize(parameters);
        m_imu.resetYaw();

        for (Component component : m_motors) {
            m_wheels.put(component.m_name, map.dcMotor.get(component.m_hardware));
            if (component.m_reverse.equals("true")) {
                m_wheels.get(component.m_name).setDirection(DcMotor.Direction.REVERSE);
            }
            else {
                m_wheels.get(component.m_name).setDirection(DcMotor.Direction.FORWARD);
            }
        }

        if (!m_wheels.containsKey("left-front-wheel")) {
            m_logger.addLine("Piloting - Missing left front wheel for omniwheels configuration");
            throw new IOException("Piloting configuration failed");
        }
        if (!m_wheels.containsKey("right-front-wheel")) {
            m_logger.addLine("Piloting - Missing right front wheel for omniwheels configuration");
            throw new IOException("Piloting configuration failed");
        }
        if (!m_wheels.containsKey("left-back-wheel")) {
            m_logger.addLine("Piloting - Missing left back wheel for omniwheels configuration");
            throw new IOException("Piloting configuration failed");
        }
        if (!m_wheels.containsKey("right-back-wheel")) {
            m_logger.addLine("Piloting - Missing right back wheel for omniwheels configuration");
            throw new IOException("Piloting configuration failed");
        }


        for (Map.Entry<String, DcMotor> wheel : m_wheels.entrySet()) {
            wheel.getValue().resetDeviceConfigurationForOpMode();
            wheel.getValue().setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            m_ticks_per_rotation.put(wheel.getKey(), wheel.getValue().getMotorType().getTicksPerRev());
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

            m_logger.addLine(String.format("\n===> PILOTING "));
            

            double y = -m_controller.left_stick_y; // Forward/Back (Y-axis is reversed)
            double x = m_controller.left_stick_x * 1.1;  // Strafe Left/Right
            double rotation = m_controller.right_stick_x; // Rotate Left/Right

            if (m_centricity == Centricity.FIELD) {
                double botHeading = m_imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.RADIANS);
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
            m_wheels.get("left-front-wheel").setPower(frontLeftPower);
            m_wheels.get("right-front-wheel").setPower(frontRightPower);
            m_wheels.get("left-back-wheel").setPower(-backLeftPower);
            m_wheels.get("right-back-wheel").setPower(backRightPower);


            m_logger.addLine(String.format("LF RF LB RB  %6.1f %6.1f %6.1f %6.1f",
                    frontLeftPower,
                    frontRightPower,
                    -backLeftPower,
                    backRightPower));

        }
        else
        {
            for (DcMotor wheel : m_wheels.values())
            {
                wheel.setPower(0);
            }
        }
    }

}
