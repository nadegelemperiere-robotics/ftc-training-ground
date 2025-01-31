/* -------------------------------------------------------
   Copyright (c) [2025] Nadege LEMPERIERE
   All rights reserved
   -------------------------------------------------------
   Localization interface providing robot precise position
   and orientation on the mat
   ------------------------------------------------------- */

package org.firstinspires.ftc.teamcode.core.components;

/* Json includes */
import org.json.JSONObject;

/* Qualcomm includes */
import com.acmerobotics.roadrunner.Pose2d;
import com.qualcomm.robotcore.hardware.HardwareMap;

/* Tools includes */
import org.firstinspires.ftc.teamcode.core.tools.Logger;

/* Configuration includes */
import org.firstinspires.ftc.teamcode.core.configuration.ConfOdometer;

public interface OdometerComponent {

    static OdometerComponent factory(ConfOdometer config, HardwareMap hwMap, Logger logger) {

        OdometerComponent result = null;

        switch (config.name()) {
            case "otos":
                result = new OpticalTrackingOdometer(config, hwMap, logger); break;
            case "2deadwheels":
                result = new TwoDeadWheelsOdometer(config, hwMap, logger); break;
            case "3deadwheels":
                result = new ThreeDeadWheelsOdometer(config, hwMap, logger); break;
        }

        return result;

    }

    /* --------------- Accessors -------------- */
    boolean isReady();
    void    setPose(Pose2d current);
    Pose2d  getPose();
    void    log();

    /* ------------- Localization ------------- */
    void    update();

}