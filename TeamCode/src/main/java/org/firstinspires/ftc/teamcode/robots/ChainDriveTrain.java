/* -------------------------------------------------------
   Copyright (c) [2024] FASNY
   All rights reserved
   -------------------------------------------------------
   Chain Train configuration and control
   ------------------------------------------------------- */

package org.firstinspires.ftc.teamcode.robots;

import org.firstinspires.ftc.robotcore.external.Telemetry;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Gamepad;

public class ChainDriveTrain {
    /** Class managing configuration and control of a basic chain train **/

    private     double      m_right_ticks_per_rotation = 0;
    private     double      m_left_ticks_per_rotation = 0;
    private     DcMotor     m_right_wheels;
    private     DcMotor     m_left_wheels;
    private     Boolean     m_is_started = false;

    private     Gamepad     m_controller;

    private     Telemetry   m_logger;

    /** Initialize robot **/
    public void init(HardwareMap map, Gamepad gamepad, Telemetry logger)
    {
        m_right_wheels = map.dcMotor.get("rightWheels");
        m_left_wheels = map.dcMotor.get("leftWheels");
        m_right_wheels.resetDeviceConfigurationForOpMode();
        m_left_wheels.resetDeviceConfigurationForOpMode();
        m_right_wheels.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        m_left_wheels.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        m_right_ticks_per_rotation = m_right_wheels.getMotorType().getTicksPerRev();
        m_left_ticks_per_rotation = m_left_wheels.getMotorType().getTicksPerRev();

        m_controller = gamepad;

        m_logger = logger;
    }

    /** Starting robot with right motor at power 1 **/
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

            m_right_wheels.setPower(right_wheel_velocity);
            m_left_wheels.setPower(left_wheel_velocity);

            m_logger.addData("ChainTrain","vAngular:" + angular_velocity);
            m_logger.addData("ChainTrain","vForward:" + forward_velocity);
            m_logger.addData("ChainTrain","vLeft" + left_wheel_velocity);
            m_logger.addData("ChainTrain","vRight" + right_wheel_velocity);
        }
        else
        {
            m_right_wheels.setPower(0);
            m_left_wheels.setPower(0);
        }
    }


}