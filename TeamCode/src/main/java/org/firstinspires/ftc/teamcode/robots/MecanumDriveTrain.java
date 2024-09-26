/* -------------------------------------------------------
   Copyright (c) [2024] FASNY
   All rights reserved
   -------------------------------------------------------
   Chain Train configuration and control
   ------------------------------------------------------- */

package org.firstinspires.ftc.teamcode.robots;

/* System includes */
import java.util.Map;
import java.util.HashMap;

/* Qualcomm includes */
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Gamepad;

/* Ftc Controller includes */
import org.firstinspires.ftc.robotcore.external.Telemetry;

public class MecanumDriveTrain {
    /** Class managing configuration and control of a basic chain train **/

    private     Map<String, Double> m_ticks_per_rotation = new HashMap<String, Double>();
    private     Map<String, DcMotor> m_wheels = new HashMap<String, DcMotor>();

    private     Boolean     m_is_started = false;

    private     Gamepad     m_controller;

    private     Telemetry   m_logger;

    /** Initialize robot **/
    public void init(HardwareMap map, Gamepad gamepad, Telemetry logger)
    {

        m_controller = gamepad;
        m_logger = logger;

        // Gather wheels motors from configuration
        m_wheels.put("right-front", map.dcMotor.get("rightFrontWheel"));
        m_wheels.put("left-front", map.dcMotor.get("leftFrontWheel"));
        m_wheels.put("right-back", map.dcMotor.get("rightBackWheel"));
        m_wheels.put("left-back", map.dcMotor.get("leftBackWheel"));

        m_wheels.get("left-front").setDirection(DcMotor.Direction.FORWARD);
        m_wheels.get("right-front").setDirection(DcMotor.Direction.REVERSE);
        m_wheels.get("left-back").setDirection(DcMotor.Direction.FORWARD);
        m_wheels.get("right-back").setDirection(DcMotor.Direction.REVERSE);

        // Initialize wheels motors
        for (Map.Entry<String, DcMotor> wheel : m_wheels.entrySet()) {
            wheel.getValue().resetDeviceConfigurationForOpMode();
            wheel.getValue().setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            m_ticks_per_rotation.put(wheel.getKey(), wheel.getValue().getMotorType().getTicksPerRev());
        }
    }

    /** Starting robot with right motor at power 1 **/
    public void update(){

        if (m_controller.a) { m_is_started = !m_is_started; }

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

            m_logger.addData("ChainTrain","left-front:" + frontLeftPower);
            m_logger.addData("ChainTrain","right-front:" + frontRightPower);
            m_logger.addData("ChainTrain","left-back" + -backLeftPower);
            m_logger.addData("ChainTrain","right-back" + backRightPower);
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