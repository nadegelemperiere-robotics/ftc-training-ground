/* -------------------------------------------------------
   Copyright (c) [2025] Nadege LEMPERIERE
   All rights reserved
   -------------------------------------------------------
   Servo configuration management
   ------------------------------------------------------- */

package org.firstinspires.ftc.teamcode.core.configuration;

/* System includes */
import java.util.Iterator;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Objects;

/* Json includes */
import org.json.JSONObject;
import org.json.JSONArray;

/* Tools includes */
import org.firstinspires.ftc.teamcode.core.tools.Logger;

public class ConfServo implements ConfComponent {

    static final String sMockKey       = "mock";
    static final String sControllerKey = "controllers";
    static final String sPositionsKey  = "positions";

    public static class Controller {

        static  final String sHwMapKey = "hwmap";
        static  final String sReverse  = "reverse";

        String  mHwMap = "";
        Boolean mShallReverse = false;

        Logger  mLogger;// Optional, can be null

        public String  mapName()      { return mHwMap; }
        public boolean shallReverse() { return mShallReverse; }

        public void    shallReverse(boolean Value) { mShallReverse = Value; }

        public boolean check() {

            boolean is_hwmap_valid = true;
            if (Objects.equals(mHwMap, "")) {
                is_hwmap_valid = false;
                if(mLogger != null) { mLogger.addLine("Controller hwmap name mot found "); }
            }

            return (is_hwmap_valid);
        }
    }

    Logger                  mLogger;

    String                  mName;

    List<Controller>        mControllers;
    Map<String, Double>     mPositions; // Optional, can be null

    boolean                 mValid;
    boolean                 mShallMock;

    public ConfServo(String name, Logger logger) {

        mLogger = logger;
        mName   = name;

        mValid     = false;
        mShallMock = false;

        mControllers = new ArrayList<>();
        mPositions = new LinkedHashMap<>();

    }
    public ConfServo(ConfServo copy) {

        mLogger    = copy.mLogger;
        mName      = copy.mName;
        mValid     = copy.mValid;
        mShallMock = copy.mShallMock;

        mPositions = new LinkedHashMap<>(copy.mPositions);
        mControllers = new ArrayList<>();
        for(int i_ctrl = 0; i_ctrl < copy.mControllers.size(); i_ctrl ++) {

            Controller controller = new Controller();

            controller.mLogger = copy.mControllers.get(i_ctrl).mLogger;
            controller.mHwMap = copy.mControllers.get(i_ctrl).mHwMap;
            controller.mShallReverse = copy.mControllers.get(i_ctrl).mShallReverse;

            mControllers.add(controller);
        }

    }

    /* --------------- Accessors -------------- */
    public  boolean isValid()   { return mValid; }
    public  boolean shallMock() { return mShallMock; }
    public  String  name()      { return mName; }

    public  List<Controller>        controllers() { return mControllers; }
    public  Map<String, Double>     positions()   { return mPositions; }

    public Controller controller(int index) {
        Controller result = null;
        if(mControllers.size() > index) {
            result = mControllers.get(index);
        }
        return result;
    }

    public double                   position(String name) {
        double result = -1;
        if(mPositions.containsKey(name)) {
            Double object = mPositions.get(name);
            if(object != null) { result = object; }
        }
        return result;
    }

    /* ------------------ I/O ----------------- */
    public void read(JSONObject reader) {

        mValid = true;

        // Read mock
        try {
            if (reader.has(ConfServo.sMockKey)) {
                mShallMock = reader.getBoolean(ConfServo.sMockKey);
            }
        }
        catch (Exception e) {
            mLogger.addLine(Logger.Target.DRIVER_STATION, e.getMessage());
            mLogger.addLine(Logger.Target.DASHBOARD, e.toString());
            mShallMock = false;
        }

        // Read controllers
        try {

            JSONArray controllers = reader.getJSONArray(ConfServo.sControllerKey);
            if (controllers.length() == 0) {
                mValid = false;
            }
            mControllers.clear();

            for (int i_ctrl = 0; i_ctrl < controllers.length(); i_ctrl++) {

                JSONObject read = controllers.getJSONObject(i_ctrl);
                Controller controller = new Controller();

                controller.mLogger = mLogger;

                if (read.has(Controller.sHwMapKey)) {
                    controller.mHwMap = read.getString(Controller.sHwMapKey);
                }

                if (read.has(Controller.sReverse)) {
                    controller.mShallReverse = read.getBoolean(Controller.sReverse);
                }

                mControllers.add(controller);
            }

            mPositions.clear();
            if (reader.has(ConfServo.sPositionsKey)) {
                JSONObject positions = reader.getJSONObject(ConfServo.sPositionsKey);
                Iterator<String> keys = positions.keys(); // Use keys() for positions
                while (keys.hasNext()) {
                    String key = keys.next();
                    mPositions.put(key, positions.getDouble(key));
                }
            }

            this.check();
        } catch (Exception e) {
            mLogger.addLine(Logger.Target.DRIVER_STATION, e.getMessage());
            mLogger.addLine(Logger.Target.DASHBOARD, e.toString());
            mControllers.clear();
            mPositions.clear();
        }

    }

