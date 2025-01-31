/* -------------------------------------------------------
   Copyright (c) [2025] Nadege LEMPERIERE
   All rights reserved
   -------------------------------------------------------
   Localization configuration
   ------------------------------------------------------- */

package org.firstinspires.ftc.teamcode.core.configuration;

/* System includes */
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Iterator;
import java.util.Objects;

/* Json includes */
import org.json.JSONObject;

/* Tools includes */
import org.firstinspires.ftc.teamcode.core.tools.Logger;

public class ConfOdometer implements ConfComponent{

    static final String     sTypeKey  = "type";

    Logger                  mLogger;

    boolean                 mValid;

    String                  mType;
    Map<String,String>      mHwMap;
    Map<String,Double>      mParameters;

    public ConfOdometer(String name, Logger logger) {

        mLogger     = logger;

        mValid      = false;

        mType       = name;
        mParameters = new LinkedHashMap<>();
        mHwMap      = new LinkedHashMap<>();

    }
    public ConfOdometer(ConfOdometer copy) {

        mLogger    = copy.mLogger;
        mValid     = copy.mValid;
        mType      = copy.mType;

        mParameters = new LinkedHashMap<>(copy.mParameters);
        mHwMap      = new LinkedHashMap<>(copy.mHwMap);

    }

    /* --------------- Accessors -------------- */

    public  boolean             isValid()    { return mValid;     }
    public  String              name()       { return mType;      }
    public  Map<String,String>  mapName()    { return mHwMap;     }
    public  Map<String,Double>  parameters() { return mParameters;}

    /* -------------- Comparisons ------------- */
    public boolean              equals(ConfOdometer odometer) {

        boolean result = odometer.mType.equals(mType);

        for (Map.Entry<String, String> hw : mHwMap.entrySet()) {
            if(!odometer.mHwMap.containsKey(hw.getKey())) { result = false; }
            else if (!Objects.equals(odometer.mHwMap.get(hw.getKey()), hw.getValue())) { result = false; }
        }
        for (Map.Entry<String, String> hw : odometer.mHwMap.entrySet()) {
            if(!mHwMap.containsKey(hw.getKey())) { result = false; }
            else if (!Objects.equals(mHwMap.get(hw.getKey()), hw.getValue())) { result = false; }
        }

        for(Map.Entry<String,Double> param : mParameters.entrySet()) {
            if(!odometer.mParameters.containsKey(param.getKey())) { result = false; }
            else if (!Objects.equals(odometer.mParameters.get(param.getKey()), param.getValue())) { result = false; }
        }
        for(Map.Entry<String,Double> param : odometer.mParameters.entrySet()) {
            if(!mParameters.containsKey(param.getKey())) { result = false; }
            else if (!Objects.equals(mParameters.get(param.getKey()), param.getValue())) { result = false; }
        }

        return result;
    }


    /* ------------------ I/O ----------------- */
    public void read(JSONObject reader) {

        mValid = true;

        try {

            mHwMap.clear();
            mParameters.clear();

            if(mType.equals("otos")) {

                if (reader.has("hwmap")) {
                    mHwMap.put("hwmap",reader.getString("hwmap"));
                }
                Iterator<String> keys = reader.keys();
                while (keys.hasNext()) {
                    String key = keys.next();
                    Object value = reader.get(key); // Retrieve the value
                    if(!key.equals("hwmap")) {
                        mParameters.put(key,(double)value);
                    }
                }
            }

            this.check();
        }
        catch(Exception e) {
            mLogger.addLine(Logger.Target.DRIVER_STATION,e.getMessage());
            mLogger.addLine(Logger.Target.DASHBOARD,e.toString());
        }

    }

    public void write(JSONObject writer) {

        if(mValid) {

            try {
                // Write type key
                if (mType != null) {
                    writer.put(sTypeKey, mType);
                }

                for (Map.Entry<String, Double> param : mParameters.entrySet()) {
                    writer.put(param.getKey(),param.getValue());
                }

            } catch (Exception e) {
                mLogger.addLine(Logger.Target.DRIVER_STATION, e.getMessage());
                mLogger.addLine(Logger.Target.DASHBOARD, e.toString());
            }
        }
    }


    public String log() {

        StringBuilder result = new StringBuilder("<p>Type : " + mType);

        for (Map.Entry<String, Double> param : mParameters.entrySet()) {
            result.append("</p><p>").append(param.getKey()).append(" : ").append(param.getValue());
        }

        result.append("</p>");

        return result.toString();

    }

    private void check() {

        boolean is_type_valid = false;
        switch (mType) {
            case "otos":
                if(mHwMap.size() == 1) { is_type_valid = true; }
                break;
            case "2deadwheels":
                if(mHwMap.size() == 2) { is_type_valid = true; }
                break;
            case "3deadwheels":
                if(mHwMap.size() == 3) { is_type_valid = true; }
                break;
        }

        mValid = (is_type_valid);

    }

}
