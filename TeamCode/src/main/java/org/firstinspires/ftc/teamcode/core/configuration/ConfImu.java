/* -------------------------------------------------------
   Copyright (c) [2025] Nadege LEMPERIERE
   All rights reserved
   -------------------------------------------------------
   Imu configuration management
   ------------------------------------------------------- */

package org.firstinspires.ftc.teamcode.core.configuration;

/* System includes */
import java.util.Objects;

/* Json includes */
import org.json.JSONObject;

/* Tools includes */
import org.firstinspires.ftc.teamcode.core.tools.Logger;

public class ConfImu implements ConfComponent{

    static final String sHwMapKey       = "hwmap";
    static final String sLogoDirection  = "logo-direction";
    static final String sUsbDirection   = "usb-direction";


    Logger                  mLogger;

    String                  mName;

    String                  mHwMap;
    String                  mLogoDirection;
    String                  mUsbDirection;

    boolean                 mValid;
    boolean                 mShallMock;

    public ConfImu(String name, Logger logger) {

        mLogger = logger;
        mName   = name;

        mValid     = false;
        mShallMock = false;

        mLogoDirection = "";
        mUsbDirection  = "";
        mHwMap         = "";

    }
    public ConfImu(ConfImu copy) {

        mLogger    = copy.mLogger;
        mName      = copy.mName;
        mValid     = copy.mValid;
        mShallMock = copy.mShallMock;

        mLogoDirection = copy.mLogoDirection;
        mUsbDirection  = copy.mUsbDirection;
        mHwMap         = copy.mHwMap;

    }

    /* --------------- Accessors -------------- */

    public  boolean isValid()   { return mValid;     }
    public  boolean shallMock() { return mShallMock; }
    public  String  name()      { return mName;      }

    public  String  hardware()  { return mHwMap;         }
    public  String  logo()      { return mLogoDirection; }
    public  String  usb()       { return mUsbDirection;  }

    /* ------------------ I/O ----------------- */
    public void read(JSONObject reader) {

        mValid = true;

        try {

            if(reader.has(ConfImu.sHwMapKey)) {
                mHwMap = reader.getString(ConfImu.sHwMapKey);
            }

            if(reader.has(ConfImu.sLogoDirection)) {
                mLogoDirection = reader.getString(ConfImu.sLogoDirection);
            }

            if(reader.has(ConfImu.sUsbDirection)) {
                mUsbDirection  = reader.getString(ConfImu.sUsbDirection);
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
                // Write hardware map key
                if (mHwMap != null) {
                    writer.put(ConfImu.sHwMapKey, mHwMap);
                }

                // Write logo direction
                if (mLogoDirection != null) {
                    writer.put(ConfImu.sLogoDirection, mLogoDirection);
                }

                // Write USB direction
                if (mUsbDirection != null) {
                    writer.put(ConfImu.sUsbDirection, mUsbDirection);
                }
            } catch (Exception e) {
                mLogger.addLine(Logger.Target.DRIVER_STATION, e.getMessage());
                mLogger.addLine(Logger.Target.DASHBOARD, e.toString());
            }
        }
    }


    public String log() {

        return "<p>HW : " +
                mHwMap +
                " - LOGO : " +
                mLogoDirection +
                " - USB : " +
                mUsbDirection +
                "</p>";

    }

    private void check() {

        boolean is_hwmap_valid = true;
        if(Objects.equals(mHwMap,"")) {
            is_hwmap_valid = false;
            mLogger.addLine("Imu hwmap name mot found ");
        }

        boolean is_logo_direction_valid = ConfImu.isDirectionValid(mLogoDirection);
        if(!is_logo_direction_valid) { mLogger.addLine("Imu logo direction " + mLogoDirection + " is invalid"); }

        boolean is_usb_direction_valid = ConfImu.isDirectionValid(mUsbDirection);
        if(!is_logo_direction_valid) { mLogger.addLine("Imu usb direction " + mUsbDirection + " is invalid"); }

        mValid = (is_hwmap_valid && is_logo_direction_valid && is_usb_direction_valid);

    }



    static private boolean isDirectionValid(String direction) {
        boolean is_logo_direction_valid = false;
        if(Objects.equals(direction, ""))              { is_logo_direction_valid = true; }
        else if(Objects.equals(direction, "up"))       { is_logo_direction_valid = true; }
        else if(Objects.equals(direction, "down"))     { is_logo_direction_valid = true; }
        else if(Objects.equals(direction, "right"))    { is_logo_direction_valid = true; }
        else if(Objects.equals(direction, "left"))     { is_logo_direction_valid = true; }
        else if(Objects.equals(direction, "backward")) { is_logo_direction_valid = true; }
        else if(Objects.equals(direction, "forward"))  { is_logo_direction_valid = true; }
        return is_logo_direction_valid;
    }


}

