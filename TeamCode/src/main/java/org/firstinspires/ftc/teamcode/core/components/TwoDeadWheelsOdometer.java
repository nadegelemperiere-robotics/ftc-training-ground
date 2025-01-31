/* -------------------------------------------------------
   Copyright (c) [2025] Nadege LEMPERIERE
   All rights reserved
   -------------------------------------------------------
   Localization using OTOS sparkfun component
   ------------------------------------------------------- */

package org.firstinspires.ftc.teamcode.core.components;

/* System includes */
import java.util.Map;

/* Qualcomm includes */
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.DcMotorEx;

/* ACME robotics includes */
import com.acmerobotics.roadrunner.ftc.Encoder;
import com.acmerobotics.roadrunner.ftc.OverflowEncoder;
import com.acmerobotics.roadrunner.ftc.RawEncoder;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.Vector2d;

/* Tools includes */
import org.firstinspires.ftc.teamcode.core.tools.Logger;

/* Configuration includes */
import org.firstinspires.ftc.teamcode.core.configuration.ConfOdometer;

public class TwoDeadWheelsOdometer implements OdometerComponent {

    static  final String sParHwMapKey   = "par";
    static  final String sPerpHwMapKey  = "perp";
    static  final String sParTicksKey   = "par-y-ticks";
    static  final String sPerpTicksKey  = "perp-x-ticks";

    Logger                  mLogger;

    boolean                 mReady;

    Encoder                 mPar;
    Encoder                 mPerp;

    Pose2d                  mInitialPose;
    Pose2d                  mCurrentPose;

    double                  mParYTicks;
    double                  mPerpXTicks;

    public  TwoDeadWheelsOdometer(ConfOdometer config, HardwareMap hwMap, Logger logger) {


        mLogger = logger;
        mReady  = true;

        mPar = null;
        mPerp = null;

        Map<String,String> hw = config.mapName();
        if(hw.containsKey(sParHwMapKey)) {
            DcMotorEx motor = hwMap.tryGet(DcMotorEx.class,hw.get(sParHwMapKey));
            if(motor != null) { mPar = new OverflowEncoder(new RawEncoder(motor)); }
        }
        if(hw.containsKey(sPerpHwMapKey)) {
            DcMotorEx motor = hwMap.tryGet(DcMotorEx.class,hw.get(sPerpHwMapKey));
            if(motor != null) { mPerp = new OverflowEncoder(new RawEncoder(motor)); }
        }

        if(mPar == null)  { mReady = false; }
        if(mPerp == null) { mReady = false; }

        mParYTicks = 0.0;
        mPerpXTicks = 0.0;

        Map<String, Double> parameters = config.parameters();
        if (parameters.containsKey(sParTicksKey)) {
            Double param = parameters.get(sParTicksKey);
            if(param != null) { mParYTicks = param; }
        }
        if (parameters.containsKey(sPerpTicksKey)) {
            Double param = parameters.get(sPerpTicksKey);
            if(param != null) { mPerpXTicks = param; }
        }

        Pose2d origin = new Pose2d(new Vector2d(0,0),0);

        this.setPose(origin);
    }

    @Override
    public boolean  isReady() { return mReady;}

    @Override
    public void     setPose(Pose2d current) {
        if(mReady) {
            mInitialPose = current;
        }
    }

    @Override
    public void     update() {
        if (mReady) {

        }
    }

    @Override
    public Pose2d     getPose() {
        return mCurrentPose;
    }

    @Override
    public void         log() {
        if (mReady) {
        }
    }
}