    public void write(JSONObject writer) {

        if(mValid) {

            // Write mock
            try {
                writer.put(ConfServo.sMockKey, mShallMock);
            } catch (Exception e) {
                mLogger.addLine(Logger.Target.DRIVER_STATION, e.getMessage());
                mLogger.addLine(Logger.Target.DASHBOARD, e.toString());
            }

            // Write controllers
            try {
                JSONArray controllersArray = new JSONArray();

                for (Controller controller : mControllers) {
                    JSONObject controllerJson = new JSONObject();

                    // Write controller properties
                    controllerJson.put(Controller.sHwMapKey, controller.mHwMap);

                    controllerJson.put(Controller.sReverse, controller.mShallReverse);

                    // Add the controller JSON to the array
                    controllersArray.put(controllerJson);
                }

                writer.put(ConfServo.sControllerKey, controllersArray);

            } catch (Exception e) {
                mLogger.addLine(Logger.Target.DRIVER_STATION, e.getMessage());
                mLogger.addLine(Logger.Target.DASHBOARD, e.toString());
            }

            // Write positions
            try {
                JSONObject positionsJson = new JSONObject();

                for (Map.Entry<String, Double> entry : mPositions.entrySet()) {
                    positionsJson.put(entry.getKey(), entry.getValue());
                }

                writer.put(ConfServo.sPositionsKey, positionsJson);

            } catch (Exception e) {
                mLogger.addLine(Logger.Target.DRIVER_STATION, e.getMessage());
                mLogger.addLine(Logger.Target.DASHBOARD, e.toString());
            }
        }
    }

    public String log() {

        StringBuilder motorLog = new StringBuilder();
        motorLog.append("<details>\n");
        motorLog.append("<summary style=\"font-size: 12px; font-weight: 500\"> CONTROLLERS </summary>\n");
        motorLog.append("<ul>\n");
        for (int i_ctrl = 0; i_ctrl < mControllers.size(); i_ctrl++) {
            motorLog.append("<li style=\"padding-left:10px; font-size: 11px\">")
                    .append("ID : ")
                    .append(i_ctrl)
                    .append(" - HW : ")
                    .append(mControllers.get(i_ctrl).mHwMap)
                    .append(" - REV : ")
                    .append(mControllers.get(i_ctrl).mShallReverse)
                    .append("</li>\n");
        }
        motorLog.append("</ul>\n");
        motorLog.append("</details>\n");

        if(!mPositions.isEmpty()) {
            motorLog.append("<details>\n");
            motorLog.append("<summary style=\"font-size: 12px; font-weight: 500\"> POSITIONS </summary>\n");
            motorLog.append("<ul>\n");

            for (Map.Entry<String, Double> position : mPositions.entrySet()) {
                motorLog.append("<li style=\"padding-left:10px; font-size: 11px\">")
                        .append(position.getKey())
                        .append(" : ")
                        .append(position.getValue())
                        .append("</li>");
            }

            motorLog.append("</ul>\n");
            motorLog.append("</details>\n");
        }

        return motorLog.toString();
    }

    private void check() {

        boolean result = !mControllers.isEmpty();
        for (int i_ctrl = 0; i_ctrl < mControllers.size(); i_ctrl++) {
            if (!mControllers.get(i_ctrl).check()) {
                result = false;
            }
        }
        mValid = result;
    }

}


