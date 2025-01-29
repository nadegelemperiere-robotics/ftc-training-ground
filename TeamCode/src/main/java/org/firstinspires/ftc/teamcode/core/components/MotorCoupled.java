/* -------------------------------------------------------
   Copyright (c) [2025] Nadege LEMPERIERE
   All rights reserved
   -------------------------------------------------------
   CoupledMotor class overloads the FTC motor class to manage
   A couple of motors both turning the same hardware.

   Note that this is a dangerous situation which can result in
   motor destruction if not correctly tuned. The coupled motors
   shall be the same model
   ------------------------------------------------------- */

package org.firstinspires.ftc.teamcode.core.components;

/* System includes */
import java.util.List;

/* Qualcomm includes */
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;

/* Tools includes */
import org.firstinspires.ftc.teamcode.core.tools.Logger;

/* Configuration includes */
import org.firstinspires.ftc.teamcode.core.configuration.ConfMotor;


public class MotorCoupled implements MotorComponent {

    Logger                      mLogger;

    boolean                     mReady;
    String                      mName;
    
    DcMotorSimple.Direction     mDirection;

    DcMotorEx                   mFirst;
    DcMotorEx                   mSecond;
    int                         mFirstInvertPosition;
    int                         mSecondInvertPosition;


    /* ----------------------- Constructors ------------------------ */

