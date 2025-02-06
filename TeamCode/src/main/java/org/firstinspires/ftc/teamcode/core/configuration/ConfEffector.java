/* -------------------------------------------------------
   Copyright (c) [2025] Nadege LEMPERIERE
   All rights reserved
   -------------------------------------------------------
   Localization configuration
   ------------------------------------------------------- */

package org.firstinspires.ftc.teamcode.core.configuration;

/* System includes */

import org.firstinspires.ftc.teamcode.core.tools.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public class ConfEffector implements ConfComponent{

    Logger                  mLogger;

    boolean                 mValid;

    String                  mName;

    Map<String, Double>     mPositions; // Optional, can be null


    public ConfEffector(String name, Logger logger) {

        mLogger     = logger;

        mValid      = false;

        mName       = name;

        mPositions  = new LinkedHashMap<>();

    }
    public ConfEffector(ConfEffector copy) {

        mLogger    = copy.mLogger;
        mValid     = copy.mValid;
        mName      = copy.mName;

        mPositions = new LinkedHashMap<>(copy.mPositions);

    }

    /* --------------- Accessors -------------- */

    public  boolean             isValid()    { return mValid;     }
    public  String              name()       { return mName;      }
    public  Map<String,Double>  positions()  { return mPositions; }

    /* ------------------ I/O ----------------- */
    public void read(JSONObject reader) {

        mValid = true;

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

            // Write positions
            try {
                JSONObject positionsJson = new JSONObject();

                for (Map.Entry<String, Double> entry : mPositions.entrySet()) {
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

        boolean is_valid = true;

        mValid = is_valid;

    }

}
