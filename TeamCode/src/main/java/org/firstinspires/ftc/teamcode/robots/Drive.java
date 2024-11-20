/* -------------------------------------------------------
   Copyright (c) [2024] FASNY
   All rights reserved
   -------------------------------------------------------
   Robot drive initialization ( wheels motor + imu)
   ------------------------------------------------------- */

package org.firstinspires.ftc.teamcode.robots;

/* System includes */
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

/* Qualcomm includes */
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;

/* Ftc Controller includes */
import org.firstinspires.ftc.robotcore.external.Telemetry;

/* Roadrunner includes */
import com.acmerobotics.roadrunner.ftc.Encoder;
import com.acmerobotics.roadrunner.ftc.OverflowEncoder;
import com.acmerobotics.roadrunner.ftc.RawEncoder;
import com.acmerobotics.roadrunner.ftc.LazyImu;

/* Local includes */
import org.firstinspires.ftc.teamcode.functions.Configuration;
import org.firstinspires.ftc.teamcode.tools.Angles;


public class Drive {

    protected LazyImu                 m_imu;

    protected Map<String, DcMotorEx>  m_wheels;
    protected Map<String, Encoder>    m_encoders;
    protected Map<String, Double>     m_ticks_per_rotation;

    protected double                  m_fwd_in_per_ticks;
    protected double                  m_lat_in_per_ticks;
    protected double                  m_track_width_ticks;
    protected double                  m_ks;
    protected double                  m_ka;
    protected double                  m_kv;

    protected Telemetry               m_logger;

    public Drive(Telemetry logger) {
        m_logger = logger;
        m_wheels = new HashMap<String,DcMotorEx>();
        m_ticks_per_rotation = new HashMap<String,Double>();
        m_encoders = new HashMap<String,Encoder>();
    }
    public Drive() {
        m_logger = null;
        m_wheels = new HashMap<String,DcMotorEx>();
        m_ticks_per_rotation = new HashMap<String,Double>();
        m_encoders = new HashMap<String,Encoder>();
    }
    public void configure(HardwareMap map, Configuration config) throws IOException {

        /* Reinitialization */
        m_wheels.clear();
        m_encoders.clear();
        m_ticks_per_rotation.clear();

        /* Motors configuration */
        List<Component> motors = config.get("type","motor");
        for (Component component : motors) {
            if(component.m_name.contains("-wheel")) {

                m_wheels.put(component.m_name, map.get(DcMotorEx.class, component.m_hardware));
                m_encoders.put(component.m_name, new OverflowEncoder(new RawEncoder(m_wheels.get(component.m_name))));

                if (component.m_reverse.equals("true")) {
                    if(m_logger != null) { m_logger.addLine("Reversing wheel " + component.m_name);}
                    m_wheels.get(component.m_name).setDirection(DcMotor.Direction.REVERSE);
                    m_encoders.get(component.m_name).setDirection(DcMotor.Direction.REVERSE);
                }
                else {
                    m_wheels.get(component.m_name).setDirection(DcMotor.Direction.FORWARD);
                    m_encoders.get(component.m_name).setDirection(DcMotor.Direction.FORWARD);
                }

            }
        }

        for (Map.Entry<String, DcMotorEx> wheel : m_wheels.entrySet()) {
            m_ticks_per_rotation.put(wheel.getKey(), wheel.getValue().getMotorType().getTicksPerRev());
        }

        /* IMU configuration */
        List<Component> imu = config.get("type","imu");

        RevHubOrientationOnRobot.LogoFacingDirection logo_direction = RevHubOrientationOnRobot.LogoFacingDirection.UP;
        RevHubOrientationOnRobot.UsbFacingDirection usb_direction = RevHubOrientationOnRobot.UsbFacingDirection.FORWARD;;

        Angles.Direction usb_orientation = Angles.determineFacingDirection(
                imu.get(0).m_orientation.get(0),
                imu.get(0).m_orientation.get(1),
                imu.get(0).m_orientation.get(2));
        Angles.Direction logo_orientation = Angles.determineFacingDirection(
                imu.get(0).m_orientation.get(0),
                imu.get(0).m_orientation.get(1) + 90,
                imu.get(0).m_orientation.get(2));

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

        m_imu = new LazyImu(map, imu.get(0).m_hardware, new RevHubOrientationOnRobot(
                logo_direction, usb_direction));
        m_imu.get().resetYaw();

        m_fwd_in_per_ticks = config.fwdTicks();
        m_lat_in_per_ticks = config.latTicks();
        m_track_width_ticks = config.trackTicks();
        m_ks = config.kS();
        m_kv = config.kV();
        m_ka = config.kA();

    }

    public DcMotorEx wheel(String position) {
        DcMotorEx result = null;
        if(m_wheels.containsKey(position)) {
            result = m_wheels.get(position);
        }
        return result;
    }
    public Encoder encoder(String position) {
        Encoder result = null;
        if(m_encoders.containsKey(position)) {
            result = m_encoders.get(position);
        }
        return result;
    }
    public LazyImu imu() {
        return m_imu;
    }

    public double fwdTicks() {
        return m_fwd_in_per_ticks;
    }
    public double latTicks() {
        return m_lat_in_per_ticks;
    }
    public double trackTicks() {
        return m_track_width_ticks;
    }
    public double kS() {
        return m_ks;
    }
    public double kV() {
        return m_kv;
    }
    public double kA() {
        return m_ka;
    }

}
