/* -------------------------------------------------------
   Copyright (c) [2025] Nadege LEMPERIERE
   All rights reserved
   -------------------------------------------------------
   Logging manager
   ------------------------------------------------------- */

package org.firstinspires.ftc.teamcode.core.tools;

/* FTC Controller includes */
import org.firstinspires.ftc.robotcore.external.Telemetry;

/* ACME robotics includes */
import com.acmerobotics.dashboard.FtcDashboard;

public class Logger {

    public enum Target {
        DRIVER_STATION,
        DASHBOARD
    }

    // Loggers
    Telemetry       mDriverStation;
    FtcDashboard    mDashboard;


    // Constructors
    public Logger(Telemetry station, FtcDashboard dashboard) {
        mDriverStation = station;
        mDashboard = dashboard;
        if(mDashboard != null) {
            mDashboard.getTelemetry().log().setDisplayOrder(Telemetry.Log.DisplayOrder.NEWEST_FIRST);
        }
    }


    public void addLine(Target target, String line) {
        if (target == Target.DRIVER_STATION && mDriverStation != null) {
            mDriverStation.addLine(line);
        } else if (target == Target.DASHBOARD && mDashboard != null) {
            mDashboard.getTelemetry().addLine(line);
        }
    }

    public void addLine(String line) {
        if (mDriverStation != null) { mDriverStation.addLine(line); }
        if ( mDashboard != null)    { mDashboard.getTelemetry().addLine(line); }
    }

    public void addData(Target target, String first, String second) {
        if (target == Target.DRIVER_STATION && mDriverStation != null) {
            mDriverStation.addData(first, second);
        } else if (target == Target.DASHBOARD && mDashboard != null) {
            mDashboard.getTelemetry().addData(first, second);
        }
    }

    public void addData(String first, String second) {
        if (mDriverStation != null) { mDriverStation.addData(first, second); }
        if (mDashboard != null)     { mDashboard.getTelemetry().addData(first, second); }
    }

    public void update(Target target) {
        if (target == Target.DRIVER_STATION && mDriverStation != null) {
            mDriverStation.update();
        } else if (target == Target.DASHBOARD && mDashboard != null) {
            mDashboard.getTelemetry().update();
        }
    }

    public void update() {
        if (mDriverStation != null) { mDriverStation.update(); }
        if (mDashboard != null) { mDashboard.getTelemetry().update(); }
    }

    public void clear(Target target) {
        if (target == Target.DRIVER_STATION && mDriverStation != null) {
            mDriverStation.clear();
        } else if (target == Target.DASHBOARD && mDashboard != null) {
            mDashboard.getTelemetry().clear();
        }
    }

    public void clear() {
        if (mDriverStation != null) { mDriverStation.clear(); }
        if (mDashboard != null)     { mDashboard.getTelemetry().clear(); }
    }

}