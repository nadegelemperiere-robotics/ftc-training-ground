/* -------------------------------------------------------
   Copyright (c) [2025] FASNY
   All rights reserved
   -------------------------------------------------------
   CoupledMotor class overloads the FTC motor class to manage
   A couple of motors both turning the same hardware.

   Note that this is a dangerous situation which can result in
   motor destruction if not correctly tuned. The coupled motors
   shall be the same model
   ------------------------------------------------------- */

package org.firstinspires.ftc.teamcode.core.components;

/* Qualcomm includes */
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;

/* Tools includes */
import org.firstinspires.ftc.teamcode.core.tools.Logger;

/* Configuration includes */
import org.firstinspires.ftc.teamcode.core.configuration.ConfMotor;

public class MotorSingle implements MotorComponent {

    Logger              mLogger;

    boolean             mReady;
    String              mName;

    DcMotorEx           mMotor;

    int                 mInvertPosition;

    /* ----------------------- Constructors ------------------------ */

    public MotorSingle(ConfMotor conf, HardwareMap hwMap, Logger logger)
    {
        mReady  = true;

        mLogger = logger;

        mName   = conf.name();

        mInvertPosition = 1;

        if((conf.controllers().size() == 1) && !conf.shallMock()) {

            ConfMotor.Controller motor = conf.controller(0);

            mMotor = hwMap.tryGet(DcMotorEx.class, motor.mapName());
            if(mMotor != null && sString2Direction.containsKey(motor.direction())) {
                mMotor.setDirection(sString2Direction.get(motor.direction()));
            }
            else if(mMotor != null)                {
                mMotor.setDirection(DcMotorSimple.Direction.FORWARD);
            }
            if(motor.shallReverseEncoder()) { mInvertPosition = -1; }

            if(mMotor != null) { mMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER); }

        }

        if(mMotor  == null) { mReady = false; }
    }
    public MotorSingle(ConfMotor.Controller conf, HardwareMap hwMap, Logger logger)
    {
        mReady  = true;

        mLogger = logger;

        mName   = conf.mapName();

        mInvertPosition = 1;

        mMotor = hwMap.tryGet(DcMotorEx.class, conf.mapName());
        if(mMotor != null && sString2Direction.containsKey(conf.direction())) {
            mMotor.setDirection(sString2Direction.get(conf.direction()));
        }
        else if(mMotor != null)                {
            mMotor.setDirection(DcMotorSimple.Direction.FORWARD);
        }
        if(conf.shallReverseEncoder()) { mInvertPosition = -1; }

        if(mMotor != null) { mMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER); }

        if(mMotor  == null) { mReady = false; }
    }


    /* --------------------- Custom functions ---------------------- */

    @Override
    public boolean                      isReady() { return mReady;}

    @Override
    public String                       getName() { return mName; }

    @Override
    public boolean                      getEncoderCorrection() { return (mInvertPosition == -1);}

    /* --------------------- DcMotor functions --------------------- */

    @Override
    public int	                        getCurrentPosition()
    {
        int result = -1;
        if(mReady) {
            result = mInvertPosition * mMotor.getCurrentPosition();
        }
        return result;
    }

    @Override
    public DcMotorSimple.Direction      getDirection()
    {
        DcMotorSimple.Direction result = DcMotorSimple.Direction.FORWARD;
        if(mReady) { result = mMotor.getDirection(); }
        return result;
    }

    @Override
    public DcMotor.RunMode	            getMode()
    {
        DcMotor.RunMode result =  DcMotor.RunMode.RUN_WITHOUT_ENCODER;
        if (mReady) { result = mMotor.getMode(); }
        return result;
    }

    @Override
    public int	                        getTargetPosition()
    {
        int result = -1;
        if(mReady) {
            result = mInvertPosition * mMotor.getTargetPosition();
        }
        return result;
    }

    @Override
    public DcMotor.ZeroPowerBehavior	getZeroPowerBehavior()
    {
        DcMotor.ZeroPowerBehavior result = DcMotor.ZeroPowerBehavior.UNKNOWN;
        if(mReady) { result = mMotor.getZeroPowerBehavior(); }
        return result;
    }

    @Override
    public double	                    getPower()
    {
        double result = -1;
        if(mReady) { result = mMotor.getPower(); }
        return result;
    }

    @Override
    public boolean	                    isBusy()
    {
        boolean result = false;
        if(mReady) { result = mMotor.isBusy(); }
        return result;
    }

    @Override
    public void	                        setMode(DcMotor.RunMode mode)
    {
        if(mReady) {
            mMotor.setMode(mode);
        }
    }

    @Override
    public void	                        setDirection(DcMotorSimple.Direction direction)
    {
        if(mReady) {
            mMotor.setDirection(direction);
        }
    }

    @Override
    public void	                        setTargetPosition(int position)
    {
        if(mReady) {
            mMotor.setTargetPosition(mInvertPosition * position);
        }
    }

    @Override
    public void	                        setZeroPowerBehavior(DcMotor.ZeroPowerBehavior zeroPowerBehavior)
    {
        if(mReady) {
            mMotor.setZeroPowerBehavior(zeroPowerBehavior);
        }
    }

    @Override
    public void	                        setPower(double power)
    {
        if(mReady) {
            mMotor.setPower(power);
        }
    }
    
    /* -------------------- DcMotorEx functions -------------------- */


    @Override
    public PIDFCoefficients            getPIDFCoefficients(DcMotor.RunMode mode){
        PIDFCoefficients result = null;
        if(mReady) {
            result = mMotor.getPIDFCoefficients(mode);
        }
        return result;
    }

    @Override
    public void                        setPIDFCoefficients(DcMotor.RunMode mode, PIDFCoefficients pidfCoefficients){
        if(mReady) {
            mMotor.setPIDFCoefficients(mode, pidfCoefficients);
        }
    }

    @Override
    public void                        setTargetPositionTolerance(int tolerance)
    {
        if(mReady) {
            mMotor.setTargetPositionTolerance(tolerance);
        }
    }

    @Override
    public int                         getTargetPositionTolerance()
    {
        int result = -1;
        if(mReady) {
            result = mMotor.getTargetPositionTolerance();
        }
        return result;
    }

    @Override
    public double                      getVelocity()
    {
        double result = 0;
        if(mReady) {
            result = mMotor.getVelocity();
        }
        return result;

    }

    @Override
    public String                      logPositions()
    {
        String result = "";
        if(mReady) {
            result += "\n  First : P : " + mMotor.getCurrentPosition() + " V : " + mMotor.getVelocity() + " P : " + mMotor.getPower();
        }
        return result;
    }

}
