/* -------------------------------------------------------
   Copyright (c) [2025] Nadege LEMPERIERE
   All rights reserved
   -------------------------------------------------------
   Motor configuration management
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

public class ConfMotor implements ConfComponent{

    static final String sMockKey       = "mock";
    static final String sControllerKey = "controllers";
    static final String sPositionsKey  = "positions";

    public static class Controller {

        static  final String sHwMapKey = "hwmap";
        static  final String sDirection = "direction";
        static  final String sEncoderReverse = "encoder-reverse";

        String  mHwMap = "";
        String  mDirection = ""; // Optional, can be null
        Boolean mShallReverseEncoder = false;

        Logger  mLogger;// Optional, can be null

        public String  mapName()             { return mHwMap; }
        public String  direction()           { return mDirection; }
        public boolean shallReverseEncoder() { return mShallReverseEncoder; }

        public void    shallReverseEncoder(boolean Value) { mShallReverseEncoder = Value; }
        public void    direction(String Value)            { mDirection = Value; }

        public boolean check() {

            boolean is_hwmap_valid = true;
            if (Objects.equals(mHwMap, "")) {
                is_hwmap_valid = false;
                mLogger.addLine("Controller hwmap name mot found ");
            }

            boolean is_direction_valid = false;
            if (Objects.equals(mDirection, "forward")) {
                is_direction_valid = true;
            }
            else if (Objects.equals(mDirection, "reverse")) {
                is_direction_valid = true;
            }
            if (!is_direction_valid) {
                mLogger.addLine("Motor controller direction " + mDirection + " is invalid");
            }

            return (is_hwmap_valid && is_direction_valid);
        }
    }

    Logger                  mLogger;
    
    String                  mName;

    List<Controller>        mControllers;
    Map<String, Integer>    mPositions; // Optional, can be null

    boolean                 mValid;
    boolean                 mShallMock;

    public ConfMotor(String name, Logger logger) {

        mLogger    = logger;
        mName      = name;
        
        mValid     = false;
        mShallMock = true;

        mControllers = new ArrayList<>();
        mPositions   = new LinkedHashMap<>();

    }

    public ConfMotor(ConfMotor copy) {

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
            controller.mDirection = copy.mControllers.get(i_ctrl).mDirection;
            controller.mShallReverseEncoder = copy.mControllers.get(i_ctrl).mShallReverseEncoder;

            mControllers.add(controller);
        }

    }

    /* --------------- Accessors -------------- */
    public  boolean isValid()   { return mValid; }
    public  boolean shallMock() { return mShallMock; }
    public  String  name()      { return mName; }

    public  List<Controller>        controllers() { return mControllers; }
    public  Map<String, Integer>    positions()   { return mPositions; }

    public  Controller              controller(int index) {
        Controller result = null;
        if(mControllers.size() > index) {
            result = mControllers.get(index);
        }
        return result;
    }

    public int                      position(String name) {
        int result = -1;
        if(mPositions.containsKey(name)) {
            Integer object = mPositions.get(name);
            if(object != null) { result = (int)object; }
        }
        return result;
    }

    /* ------------------ I/O ----------------- */
    public void read(JSONObject reader) {

        mValid     = true;
        mShallMock = false;

        // Read mock
        try {
            if (reader.has(ConfMotor.sMockKey)) {
                mShallMock = reader.getBoolean(ConfMotor.sMockKey);
            }
        }
        catch (Exception e) {
            mLogger.addLine(Logger.Target.DRIVER_STATION, e.getMessage());
            mLogger.addLine(Logger.Target.DASHBOARD, e.toString());
            mShallMock = false;
        }

        // Read controllers
        mControllers.clear();
        try {

            if (reader.has(ConfMotor.sControllerKey)) {

                JSONArray controllers = reader.getJSONArray(ConfMotor.sControllerKey);
                for (int i_ctrl = 0; i_ctrl < controllers.length(); i_ctrl++) {

                    JSONObject read = controllers.getJSONObject(i_ctrl);
                    Controller controller = new Controller();

                    controller.mLogger = mLogger;

                    if (read.has(Controller.sHwMapKey)) {
                        controller.mHwMap = read.getString(Controller.sHwMapKey);
                    }

                    if (read.has(Controller.sDirection)) {
                        controller.mDirection = read.getString(Controller.sDirection);
                    } else {
                        controller.mDirection = "forward";
                    }

                    if (read.has(Controller.sEncoderReverse)) {
                        controller.mShallReverseEncoder = read.getBoolean(Controller.sEncoderReverse);
                    }

                    mControllers.add(controller);
                }
            }

        } catch (Exception e) {
            mLogger.addLine(Logger.Target.DRIVER_STATION, e.getMessage());
            mLogger.addLine(Logger.Target.DASHBOARD, e.toString());
            mControllers.clear();
        }

        // Read positions
        mPositions.clear();
        try {

            if (reader.has(ConfMotor.sPositionsKey)) {
                JSONObject positions = reader.getJSONObject(ConfMotor.sPositionsKey);
                Iterator<String> keys = positions.keys();
                while (keys.hasNext()) {
                    String key = keys.next();
                    mPositions.put(key, positions.getInt(key));
                }
            }

        } catch (Exception e) {
            mLogger.addLine(Logger.Target.DRIVER_STATION, e.getMessage());
            mLogger.addLine(Logger.Target.DASHBOARD, e.toString());
            mPositions.clear();
        }

        this.check();

    }

    public void write(JSONObject writer) {

        if(mValid) {

            // Write mock
            try {
                writer.put(ConfMotor.sMockKey, mShallMock);
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

                    if (controller.mDirection != null) {
                        controllerJson.put(Controller.sDirection, controller.mDirection);
                    }

                    controllerJson.put(Controller.sEncoderReverse, controller.mShallReverseEncoder);

                    // Add the controller JSON to the array
                    controllersArray.put(controllerJson);
                }

                writer.put(ConfMotor.sControllerKey, controllersArray);

            } catch (Exception e) {
                mLogger.addLine(Logger.Target.DRIVER_STATION, e.getMessage());
                mLogger.addLine(Logger.Target.DASHBOARD, e.toString());
            }

            // Write positions
            try {
                JSONObject positionsJson = new JSONObject();

                for (Map.Entry<String, Integer> entry : mPositions.entrySet()) {
                    positionsJson.put(entry.getKey(), entry.getValue());
                }

                writer.put(ConfMotor.sPositionsKey, positionsJson);

            } catch (Exception e) {
                mLogger.addLine(Logger.Target.DRIVER_STATION, e.getMessage());
                mLogger.addLine(Logger.Target.DASHBOARD, e.toString());
            }
        }
    }

    public String log() {

        StringBuilder motorLog = new StringBuilder();
        motorLog.append("<p>Mock : ")
                        .append(mShallMock)
                        .append("</p>");
        motorLog.append("<details>\n");
        motorLog.append("<summary style=\"font-size: 12px; font-weight: 500\"> CONTROLLERS </summary>\n");
        motorLog.append("<ul>\n");
        for (int i_ctrl = 0; i_ctrl < mControllers.size(); i_ctrl++) {
            motorLog.append("<li style=\"padding-left:10px; font-size: 11px\">")
                    .append("ID : ")
                    .append(i_ctrl)
                    .append(" - HW : ")
                    .append(mControllers.get(i_ctrl).mHwMap)
                    .append(" - DIR : ")
                    .append(mControllers.get(i_ctrl).mDirection)
                    .append(" - ENC : ")
                    .append(mControllers.get(i_ctrl).mShallReverseEncoder)
                    .append("</li>\n");
        }
        motorLog.append("</ul>\n");
        motorLog.append("</details>\n");
        
        if(!mPositions.isEmpty()) {
            motorLog.append("<details>\n");
            motorLog.append("<summary style=\"font-size: 12px; font-weight: 500\"> POSITIONS </summary>\n");
            motorLog.append("<ul>\n");

            for (Map.Entry<String, Integer> position : mPositions.entrySet()) {
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

        boolean is_valid;
        if(mShallMock) { is_valid = true; }
        else {
            is_valid = !mControllers.isEmpty();
            for (int i_ctrl = 0; i_ctrl < mControllers.size(); i_ctrl++) {
                if (!mControllers.get(i_ctrl).check()) {
                    is_valid = false;
                }
            }
        }
        mValid = is_valid;
    }

}


