/* -------------------------------------------------------
   Copyright (c) [2024] FASNY
   All rights reserved
   -------------------------------------------------------
   Robot absolute location estimation
   ------------------------------------------------------- */
package org.firstinspires.ftc.teamcode.functions;

/* System includes */
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.List;
import java.util.ArrayList;

/* Json parser includes */
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/* Ftc Controller includes */
import org.firstinspires.ftc.robotcore.external.Telemetry;

/* Roadrunner includes */
import org.firstinspires.ftc.teamcode.roadrunner.MecanumDrive;
import org.firstinspires.ftc.teamcode.roadrunner.TankDrive;

/* Local includes */
import org.firstinspires.ftc.teamcode.robot.Component;

class Model {
    public String name;
    public String drive;
    public List<Component> components;
    public MecanumDrive.Params rr_mecanum;
    public TankDrive.Params rr_tank;

}

public class Configuration {

    private Telemetry           m_logger = null;
    private Model               m_model;

    public Configuration(Telemetry logger) {
        m_logger = logger;
    }

    public Configuration() {
    }

    public void read(String Json) throws JSONException, IOException {
        
        JSONObject jsonObj = new JSONObject(Json);
        m_model = new Model();
        m_model.name = jsonObj.getString("name");
        m_model.drive = jsonObj.getString("drive");

        JSONObject roadrunnerObject = jsonObj.getJSONObject("roadrunner");
        if(m_model.drive.equals("mecanum")) {
            m_model.rr_mecanum = new MecanumDrive.Params();
            m_model.rr_mecanum.inPerTick = Double.parseDouble(roadrunnerObject.getString("inPerTick"));
            m_model.rr_mecanum.lateralInPerTick = Double.parseDouble(roadrunnerObject.getString("lateralInPerTick"));
            m_model.rr_mecanum.trackWidthTicks = Double.parseDouble(roadrunnerObject.getString("trackWidthTicks"));
            m_model.rr_mecanum.kS = Double.parseDouble(roadrunnerObject.getString("kS"));
            m_model.rr_mecanum.kA = Double.parseDouble(roadrunnerObject.getString("kA"));
            m_model.rr_mecanum.kV = Double.parseDouble(roadrunnerObject.getString("kV"));
            m_model.rr_mecanum.axialGain = Double.parseDouble(roadrunnerObject.getString("axialGain"));
            m_model.rr_mecanum.lateralGain = Double.parseDouble(roadrunnerObject.getString("lateralGain"));
            m_model.rr_mecanum.headingGain = Double.parseDouble(roadrunnerObject.getString("headingGain"));
            m_model.rr_mecanum.axialVelGain = Double.parseDouble(roadrunnerObject.getString("axialVelGain"));
            m_model.rr_mecanum.lateralVelGain = Double.parseDouble(roadrunnerObject.getString("lateralVelGain"));
            m_model.rr_mecanum.headingVelGain = Double.parseDouble(roadrunnerObject.getString("headingVelGain"));
            m_model.rr_mecanum.maxAngAccel = Double.parseDouble(roadrunnerObject.getString("maxAngAccel"));
            m_model.rr_mecanum.maxAngVel = Double.parseDouble(roadrunnerObject.getString("maxAngVel"));
            m_model.rr_mecanum.maxProfileAccel = Double.parseDouble(roadrunnerObject.getString("maxProfileAccel"));
            m_model.rr_mecanum.maxWheelVel = Double.parseDouble(roadrunnerObject.getString("maxWheelVel"));
            m_model.rr_mecanum.minProfileAccel = Double.parseDouble(roadrunnerObject.getString("minProfileAccel"));
        }
        else if(m_model.drive.equals("tank")) {
            m_model.rr_tank = new TankDrive.Params();
            m_model.rr_tank.inPerTick = Double.parseDouble(roadrunnerObject.getString("inPerTick"));
            m_model.rr_tank.trackWidthTicks = Double.parseDouble(roadrunnerObject.getString("trackWidthTicks"));
            m_model.rr_tank.kS = Double.parseDouble(roadrunnerObject.getString("kS"));
            m_model.rr_tank.kA = Double.parseDouble(roadrunnerObject.getString("kA"));
            m_model.rr_tank.kV = Double.parseDouble(roadrunnerObject.getString("kV"));
            m_model.rr_tank.maxAngVel = Double.parseDouble(roadrunnerObject.getString("maxAngVel"));
            m_model.rr_tank.maxAngAccel = Double.parseDouble(roadrunnerObject.getString("maxAngAccel"));
            m_model.rr_tank.maxWheelVel = Double.parseDouble(roadrunnerObject.getString("maxWheelVel"));
            m_model.rr_tank.minProfileAccel = Double.parseDouble(roadrunnerObject.getString("minProfileAccel"));
            m_model.rr_tank.ramseteBBar = Double.parseDouble(roadrunnerObject.getString("ramseteBBar"));
            m_model.rr_tank.ramseteZeta = Double.parseDouble(roadrunnerObject.getString("ramseteZeta"));
            m_model.rr_tank.turnGain = Double.parseDouble(roadrunnerObject.getString("turnGain"));
            m_model.rr_tank.turnVelGain = Double.parseDouble(roadrunnerObject.getString("turnVelGain"));
        }
        else {
            throw new IOException("Unknown drive mode " + m_model.drive);
        }

        m_model.components = new ArrayList<>();
        JSONArray componentsArray = jsonObj.getJSONArray("components");
        for (int i = 0; i < componentsArray.length(); i++) {

            JSONObject componentObj = componentsArray.getJSONObject(i);
            
            // Create and populate a new Component
            Component component = new Component();
            component.read(componentObj);

            // Add the component to the model
            m_model.components.add(component);
        }
    }

    public List<Component> get(String topic, String value) throws IOException {

        List<Component> result = new ArrayList<>();
        
        // Loop through each component in the model
        for (Component component : m_model.components) {
            try {
                // Access the field dynamically using Reflection
                Field field = Component.class.getDeclaredField("m_" + topic);
                field.setAccessible(true);
                
                // Check if the field value matches the specified value
                Object fieldValue = field.get(component);
                if (fieldValue != null && fieldValue.toString().equals(value)) {
                    result.add(component);
                }
            } catch (NoSuchFieldException | IllegalAccessException e) {
                if(m_logger != null) { m_logger.addLine("Configuration - Invalid topic: " + topic); }
                throw new IOException("Configuration error");
            }
        }
        
        return result;
    }

    public String name() {
        return m_model.name;
    }
    public String drive() {
        return m_model.drive;
    }
    public MecanumDrive.Params rrMecanum() {
        return m_model.rr_mecanum;
    }
    public TankDrive.Params rrTank() {
        return m_model.rr_tank;
    }

}
