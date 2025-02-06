/* -------------------------------------------------------
   Copyright (c) [2025] Nadege LEMPERIERE
   All rights reserved
   -------------------------------------------------------
   Localization using OTOS sparkfun component
   ------------------------------------------------------- */

package org.firstinspires.ftc.teamcode.core.components.odometers;

/* System includes */
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/* Qualcomm includes */
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.DcMotorEx;

/* ACME robotics includes */
import com.acmerobotics.roadrunner.DualNum;
import com.acmerobotics.roadrunner.Time;
import com.acmerobotics.roadrunner.Twist2dDual;
import com.acmerobotics.roadrunner.Vector2dDual;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.Vector2d;
import com.acmerobotics.roadrunner.ftc.PositionVelocityPair;
import com.acmerobotics.roadrunner.ftc.Encoder;
import com.acmerobotics.roadrunner.ftc.OverflowEncoder;
import com.acmerobotics.roadrunner.ftc.RawEncoder;

/* Tools includes */
import org.firstinspires.ftc.teamcode.core.tools.Logger;

/* Configuration includes */
import org.firstinspires.ftc.teamcode.core.configuration.ConfOdometer;
import org.firstinspires.ftc.teamcode.core.configuration.ConfMotor;

public class ThreeDeadWheelsOdometer implements OdometerComponent {

    static  final String    sPar0HwMapKey  = "par0";
    static  final String    sPar1HwMapKey  = "par1";
    static  final String    sPerpHwMapKey  = "perp";
    static  final String    sPar0TicksKey  = "par0-y-ticks";
    static  final String    sPar1TicksKey  = "par1-y-ticks";
    static  final String    sPerpTicksKey  = "perp-x-ticks";
    static  final String    sInPerTickKey  = "in-per-tick";

    Logger                      mLogger;

    boolean                     mReady;
    boolean                     mIsFirstTime;

    Encoder                     mPar0;
    Encoder                     mPar1;
    Encoder                     mPerp;

    Pose2d                      mInitialPose;
    Pose2d                      mCurrentPose;
    public LinkedList<Pose2d>   mPoseHistory;

    double                      mPar0YTicks;
    double                      mPar1YTicks;
    double                      mPerpXTicks;
    double                      mInPerTick;

    double                      mLastPar0Pos;
    double                      mLastPar1Pos;
    double                      mLastPerpPos;

    public  ThreeDeadWheelsOdometer(ConfOdometer config, Map<String, ConfMotor> motors, HardwareMap hwMap, Logger logger) {

        mLogger = logger;
        mReady  = true;

        mCurrentPose = new Pose2d(new Vector2d(0,0),0);
        mPoseHistory = new LinkedList<>();

        mPar0 = null;
        mPar1 = null;
        mPerp = null;

        if(motors.containsKey(sPar0HwMapKey)) {
            List<ConfMotor.Controller> conf = motors.get(sPar0HwMapKey).controllers();
            if(conf.size() == 1) {
                DcMotorEx motor = hwMap.tryGet(DcMotorEx.class,conf.get(0).mapName());
                if(motor != null) { mPar0 = new OverflowEncoder(new RawEncoder(motor)); }
                if(mPar0 != null && MotorComponent.sString2Direction.containsKey(conf.get(0).direction())) {
                    mPar0.setDirection(Objects.requireNonNull(MotorComponent.sString2Direction.get(conf.get(0).direction())));
                }
                else if(mPar0 != null)                {
                    mPar0.setDirection(DcMotorSimple.Direction.FORWARD);
                }
            }
        }
        if(motors.containsKey(sPar1HwMapKey)) {
            List<ConfMotor.Controller> conf = motors.get(sPar1HwMapKey).controllers();
            if(conf.size() == 1) {
                DcMotorEx motor = hwMap.tryGet(DcMotorEx.class,conf.get(0).mapName());
                if(motor != null) { mPar1 = new OverflowEncoder(new RawEncoder(motor)); }
                if(mPar1 != null && MotorComponent.sString2Direction.containsKey(conf.get(0).direction())) {
                    mPar1.setDirection(Objects.requireNonNull(MotorComponent.sString2Direction.get(conf.get(0).direction())));
                }
                else if(mPar1 != null)                {
                    mPar1.setDirection(DcMotorSimple.Direction.FORWARD);
                }
            }
        }
        if(motors.containsKey(sPerpHwMapKey)) {
            List<ConfMotor.Controller> conf = motors.get(sPerpHwMapKey).controllers();
            if(conf.size() == 1) {
                DcMotorEx motor = hwMap.tryGet(DcMotorEx.class,conf.get(0).mapName());
                if(motor != null) { mPerp = new OverflowEncoder(new RawEncoder(motor)); }
                if(mPerp != null && MotorComponent.sString2Direction.containsKey(conf.get(0).direction())) {
                    mPerp.setDirection(Objects.requireNonNull(MotorComponent.sString2Direction.get(conf.get(0).direction())));
                }
                else if(mPerp != null)                {
                    mPerp.setDirection(DcMotorSimple.Direction.FORWARD);
                }
            }
        }

        if(mPar0 == null) { mReady = false; }
        if(mPar1 == null) { mReady = false; }
        if(mPerp == null) { mReady = false; }

        mPar0YTicks = 0.0;
        mPar1YTicks = 1.0;
        mPerpXTicks = 0.0;
        mInPerTick  = 1.0;

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
        if (parameters.containsKey(sInPerTickKey)) {
            Double param = parameters.get(sInPerTickKey);
            if(param != null) { mInPerTick = param; }
        }

        Pose2d origin = new Pose2d(new Vector2d(0,0),0);

        this.setPose(origin);
    }

