/* -------------------------------------------------------
   Copyright (c) [2024] FASNY
   All rights reserved
   -------------------------------------------------------
   Robot Moving logic
   ------------------------------------------------------- */

package org.firstinspires.ftc.teamcode.functions;

/* System includes */
import java.io.IOException;

/* Qualcomm includes */
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Gamepad;

/* Ftc Controller includes */
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.Telemetry;

/* Local includes */
import org.firstinspires.ftc.teamcode.robots.Drive;

public class Moving {
    /** Class managing configuration and control of a basic chain train **/

    protected Drive                   m_drive;

    protected Telemetry               m_logger;
    
    public  static  Moving    Builder(Configuration config, Telemetry logger) throws IOException {

        Moving result = new Moving();

        if (config.drive().equals("mecanum")) {
            result = new MecanumMoving(logger);
        }
        else if (config.drive().equals("tank")) {
            result = new TankMoving(logger);
        }

        return result;
    }

    public Moving() {
    }


    public void configure(HardwareMap map, Gamepad gamepad, Configuration config) throws IOException {
    }

    public void update() {

    }


}

class TankMoving extends Moving {
    
    public TankMoving(Telemetry logger) throws IOException {

        m_logger = logger;
        m_drive = new Drive(logger);
    }

    @Override
    public void configure(HardwareMap map, Gamepad gamepad, Configuration config) throws IOException {

        m_drive.configure(map,config);

        if(m_drive.wheel("left") == null) {
            m_logger.addLine("Moving - Missing left wheel for omniwheels configuration");
            throw new IOException("Moving configuration failed");
        }
        if(m_drive.wheel("right") == null) {
            m_logger.addLine("Moving - Missing right  wheel for omniwheels configuration");
            throw new IOException("Moving configuration failed");
        }
        
    }

    @Override
    public void update(){


    }

}

class MecanumMoving extends Moving {

    public MecanumMoving(Telemetry logger) throws IOException {

        m_logger  = logger;
        m_drive   = new Drive(logger);
    }

    @Override
    public void configure(HardwareMap map, Gamepad gamepad, Configuration config) throws IOException {

        m_drive.configure(map,config);


        if (m_drive.wheel("left-front-wheel") == null) {
            m_logger.addLine("Moving - Missing left front wheel for omniwheels configuration");
            throw new IOException("Moving configuration failed");
        }
        if (m_drive.wheel("right-front-wheel") == null) {
            m_logger.addLine("Moving - Missing right front wheel for omniwheels configuration");
            throw new IOException("Moving configuration failed");
        }
        if (m_drive.wheel("left-back-wheel") == null) {
            m_logger.addLine("Moving - Missing left back wheel for omniwheels configuration");
            throw new IOException("Moving configuration failed");
        }
        if (m_drive.wheel("right-back-wheel") == null) {
            m_logger.addLine("Moving - Missing right back wheel for omniwheels configuration");
            throw new IOException("Moving configuration failed");
        }

    }

    @Override
    public void update(){

    }

}
