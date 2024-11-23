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
import com.acmerobotics.roadrunner.Action;
import com.acmerobotics.roadrunner.TurnConstraints;
import com.acmerobotics.roadrunner.Vector2d;
import com.acmerobotics.roadrunner.ftc.Actions;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Gamepad;

/* Ftc Controller includes */
import org.firstinspires.ftc.robotcore.external.Telemetry;

/* Roadrunner includes */
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.ftc.Actions;
import org.firstinspires.ftc.teamcode.roadrunner.MecanumDrive;
import org.firstinspires.ftc.teamcode.roadrunner.TankDrive;

/* Local includes */
import org.firstinspires.ftc.teamcode.robot.Drive;

public class Moving {
    /** Class managing configuration and control of a basic chain train **/

    protected Drive                   m_drive;

    protected Action                  m_actions;

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


    public void configure(HardwareMap map, Pose2d initial, Configuration config) throws IOException {
    }

    public void update() {

    }


}

class TankMoving extends Moving {

    private TankDrive m_roadrunner;

    public TankMoving(Telemetry logger) throws IOException {

        m_logger = logger;
        m_drive = new Drive(logger);
    }

    @Override
    public void configure(HardwareMap map, Pose2d initial, Configuration config) throws IOException {

        m_drive.configure(map,config);

        if(m_drive.wheel("left") == null) {
            m_logger.addLine("Moving - Missing left wheel for omniwheels configuration");
            throw new IOException("Moving configuration failed");
        }
        if(m_drive.wheel("right") == null) {
            m_logger.addLine("Moving - Missing right  wheel for omniwheels configuration");
            throw new IOException("Moving configuration failed");
        }

        m_roadrunner = new TankDrive(map, initial, m_drive);

        m_actions = m_roadrunner.actionBuilder(initial).lineToX(30).build();
        
    }

    @Override
    public void update(){


    }

}

class MecanumMoving extends Moving {

    private MecanumDrive m_roadrunner;

    public MecanumMoving(Telemetry logger) throws IOException {

        m_logger  = logger;
        m_drive   = new Drive(logger);
    }

    @Override
    public void configure(HardwareMap map, Pose2d initial, Configuration config) throws IOException {

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


        m_drive.wheel("left-back-wheel").setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        m_drive.wheel("left-front-wheel").setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        m_drive.wheel("right-back-wheel").setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        m_drive.wheel("right-front-wheel").setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        m_roadrunner = new MecanumDrive(map, initial, m_drive);

        m_actions = m_roadrunner.actionBuilder(initial).turnTo(Math.PI/2).build();

    }

    @Override
    public void update(){

        m_logger.addLine(String.format("ECP LF RF LB RB  %d %d %d %d",
                m_drive.encoder("left-front-wheel").getPositionAndVelocity().position,
                m_drive.encoder("right-front-wheel").getPositionAndVelocity().position,
                m_drive.encoder("left-back-wheel").getPositionAndVelocity().position,
                m_drive.encoder("right-back-wheel").getPositionAndVelocity().position));
        m_logger.addLine(String.format("ECV LF RF LB RB  %d %d %d %d",
                m_drive.encoder("left-front-wheel").getPositionAndVelocity().velocity,
                m_drive.encoder("right-front-wheel").getPositionAndVelocity().velocity,
                m_drive.encoder("left-back-wheel").getPositionAndVelocity().velocity,
                m_drive.encoder("right-back-wheel").getPositionAndVelocity().velocity));

        Actions.runBlocking(m_actions);

        m_logger.addLine(String.format("ECP LF RF LB RB  %d %d %d %d",
                m_drive.encoder("left-front-wheel").getPositionAndVelocity().position,
                m_drive.encoder("right-front-wheel").getPositionAndVelocity().position,
                m_drive.encoder("left-back-wheel").getPositionAndVelocity().position,
                m_drive.encoder("right-back-wheel").getPositionAndVelocity().position));
        m_logger.addLine(String.format("ECV LF RF LB RB  %d %d %d %d",
                m_drive.encoder("left-front-wheel").getPositionAndVelocity().velocity,
                m_drive.encoder("right-front-wheel").getPositionAndVelocity().velocity,
                m_drive.encoder("left-back-wheel").getPositionAndVelocity().velocity,
                m_drive.encoder("right-back-wheel").getPositionAndVelocity().velocity));

    }

}
