/* -------------------------------------------------------
   Copyright (c) [2025] Nadege LEMPERIERE
   All rights reserved
   -------------------------------------------------------
   A container to mock servo behavior
   ------------------------------------------------------- */

package org.firstinspires.ftc.teamcode.core.components;

/* System includes */
import static java.lang.Math.max;
import static java.lang.Math.min;

/* Qualcomm includes */
import com.qualcomm.robotcore.hardware.Servo;

/* Tools includes */
import org.firstinspires.ftc.teamcode.core.tools.Logger;

public class ServoMock implements ServoComponent {

    Logger                      mLogger;

    boolean                     mReady;
    String                      mName;

    ServoControllerComponent    mController;

    Servo.Direction             mDirection;
    double                      mPosition;
    double                      mMin;
    double                      mMax;

    /* -------------- Constructors --------------- */
    public ServoMock(String name, Logger logger)
    {
        mName   = name;
        mLogger = logger;
        mReady  = true;
        mDirection = Servo.Direction.FORWARD;
        mController = new ServoControllerMock(mLogger);
    }

    /* --------------------- Custom functions ---------------------- */

    @Override
    public boolean                      isReady() { return mReady;}

    @Override
    public String                       getName() { return mName; }

    /* ---------------------- Servo functions ---------------------- */
    @Override
    public ServoControllerComponent     getController() { return mController; }

    @Override
    public Servo.Direction	            getDirection()  { return mDirection;  }

    @Override
    public double	                    getPosition()   { return mPosition;   }

    @Override
    public void	                        scaleRange(double min, double max)
    {
        mMin = min;
        mMax = max;
    }

    @Override
    public void	                        setDirection(Servo.Direction direction) { mDirection = direction; }

    @Override
    public void	                        setPosition(double position)
    {
        mPosition = min(position,mMax);
        mPosition = max(mPosition,mMin);
    }

}
