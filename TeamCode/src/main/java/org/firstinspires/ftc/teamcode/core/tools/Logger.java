/* -------------------------------------------------------
   Copyright (c) [2025] Nadege LEMPERIERE
   All rights reserved
   -------------------------------------------------------
   Logging manager
   ------------------------------------------------------- */

package org.firstinspires.ftc.teamcode.core.tools;

/* System includes */
import java.util.LinkedHashMap;
import java.util.Map;

/* FTC Controller includes */
import org.firstinspires.ftc.robotcore.external.Telemetry;

/* ACME robotics includes */
import com.acmerobotics.dashboard.FtcDashboard;


public class Logger {

    public static class Line {

        Map<Target, Telemetry.Line> mLines;
        public Line() {
            mLines = new LinkedHashMap<>();
        }
    };
    public static class Item {

        Map<Target, Telemetry.Item> mItems;
        public Item() {
            mItems = new LinkedHashMap<>();
        }
    }

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


    public Line addLine(Target target, String line) {
        Line result = new Line();
        if (target == Target.DRIVER_STATION && mDriverStation != null) {
            result.mLines.put(Target.DRIVER_STATION,mDriverStation.addLine(line));
        } else if (target == Target.DASHBOARD && mDashboard != null) {
            result.mLines.put(Target.DASHBOARD,mDashboard.getTelemetry().addLine(line));
        }
        return result;
    }

    public Line addLine(String line) {
        Line result = new Line();
        if (mDriverStation != null) {
            result.mLines.put(Target.DRIVER_STATION,mDriverStation.addLine(line));
        }
        if ( mDashboard != null)    {
            result.mLines.put(Target.DASHBOARD,mDashboard.getTelemetry().addLine(line));
        }
        return result;
    }

    public void removeLine(Target target, Line line) {
        if (target == Target.DRIVER_STATION &&
                mDriverStation != null &&
                line.mLines.containsKey(Target.DRIVER_STATION )) {
            mDriverStation.removeLine(line.mLines.get(Target.DRIVER_STATION));
        } else if (target == Target.DASHBOARD &&
                mDashboard != null &&
                line.mLines.containsKey(Target.DRIVER_STATION )) {
            mDashboard.getTelemetry().removeLine(line.mLines.get(Target.DASHBOARD));
        }
    }

    public void removeLine(Line line) {
        if (mDriverStation != null &&
                line.mLines.containsKey(Target.DRIVER_STATION )) {
            mDriverStation.removeLine(line.mLines.get(Target.DRIVER_STATION));
        }
        if ( mDashboard != null &&
            line.mLines.containsKey(Target.DRIVER_STATION )) {
                mDashboard.getTelemetry().removeLine(line.mLines.get(Target.DASHBOARD));
        }
    }

    public Item addData(Target target, String first, String second) {
        Item result = new Item();
        if (target == Target.DRIVER_STATION && mDriverStation != null) {
            result.mItems.put(Target.DRIVER_STATION,mDriverStation.addData(first, second));
        } else if (target == Target.DASHBOARD && mDashboard != null) {
            result.mItems.put(Target.DASHBOARD,mDashboard.getTelemetry().addData(first, second));
        }
        return result;
    }

    public Item addData(String first, String second) {
        Item result = new Item();
        if (mDriverStation != null) {
            result.mItems.put(Target.DRIVER_STATION,mDriverStation.addData(first, second));
        }
        if (mDashboard != null)     {
            result.mItems.put(Target.DASHBOARD,mDashboard.getTelemetry().addData(first, second));
        }
        return result;
    }


    public void removeItem(Target target, Item item) {
        if (target == Target.DRIVER_STATION &&
                mDriverStation != null &&
                item.mItems.containsKey(Target.DRIVER_STATION )) {
            mDriverStation.removeItem(item.mItems.get(Target.DRIVER_STATION));
        } else if (target == Target.DASHBOARD &&
                mDashboard != null &&
                item.mItems.containsKey(Target.DRIVER_STATION )) {
            mDashboard.getTelemetry().removeItem(item.mItems.get(Target.DASHBOARD));
        }
    }

    public void removeItem(Item item) {
        if (mDriverStation != null &&
                item.mItems.containsKey(Target.DRIVER_STATION )) {
            mDriverStation.removeItem(item.mItems.get(Target.DRIVER_STATION));
        }
        if ( mDashboard != null &&
                item.mItems.containsKey(Target.DRIVER_STATION )) {
            mDashboard.getTelemetry().removeItem(item.mItems.get(Target.DASHBOARD));
        }
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