    public MotorCoupled(ConfMotor conf, HardwareMap hwMap, Logger logger)
    {
        mReady  = true;

        mLogger = logger;

        mName   = conf.name();

        mDirection = DcMotor.Direction.FORWARD;
        mFirstInvertPosition = 1;
        mSecondInvertPosition = 1;

        List<ConfMotor.Controller> controllers = conf.controllers();
        if((controllers.size() == 2) && !conf.shallMock()) {

            ConfMotor.Controller first = conf.controller(0);
            ConfMotor.Controller second = conf.controller(1);

            mFirst = hwMap.tryGet(DcMotorEx.class, first.mapName());
            if(mFirst != null && sString2Direction.containsKey(first.direction())) {
                mFirst.setDirection(sString2Direction.get(first.direction()));
            }
            else if(mFirst != null)                {
                mFirst.setDirection(DcMotorSimple.Direction.FORWARD);
            }
            if(first.shallReverseEncoder()) { mFirstInvertPosition = -1; }

            mSecond = hwMap.tryGet(DcMotorEx.class, second.mapName());
            if(mSecond != null && sString2Direction.containsKey(second.direction())) {
                mSecond.setDirection(sString2Direction.get(second.direction()));
            }
            else if(mSecond != null)                {
                mSecond.setDirection(DcMotorSimple.Direction.FORWARD);
            }
            if(second.shallReverseEncoder()) { mFirstInvertPosition = -1; }

            if(mFirst != null) { mFirst.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER); }
            if(mSecond != null) { mSecond.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER); }

        }

        if(mFirst  == null) { mReady = false; }
        if(mSecond == null) { mReady = false; }
        

    }

    /* --------------------- Custom functions ---------------------- */

    @Override
    public boolean                      isReady() { return mReady;}

    @Override
    public String                       getName() { return mName; }

    /* --------------------- DcMotor functions --------------------- */

    @Override
    public int	                        getCurrentPosition()
    {
        int result = -1;
        if(mReady) {
            result = (int) (0.5 * mFirstInvertPosition * mFirst.getCurrentPosition() +
                    mSecondInvertPosition * 0.5 * mSecond.getCurrentPosition());
        }
        return result;
    }

    @Override
    public DcMotorSimple.Direction      getDirection()
    {
        return mDirection;
    }


    @Override
    public DcMotor.RunMode	            getMode()
    {
        DcMotor.RunMode result =  DcMotor.RunMode.RUN_WITHOUT_ENCODER;
        if (mReady) { result = mFirst.getMode(); }
        return result;
    }

    @Override
    public int	                        getTargetPosition()
    {
        int result = -1;
        if(mReady) {
            result = (int) (0.5 * mFirstInvertPosition * mFirst.getTargetPosition() +
                    0.5 * mSecondInvertPosition * mSecond.getTargetPosition());
        }
        return result;
    }

    @Override
    public double	                    getPower()
    {
        double result = -1;
        if(mReady) {
            result = (int) (0.5 * mFirst.getPower() + 0.5 * mSecond.getPower());
        }
        return result;
    }

    @Override
    public DcMotor.ZeroPowerBehavior	getZeroPowerBehavior()
    {
        DcMotor.ZeroPowerBehavior result = DcMotor.ZeroPowerBehavior.UNKNOWN;
        if(mReady) { result = mFirst.getZeroPowerBehavior(); }
        return result;
    }

    @Override
    public boolean	                    isBusy()
    {
        boolean result = false;
        if(mReady) { result = (mFirst.isBusy() || mSecond.isBusy()); }
        return result;
    }

    @Override
    public void	                        setMode(DcMotor.RunMode mode)
    {
        if(mReady) {
            mFirst.setMode(mode);
            mSecond.setMode(mode);
        }
    }

    @Override
    public void	                        setDirection(DcMotorSimple.Direction direction)
    {
        if(direction != mDirection && mReady) {

            if(     mFirst.getDirection()  == DcMotor.Direction.FORWARD) { mFirst.setDirection(DcMotor.Direction.REVERSE);  }
            else if(mFirst.getDirection()  == DcMotor.Direction.REVERSE) { mFirst.setDirection(DcMotor.Direction.FORWARD);  }

            if(     mSecond.getDirection() == DcMotor.Direction.FORWARD) { mSecond.setDirection(DcMotor.Direction.REVERSE); }
            else if(mSecond.getDirection() == DcMotor.Direction.REVERSE) { mSecond.setDirection(DcMotor.Direction.FORWARD); }

            mDirection = direction;

        }
    }

    @Override
    public void	                        setTargetPosition(int position)
    {
        if(mReady) {
            mFirst.setTargetPosition(mFirstInvertPosition * position);
            mSecond.setTargetPosition(mSecondInvertPosition * position);
        }
    }

    @Override
    public void	                        setZeroPowerBehavior(DcMotor.ZeroPowerBehavior zeroPowerBehavior)
    {
        if(mReady) {
            mFirst.setZeroPowerBehavior(zeroPowerBehavior);
            mSecond.setZeroPowerBehavior(zeroPowerBehavior);
        }
    }

    @Override
    public void	                        setPower(double power)
    {
        if(mReady) {
            mFirst.setPower(power);
            mSecond.setPower(power);
        }
    }

    /* -------------------- DcMotorEx functions -------------------- */

    @Override
    public PIDFCoefficients             getPIDFCoefficients(DcMotor.RunMode mode){
        PIDFCoefficients result = null;
        if(mReady) {
            result = mSecond.getPIDFCoefficients(mode);
        }
        return result;
    }

    @Override
    public void                        setPIDFCoefficients(DcMotor.RunMode mode, PIDFCoefficients pidfCoefficients){
        if(mReady) {
            mFirst.setPIDFCoefficients(mode, pidfCoefficients);
            mSecond.setPIDFCoefficients(mode, pidfCoefficients);
        }
    }

    @Override
    public void                        setTargetPositionTolerance(int tolerance)
    {
        if(mReady) {
            mFirst.setTargetPositionTolerance(tolerance);
            mSecond.setTargetPositionTolerance(tolerance);
        }
    }

    @Override
    public int                         getTargetPositionTolerance()
    {
        int result = -1;
        if(mReady) {
            result = mSecond.getTargetPositionTolerance();
        }
        return result;

    }

    @Override
    public double                         getVelocity()
    {
        double result = 0;
        if(mReady) {
            result = 0.5 * mSecond.getVelocity() + 0.5 * mFirst.getVelocity();
        }
        return result;

    }

    @Override
    public String                      logPositions()
    {
        String result = "";
        if(mReady) {
            result += "\n  First : P : " + mFirst.getCurrentPosition() + " V : " + mFirst.getVelocity() + " P : " + mFirst.getPower();
            result += "\n  Second : P : " + mSecond.getCurrentPosition() + " V : " + mSecond.getVelocity() + " P : " + mSecond.getPower();
        }
        return result;
    }

}
