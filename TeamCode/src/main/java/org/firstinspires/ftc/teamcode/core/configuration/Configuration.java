/* -------------------------------------------------------
   Copyright (c) [2025] Nadege LEMPERIERE
   All rights reserved
   -------------------------------------------------------
   Configuration management
   ------------------------------------------------------- */

package org.firstinspires.ftc.teamcode.core.configuration;

/* System includes */
import android.os.Environment;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/* Json includes */
import org.json.JSONException;
import org.json.JSONObject;


/* Tools includes */
import org.firstinspires.ftc.teamcode.core.tools.Logger;

public class Configuration {

    // Current working configuration
    static public final String sCurrentConfiguration = "v1";

    static final String sHardwareKey = "hardware";
    static final String sSoftwareKey = "software";
    static final String sMotorsKey   = "motors";
    static final String sImusKey     = "imus";
    static final String sServosKey   = "servos";

    // Loggers
    Logger                          mLogger;
    String                          mFilename;

    // Status
    boolean                         mValid;

    // Hardaware configuration
    public Map<String, ConfMotor>   mMotors;
    public Map<String, ConfImu>     mImus;
    public Map<String, ConfServo>   mServos;

    /* ------------- Constructors ------------- */
    public Configuration(Logger logger) {
        mLogger = logger;

        mMotors = new LinkedHashMap<>();
        mImus   = new LinkedHashMap<>();
        mServos = new LinkedHashMap<>();
    }

    public Configuration(Configuration copy) {

        mLogger   = copy.mLogger;
        mFilename = copy.mFilename;
        mValid    = copy.mValid;

        mMotors = new LinkedHashMap<>();
        mImus   = new LinkedHashMap<>();
        mServos = new LinkedHashMap<>();

        for (Map.Entry<String, ConfMotor> entry : copy.mMotors.entrySet()) {
            mMotors.put(entry.getKey(), new ConfMotor(entry.getValue()));
        }
        for (Map.Entry<String, ConfImu> entry : copy.mImus.entrySet()) {
            mImus.put(entry.getKey(), new ConfImu(entry.getValue()));
        }
        for (Map.Entry<String, ConfServo> entry : copy.mServos.entrySet()) {
            mServos.put(entry.getKey(), new ConfServo(entry.getValue()));
        }
    }

    /* --------------- Accessors -------------- */
    public Map<String, ConfMotor> motors() { return mMotors; }
    public Map<String, ConfImu>   imus()   { return mImus; }
    public Map<String, ConfServo> servos() { return mServos; }

    /* ------------------ I/O ----------------- */
    public void read() throws IOException, JSONException {

        String filename = Environment.getExternalStorageDirectory().getPath()
                + "/FIRST/"
                + Configuration.sCurrentConfiguration
                + ".json";
        this.read(filename);
    }

