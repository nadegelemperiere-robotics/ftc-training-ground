/* -------------------------------------------------------
   Copyright (c) [2025] Nadege LEMPERIERE
   All rights reserved
   -------------------------------------------------------
   A single servo
   ------------------------------------------------------- */

package org.firstinspires.ftc.teamcode.core.components.servos;

/* System includes */
import java.util.List;

/* Qualcomm includes */
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

/* Configuration includes */
import org.firstinspires.ftc.teamcode.core.configuration.ConfServo;
import org.firstinspires.ftc.teamcode.core.tools.Logger;

public class ServoSingle implements ServoComponent {

    Logger                      mLogger;

    boolean                     mReady;
    String                      mName;

    ServoControllerComponent    mController;

    Servo.Direction             mDirection;

    Servo                       mServo;

    /* -------------- Constructors --------------- */
    public ServoSingle(ConfServo conf, HardwareMap hwMap, Logger logger)
    {
        mReady = true;

        mLogger = logger;

        mName = conf.name();

        mController = null;
        mServo      = null;
        mDirection  = Servo.Direction.FORWARD;


        List<ConfServo.Controller> controllers = conf.controllers();
        if((controllers.size() == 1) && !conf.shallMock()) {

            ConfServo.Controller servo = conf.controller(0);

            mServo = hwMap.tryGet(Servo.class, servo.mapName());
            if(mServo != null && servo.shallReverse()) { mServo.setDirection(Servo.Direction.REVERSE);}
            else if(mServo != null)                    { mServo.setDirection(Servo.Direction.FORWARD);}

        }

        if(mServo  == null) { mReady = false; }

        if(mReady) {
            mController = new ServoControllerSingle(mServo.getController(), mName, mLogger);
        }
    }
    public ServoSingle(ConfServo.Controller conf, HardwareMap hwMap, Logger logger)
    {
        mReady = true;

        mLogger = logger;

        mDirection = Servo.Direction.FORWARD;

        mName = conf.mapName();

        mServo = hwMap.tryGet(Servo.class, conf.mapName());
        if(mServo != null && conf.shallReverse()) { mServo.setDirection(Servo.Direction.REVERSE);}
        else if(mServo != null)                   { mServo.setDirection(Servo.Direction.FORWARD);}

        if(mServo  == null) { mReady = false; }

        if(mReady) {
            mController = new ServoControllerSingle(mServo.getController(), mName, mLogger);
        }
    }

    /* --------------------- Custom functions ---------------------- */

    @Override
    public boolean                      isReady() { return mReady;}

    @Override
    public String                       getName() { return mName; }

    /* ---------------------- Servo functions ---------------------- */

    @Override
    public ServoControllerComponent     getController() {
        return mController;
    }

    @Override
    public Servo.Direction	            getDirection()
    {
        Servo.Direction result = Servo.Direction.FORWARD;
        if(mReady) {
            result = mServo.getDirection();
        }
        return result;
    }

    @Override
    public double	                    getPosition()
    {
        double result = -1;
        if(mReady) {
            result = mServo.getPosition();
        }
        return result;
    }

    @Override
    public void	                        scaleRange(double min, double max)
    {
        if(mReady) {
            mServo.scaleRange(min, max);
        }
    }

    @Override
    public void	                        setDirection(Servo.Direction direction)
    {
        if(mReady) {
            mServo.setDirection(direction);
        }
    }

    @Override
    public void	                        setPosition(double position)
    {
        if(mReady) {
            mServo.setPosition(position);
        }
    }
}
