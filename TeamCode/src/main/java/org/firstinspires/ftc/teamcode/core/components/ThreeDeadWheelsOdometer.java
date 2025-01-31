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

public class ThreeDeadWheelsOdometer implements OdometerComponent {

    static  final String sPar0HwMapKey  = "par0";
    static  final String sPar1HwMapKey  = "par1";
    static  final String sPerpHwMapKey  = "perp";
    static  final String sPar0TicksKey  = "par0-y-ticks";
    static  final String sPar1TicksKey  = "par1-y-ticks";
    static  final String sPerpTicksKey  = "perp-x-ticks";

    Logger                  mLogger;

    boolean                 mReady;

    Encoder                 mPar0;
    Encoder                 mPar1;
    Encoder                 mPerp;

    Pose2d                  mInitialPose;
    Pose2d                  mCurrentPose;

    double                  mPar0YTicks;
    double                  mPar1YTicks;
    double                  mPerpXTicks;

    public  ThreeDeadWheelsOdometer(ConfOdometer config, HardwareMap hwMap, Logger logger) {

        mLogger = logger;
        mReady  = true;

        mPar0 = null;
        mPar1 = null;
        mPerp = null;

        Map<String,String> hw = config.mapName();
        if(hw.containsKey(sPar0HwMapKey)) {
            DcMotorEx motor = hwMap.tryGet(DcMotorEx.class,hw.get(sPar0HwMapKey));
            if(motor != null) { mPar0 = new OverflowEncoder(new RawEncoder(motor)); }
        }
        if(hw.containsKey(sPar1HwMapKey)) {
            DcMotorEx motor = hwMap.tryGet(DcMotorEx.class,hw.get(sPar1HwMapKey));
            if(motor != null) { mPar1 = new OverflowEncoder(new RawEncoder(motor)); }
        }
        if(hw.containsKey(sPerpHwMapKey)) {
            DcMotorEx motor = hwMap.tryGet(DcMotorEx.class,hw.get(sPerpHwMapKey));
            if(motor != null) { mPerp = new OverflowEncoder(new RawEncoder(motor)); }
        }

        if(mPar0 == null) { mReady = false; }
        if(mPar1 == null) { mReady = false; }
        if(mPerp == null) { mReady = false; }

        mPar0YTicks = 0.0;
        mPar1YTicks = 1.0;
        mPerpXTicks = 0.0;

        Map<String, Double> parameters = config.parameters();
        if (parameters.containsKey(sPar0TicksKey)) {
            Double param = parameters.get(sPar0TicksKey);
            if(param != null) { mPar0YTicks = param; }
        }
        if (parameters.containsKey(sPar1TicksKey)) {
            Double param = parameters.get(sPar1TicksKey);
            if(param != null) { mPar1YTicks = param; }
        }
        if (parameters.containsKey(sPerpTicksKey)) {
            Double param = parameters.get(sPerpTicksKey);
            if(param != null) { mPerpXTicks = param; }
        }

        Pose2d origin = new Pose2d(new Vector2d(0,0),0);

        this.setPose(origin);
    }

    @Override
    public boolean      isReady() { return mReady;}

    @Override
    public void         setPose(Pose2d current) {
        if(mReady) {
            mInitialPose = current;
        }
    }

    @Override
    public void         update() {
        if (mReady) {

        }
    }

    @Override
    public Pose2d       getPose() {
        return mCurrentPose;
    }

    @Override
    public void         log() {
        if (mReady) {
        }
    }
}
