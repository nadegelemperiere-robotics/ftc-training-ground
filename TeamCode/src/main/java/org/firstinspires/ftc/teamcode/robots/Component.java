/* -------------------------------------------------------
   Copyright (c) [2024] FASNY
   All rights reserved
   -------------------------------------------------------
   Robot component description
   ------------------------------------------------------- */
   
package org.firstinspires.ftc.teamcode.robots;

/* Json parser includes */
import org.json.JSONException;
import org.json.JSONObject;

/* Ftc Controller includes */
import org.firstinspires.ftc.robotcore.external.matrices.VectorF;

public class Component {

    public String   m_name;
    public String   m_type;
    public String   m_hardware;
    public VectorF  m_position;
    public VectorF  m_orientation;

    public String   m_reverse;

    public Component()
    {
        m_position = new VectorF(0,0,0);
        m_orientation = new VectorF(0,0,0);
    }

    public void read(JSONObject Json) throws JSONException
    {
        m_name = Json.getString("name");
        m_hardware = Json.getString("hardware");
        m_type = Json.getString("type");

        JSONObject pos = Json.getJSONObject("position");
        m_position.put(0, (float) pos.getDouble("x"));
        m_position.put(1, (float) pos.getDouble("y"));
        m_position.put(2, (float) pos.getDouble("z"));

        JSONObject orient = Json.getJSONObject("orientation");
        m_orientation.put(0, (float) orient.getDouble("roll"));
        m_orientation.put(1, (float) orient.getDouble("pitch"));
        m_orientation.put(2, (float) orient.getDouble("yaw"));


        if ( Json.has("reverse")) { m_reverse = Json.getString("reverse"); }
        
    }

}