    @Override
    public boolean      isReady() { return mReady;}

    @Override
    public void         setPose(Pose2d current) {
        if(mReady) {
            mIsFirstTime = true;
            mInitialPose = current;
        }
    }

    @Override
    public void         update() {
        if (mReady) {

            Twist2dDual<Time> twist;

            PositionVelocityPair par0PosVel = mPar0.getPositionAndVelocity();
            PositionVelocityPair par1PosVel = mPar1.getPositionAndVelocity();
            PositionVelocityPair perpPosVel = mPerp.getPositionAndVelocity();

            if (mIsFirstTime) {
                mIsFirstTime = false;

                twist =  new Twist2dDual<>(
                        Vector2dDual.constant(new Vector2d(0.0, 0.0), 2),
                        DualNum.constant(0.0, 2)
                );
            }
            else {

                double par0PosDelta = par0PosVel.position - mLastPar0Pos;
                double par1PosDelta = par1PosVel.position - mLastPar1Pos;
                double perpPosDelta = perpPosVel.position - mLastPerpPos;

                twist = new Twist2dDual<>(
                        new Vector2dDual<>(
                                new DualNum<Time>(new double[]{
                                        (mPar0YTicks * par1PosDelta - mPar1YTicks * par0PosDelta) / (mPar0YTicks - mPar1YTicks),
                                        (mPar0YTicks * par1PosVel.velocity - mPar1YTicks * par0PosVel.velocity) / (mPar0YTicks - mPar1YTicks),
                                }).times(mInPerTick),
                                new DualNum<Time>(new double[]{
                                        (mPerpXTicks / (mPar0YTicks - mPar1YTicks) * (par1PosDelta - par0PosDelta) + perpPosDelta),
                                        (mPerpXTicks / (mPar0YTicks - mPar1YTicks) * (par1PosVel.velocity - par0PosVel.velocity) + perpPosVel.velocity),
                                }).times(mInPerTick)
                        ),
                        new DualNum<>(new double[]{
                                (par0PosDelta - par1PosDelta) / (mPar0YTicks - mPar1YTicks),
                                (par0PosVel.velocity - par1PosVel.velocity) / (mPar0YTicks - mPar1YTicks),
                        })
                );

            }

            mLastPar0Pos = par0PosVel.position;
            mLastPar1Pos = par1PosVel.position;
            mLastPerpPos = perpPosVel.position;

            mCurrentPose = mCurrentPose.plus(twist.value());

            mPoseHistory.add(mCurrentPose);
            while (mPoseHistory.size() > 100) {
                mPoseHistory.removeFirst();
            }

        }
    }

    @Override
    public Pose2d       getPose() {
        return mCurrentPose;
    }

    @Override
    public void         log() {
        if (mReady) {
            mLogger.addData("x", mCurrentPose.position.x + " inches");
            mLogger.addData("y", mCurrentPose.position.y + " inches");
            mLogger.addData("heading", mCurrentPose.heading.toDouble() + " rad");
        }
    }
}