    public void read(String filename) throws IOException, JSONException {

        mValid = true;
        mFilename = Configuration.getRawFilename(filename);

        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(filename), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line);
            }
        }

        // Parse JSON content
        JSONObject jsonObject = new JSONObject(content.toString());

        // Parse hardware
        mMotors.clear();
        mImus.clear();
        mServos.clear();
        if(jsonObject.has(Configuration.sHardwareKey)) {
            JSONObject hardware = jsonObject.getJSONObject(Configuration.sHardwareKey);

            if(hardware.has(Configuration.sMotorsKey)) {
                JSONObject motors = hardware.getJSONObject(Configuration.sMotorsKey);
                Iterator<String> keys = motors.keys();
                while (keys.hasNext()) {

                    String key = keys.next();
                    mMotors.put(key, new ConfMotor(key, mLogger));
                    Objects.requireNonNull(mMotors.get(key)).read(motors.getJSONObject(key));
                    if (!Objects.requireNonNull(mMotors.get(key)).isValid()) {
                        mValid = false;
                    }
                }
            }

            if(hardware.has(Configuration.sImusKey)) {
                JSONObject imus = hardware.getJSONObject(Configuration.sImusKey);
                Iterator<String> keys = imus.keys();
                while (keys.hasNext()) {

                    String key = keys.next();
                    mImus.put(key, new ConfImu(key, mLogger));
                    Objects.requireNonNull(mImus.get(key)).read(imus.getJSONObject(key));
                    if (!Objects.requireNonNull(mImus.get(key)).isValid()) {
                        mValid = false;
                    }
                }
            }

            if(hardware.has(Configuration.sServosKey)) {
                JSONObject servos = hardware.getJSONObject(Configuration.sServosKey);
                Iterator<String> keys = servos.keys();
                while (keys.hasNext()) {

                    String key = keys.next();
                    mServos.put(key, new ConfServo(key, mLogger));
                    Objects.requireNonNull(mServos.get(key)).read(servos.getJSONObject(key));
                    if (!Objects.requireNonNull(mServos.get(key)).isValid()) {
                        mValid = false;
                    }
                }
            }
        }
    }

    public void write(String filename) throws IOException, JSONException {
        JSONObject jsonObject = new JSONObject();

        // Prepare the hardware JSON object
        JSONObject hardware = new JSONObject();

        // Write motors
        if (!mMotors.isEmpty()) {
            JSONObject motors = new JSONObject();
            for (Map.Entry<String, ConfMotor> entry : mMotors.entrySet()) {
                String key = entry.getKey();
                ConfMotor motor = entry.getValue();
                JSONObject motorJson = new JSONObject();
                motor.write(motorJson);
                motors.put(key, motorJson);
            }
            hardware.put(Configuration.sMotorsKey, motors);
        }

        // Write IMUs
        if (!mImus.isEmpty()) {
            JSONObject imus = new JSONObject();
            for (Map.Entry<String, ConfImu> entry : mImus.entrySet()) {
                String key = entry.getKey();
                ConfImu imu = entry.getValue();
                JSONObject imuJson = new JSONObject();
                imu.write(imuJson);
                imus.put(key, imuJson);
            }
            hardware.put(Configuration.sImusKey, imus);
        }

        // Write servos
        if (!mServos.isEmpty()) {
            JSONObject servos = new JSONObject();
            for (Map.Entry<String, ConfServo> entry : mServos.entrySet()) {
                String key = entry.getKey();
                ConfServo servo = entry.getValue();
                JSONObject servoJson = new JSONObject();
                servo.write(servoJson);
                servos.put(key, servoJson);
            }
            hardware.put(Configuration.sServosKey, servos);
        }

        // Add hardware object to the main JSON object
        jsonObject.put(Configuration.sHardwareKey, hardware);

        // Write JSON to file
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filename), StandardCharsets.UTF_8))) {
            writer.write(jsonObject.toString(4)); // 4 is the indentation level for pretty printing
        }
    }


    public void log() {

        StringBuilder confstring = new StringBuilder();

        confstring.append("-------------------------\n");
        if (mValid) {
            mLogger.addLine(Logger.Target.DRIVER_STATION, "CNF " + mFilename + " is valid");
            confstring.append("<p style=\"color: green; font-size: 14px\"> Conf ")
                    .append(mFilename)
                    .append(" is valid</p>");
        } else {
            mLogger.addLine(Logger.Target.DRIVER_STATION, "CNF is invalid");
            confstring.append("<p style=\"color: red; font-size: 14px\"> Conf ")
                    .append(mFilename)
                    .append(" is invalid</p>");
        }

        confstring.append("-------------------------\n");
        confstring.append("<details>\n");
        confstring.append("<summary style=\"font-size: 12px; font-weight: 500\"> MOTORS </summary>\n");
        confstring.append("<ul>\n");
        mMotors.forEach((key, value) -> {
            confstring.append("<li style=\"padding-left:10px;font-size: 14px\"> ")
                    .append(key);
            confstring.append(value.log());
            confstring.append("</li>");
        });
        confstring.append("</ul>\n");
        confstring.append("</details>\n");


        confstring.append("-------------------------\n");
        confstring.append("<details>\n");
        confstring.append("<summary style=\"font-size: 12px; font-weight: 500\"> IMUS </summary>\n");
        confstring.append("<ul>\n");
        mImus.forEach((key, value) -> {
            confstring.append("<li style=\"padding-left:10px;font-size: 14px\"> ")
                    .append(key);
            confstring.append(value.log());
            confstring.append("</li>");
        });
        confstring.append("</ul>\n");
        confstring.append("</details>\n");

        confstring.append("-------------------------\n");
        confstring.append("<details>\n");
        confstring.append("<summary style=\"font-size: 12px; font-weight: 500\"> SERVOS </summary>\n");
        confstring.append("<ul>\n");
        mServos.forEach((key, value) -> {
            confstring.append("<li style=\"padding-left:10px;font-size: 14px\"> ")
                    .append(key);
            confstring.append(value.log());
            confstring.append("</li>");
        });
        confstring.append("</ul>\n");
        confstring.append("</details>\n");

        mLogger.addLine(Logger.Target.DASHBOARD,confstring.toString());
    }

    public static String getRawFilename(String filename) {
        String result;

        File file = new File(filename);
        result = file.getName();

        int dotIndex = result.lastIndexOf(".");
        result = result.substring(0,dotIndex);

        return result;
    }


}
