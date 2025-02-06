/* -------------------------------------------------------
   Copyright (c) [2025] Nadege LEMPERIERE
   All rights reserved
   -------------------------------------------------------
   ImuComponent centralized built in imu intialization
   ------------------------------------------------------- */

package org.firstinspires.ftc.teamcode.core.components.imus;

/* System includes */
import java.util.Map;

/* Qualcomm includes */
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.robotcore.hardware.HardwareMap;

/* FTC Controller */
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

/* Configuration includes */
import org.firstinspires.ftc.robotcore.external.navigation.AngularVelocity;
import org.firstinspires.ftc.teamcode.core.configuration.ConfImu;

/* Tools includes */
import org.firstinspires.ftc.teamcode.core.tools.Logger;


public class ImuComponent {

    Map<String, RevHubOrientationOnRobot.LogoFacingDirection> sString2LogoFacing = Map.of(
            "up", RevHubOrientationOnRobot.LogoFacingDirection.UP,
            "down",RevHubOrientationOnRobot.LogoFacingDirection.DOWN,
            "right",RevHubOrientationOnRobot.LogoFacingDirection.RIGHT,
            "left",RevHubOrientationOnRobot.LogoFacingDirection.LEFT,
            "backward",RevHubOrientationOnRobot.LogoFacingDirection.BACKWARD,
            "forward",RevHubOrientationOnRobot.LogoFacingDirection.FORWARD
    );

    Map<String, RevHubOrientationOnRobot.UsbFacingDirection> sString2UsbFacing = Map.of(
            "up", RevHubOrientationOnRobot.UsbFacingDirection.UP,
            "down",RevHubOrientationOnRobot.UsbFacingDirection.DOWN,
            "right",RevHubOrientationOnRobot.UsbFacingDirection.RIGHT,
            "left",RevHubOrientationOnRobot.UsbFacingDirection.LEFT,
            "backward",RevHubOrientationOnRobot.UsbFacingDirection.BACKWARD,
            "forward",RevHubOrientationOnRobot.UsbFacingDirection.FORWARD
    );

    static ImuComponent factory(ConfImu config, HardwareMap map, Logger logger) {

        return new ImuComponent(config, map, logger);

    }

    Logger          mLogger;

    boolean         mReady;
    String          mName;


    IMU             mImu;

    double          mHeadingOffset;

    /* ----------------------- Constructors ------------------------ */
    public ImuComponent(ConfImu config, HardwareMap map, Logger logger) {

        mReady  = true;

        mLogger = logger;

        mName   = config.name();

        mImu = null;
        mHeadingOffset = 0;

        if(config.isValid()) {
            mImu = map.tryGet(IMU.class, config.mapName());
            if(mImu != null) {

                RevHubOrientationOnRobot.LogoFacingDirection logo = RevHubOrientationOnRobot.LogoFacingDirection.UP;
                if(sString2LogoFacing.containsKey(config.logo())) {
                   logo = sString2LogoFacing.get(config.logo());
                }
                RevHubOrientationOnRobot.UsbFacingDirection usb = RevHubOrientationOnRobot.UsbFacingDirection.RIGHT;
                if(sString2UsbFacing.containsKey(config.usb())) {
                    usb = sString2UsbFacing.get(config.usb());
                }
                RevHubOrientationOnRobot RevOrientation = new RevHubOrientationOnRobot(logo,usb);
                mImu.initialize(new IMU.Parameters(RevOrientation));
                mImu.resetYaw();
            }
        }

        if(mImu  == null) { mReady = false; }
    }

    /* --------------------- Custom functions ---------------------- */

    double                      headingOffset() { return mHeadingOffset;    }
    boolean                     isReady()       { return mReady;            }
    String                      getName()       { return mName;             }
    double                      heading()       {
        double result = mImu.getRobotYawPitchRollAngles().getYaw(AngleUnit.RADIANS);
        result += mHeadingOffset;
        return result;
    }
    double                      headingVelocity() {
        AngularVelocity velocity = mImu.getRobotAngularVelocity(AngleUnit.RADIANS);
        return velocity.zRotationRate;
    }

    void                        headingOffset( double offset ) {
        mHeadingOffset = offset;
    }

}
