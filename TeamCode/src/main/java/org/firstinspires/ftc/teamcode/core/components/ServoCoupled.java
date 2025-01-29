/* -------------------------------------------------------
   Copyright (c) [2025] FASNY
   All rights reserved
   -------------------------------------------------------
   ServoCoupled class supersedes the FTC servo class to manage
   A couple of servos both turning the same hardware.

   Note that this is a dangerous situation which can result in
   servo destruction if not correctly tuned. The coupled servos
   shall be tuned so that each orientation of the hardware they
   both support correspond to the same position on the 2 servos.
   If wrongly tuned, each of the 2 coupled servos may end up
   each forcing into a position they can not reach without the
   other failing.

   This means for example that the 2 servos are the same model
   ------------------------------------------------------- */

package org.firstinspires.ftc.teamcode.core.components;

/* System includes */
import java.util.List;

/* Qualcomm includes */
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

/* Tools includes */
import org.firstinspires.ftc.teamcode.core.tools.Logger;

/* Configuration includes */
import org.firstinspires.ftc.teamcode.core.configuration.ConfServo;


public class ServoCoupled implements ServoComponent {

    Logger                      mLogger;

    boolean                     mReady;
    String                      mName;

    ServoControllerComponent    mController;

    Servo.Direction             mDirection;
    
    Servo                       mFirst;
    Servo                       mSecond;

    /* -------------- Constructors --------------- */
    public ServoCoupled(ConfServo conf, HardwareMap hwMap, Logger logger)
    {
        mReady = true;

        mLogger = logger;

        mDirection = Servo.Direction.FORWARD;
        mController = null;

        mName = conf.name();

        List<ConfServo.Controller> controllers = conf.controllers();
        if((controllers.size() == 2) && !conf.shallMock()) {

            ConfServo.Controller first  = conf.controller(0);
            ConfServo.Controller second = conf.controller(1);

            mFirst = hwMap.tryGet(Servo.class, first.mapName());
            if(mFirst != null && first.shallReverse()) { mFirst.setDirection(Servo.Direction.REVERSE);}
            else if(mFirst != null)                    { mFirst.setDirection(Servo.Direction.FORWARD);}

            mSecond = hwMap.tryGet(Servo.class, second.mapName());
            if(mSecond != null && second.shallReverse()) { mSecond.setDirection(Servo.Direction.REVERSE);}
            else if(mSecond != null)                     { mSecond.setDirection(Servo.Direction.FORWARD);}
        }

        if(mFirst  == null) { mReady = false; }
        if(mSecond == null) { mReady = false; }

        if(mReady) {
            mController = new ServoControllerCoupled(mFirst.getController(), mSecond.getController(), mLogger);
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
        return mDirection;
    }

    @Override
    public double	                    getPosition()
    {
        double result = -1;
        if(mReady) {
            result = 0.5 * mFirst.getPosition() + 0.5 * mSecond.getPosition();
        }
        return result;
    }

    @Override
    public void	                        scaleRange(double min, double max)
    {
        if(mReady) {
            mFirst.scaleRange(min, max);
            mSecond.scaleRange(min, max);
        }
    }

    @Override
    public void	                        setDirection(Servo.Direction direction)
    {
        if(direction != mDirection && mReady) {

            if(     mFirst.getDirection()  == Servo.Direction.FORWARD) { mFirst.setDirection(Servo.Direction.REVERSE);  }
            else if(mFirst.getDirection()  == Servo.Direction.REVERSE) { mFirst.setDirection(Servo.Direction.FORWARD);  }

            if(     mSecond.getDirection() == Servo.Direction.FORWARD) { mSecond.setDirection(Servo.Direction.REVERSE); }
            else if(mSecond.getDirection() == Servo.Direction.REVERSE) { mSecond.setDirection(Servo.Direction.FORWARD); }

            mDirection = direction;

        }
    }

    @Override
    public void	                        setPosition(double position)
    {
        if(mReady) {
            mFirst.setPosition(position);
            mSecond.setPosition(position);
        }
    }
}
