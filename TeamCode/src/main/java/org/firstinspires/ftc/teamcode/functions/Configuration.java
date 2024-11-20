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

/* Local includes */

/* Local includes */
import org.firstinspires.ftc.teamcode.robots.Component;

class Model {
    public String name;
    public String drive;
    public List<Component> components;
}

public class Configuration {

    private Telemetry           m_logger = null;
    private Model               m_model;

    public Configuration(Telemetry logger) {
        m_logger = logger;
    }

    public Configuration() {
    }

    public void read(String Json) throws JSONException {
        
        JSONObject jsonObj = new JSONObject(Json);
        m_model = new Model();
        m_model.name = jsonObj.getString("name");
        m_model.drive = jsonObj.getString("drive");
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

}
