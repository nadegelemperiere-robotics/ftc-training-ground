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
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Gamepad;

/* Ftc Controller includes */
import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.robots.Component;

public class Piloting {
    /** Class managing configuration and control of a basic chain train **/

    protected Map<String, Double>     m_ticks_per_rotation;
    protected List<Component>         m_motors;
    protected Map<String, DcMotor>    m_wheels;

    protected Boolean                 m_is_started = false;

    protected Gamepad                 m_controller;

    protected Telemetry               m_logger;
    
    public  static  Piloting    Builder(Configuration config, Telemetry logger) throws IOException {

        Piloting result = new Piloting();

        List<Component> wheels = new ArrayList<Component>();

        List<Component> motors = config.get("type","motor");
        for (Component component : motors) {
            if(component.m_hardware.contains("Wheel")) {
                wheels.add(component);
            }
        }

        if(wheels.size() != 2 && wheels.size() != 4) {
            logger.addLine("Piloting - Can not manage robots with " + wheels.size() + " wheels");
            throw new IOException("Piloting configuration failed");
        }
        if (wheels.size() == 4) {
            logger.addLine("Piloting - Creating FourWheelsPiloting");
            result = new FourWheelsPiloting(logger,wheels);
        }
        if (wheels.size() == 2) {
            logger.addLine("Piloting - Creating TwoWheelsPiloting");
            result = new TwoWheelsPiloting(logger,wheels);
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

class TwoWheelsPiloting extends Piloting {
    
    public TwoWheelsPiloting(Telemetry logger, List<Component> motors) {

        m_logger = logger;
        m_motors = motors;
        m_wheels = new HashMap<String,DcMotor>();
        m_ticks_per_rotation = new HashMap<String,Double>();
    }

    @Override
    public void configure(HardwareMap map, Gamepad gamepad, Configuration config) throws IOException {

        m_wheels.clear();
        m_controller = gamepad;

        for (Component component : m_motors) {
            m_wheels.put(component.m_name, map.dcMotor.get(component.m_hardware));
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

        if (m_controller.a) { m_is_started = !m_is_started; }

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

            m_wheels.get("right").setPower(right_wheel_velocity);
            m_wheels.get("left").setPower(left_wheel_velocity);

            m_logger.addLine("Piloting - vAngular:" + angular_velocity);
            m_logger.addLine("Piloting - vForward:" + forward_velocity);
            m_logger.addLine("Piloting - vLeft" + left_wheel_velocity);
            m_logger.addLine("Piloting - vRight" + right_wheel_velocity);
        }
        else
        {
            m_wheels.get("right").setPower(0);
            m_wheels.get("left").setPower(0);
        }
    }

}

class FourWheelsPiloting extends Piloting {

    public FourWheelsPiloting(Telemetry logger, List<Component> motors) {
        m_logger  = logger;
        m_motors  = motors;
        m_wheels = new HashMap<String,DcMotor>();
        m_ticks_per_rotation = new HashMap<String,Double>();
    }

    @Override
    public void configure(HardwareMap map, Gamepad gamepad, Configuration config) throws IOException {

        m_wheels.clear();
        m_controller = gamepad;

        for (Component component : m_motors) {
            m_wheels.put(component.m_name, map.dcMotor.get(component.m_hardware));
        }

        if (!m_wheels.containsKey("left-front")) {
            m_logger.addLine("Piloting - Missing left front wheel for omniwheels configuration");
            throw new IOException("Piloting configuration failed");
        }
        if (!m_wheels.containsKey("right-front")) {
            m_logger.addLine("Piloting - Missing right front wheel for omniwheels configuration");
            throw new IOException("Piloting configuration failed");
        }
        if (!m_wheels.containsKey("left-back")) {
            m_logger.addLine("Piloting - Missing left back wheel for omniwheels configuration");
            throw new IOException("Piloting configuration failed");
        }
        if (!m_wheels.containsKey("right-back")) {
            m_logger.addLine("Piloting - Missing right back wheel for omniwheels configuration");
            throw new IOException("Piloting configuration failed");
        }

        m_wheels.get("left-front").setDirection(DcMotor.Direction.FORWARD);
        m_wheels.get("right-front").setDirection(DcMotor.Direction.REVERSE);
        m_wheels.get("left-back").setDirection(DcMotor.Direction.FORWARD);
        m_wheels.get("right-back").setDirection(DcMotor.Direction.REVERSE);

        for (Map.Entry<String, DcMotor> wheel : m_wheels.entrySet()) {
            wheel.getValue().resetDeviceConfigurationForOpMode();
            wheel.getValue().setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            m_ticks_per_rotation.put(wheel.getKey(), wheel.getValue().getMotorType().getTicksPerRev());
        }
    }

    @Override
    public void update(){

        if (m_controller.a) {
            m_is_started = !m_is_started;
        }

        if(m_is_started) {

            double y = -m_controller.left_stick_y; // Forward/Back (Y-axis is reversed)
            double x = m_controller.left_stick_x;  // Strafe Left/Right
            double rotation = m_controller.right_stick_x; // Rotate Left/Right

            // Calculate the power for each motor
            double frontLeftPower = y + x + rotation;
            double frontRightPower = y - x - rotation;
            double backLeftPower = y - x + rotation;
            double backRightPower = y + x - rotation;

            // Normalize the motor power values so no value exceeds 1.0
            double maxPower = Math.max(Math.abs(frontLeftPower), Math.max(Math.abs(frontRightPower),
                    Math.max(Math.abs(backLeftPower), Math.abs(backRightPower))));

            if (maxPower > 1.0) {
                frontLeftPower /= maxPower;
                frontRightPower /= maxPower;
                backLeftPower /= maxPower;
                backRightPower /= maxPower;
            }

            // Set the motor powers
            m_wheels.get("left-front").setPower(frontLeftPower);
            m_wheels.get("right-front").setPower(frontRightPower);
            m_wheels.get("left-back").setPower(-backLeftPower);
            m_wheels.get("right-back").setPower(backRightPower);

            m_logger.addData("MecanumDriveTrain","left-front:" + frontLeftPower);
            m_logger.addData("MecanumDriveTrain","right-front:" + frontRightPower);
            m_logger.addData("MecanumDriveTrain","left-back" + -backLeftPower);
            m_logger.addData("MecanumDriveTrain","right-back" + backRightPower);
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
