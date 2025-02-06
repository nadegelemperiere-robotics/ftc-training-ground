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

public class TwoDeadWheelsOdometer implements OdometerComponent {

    static  final String sParHwMapKey   = "par";
    static  final String sPerpHwMapKey  = "perp";
    static  final String sParTicksKey   = "par-y-ticks";
    static  final String sPerpTicksKey  = "perp-x-ticks";
    static  final String sInPerTickKey  = "in-per-tick";

    Logger                      mLogger;

    boolean                     mReady;
    boolean                     mIsFirstTime;

    Encoder                     mPar;
    Encoder                     mPerp;
    ImuComponent                mImu;

    Pose2d                      mInitialPose;
    Pose2d                      mCurrentPose;
    public LinkedList<Pose2d>   mPoseHistory;

    double                      mParYTicks;
    double                      mPerpXTicks;
    double                      mInPerTick;

    double                      mLastHeading;
    double                      mLastHeadingVel;
    double                      mVelocityOffset;
    double                      mLastParPos;
    double                      mLastPerpPos;


    public  TwoDeadWheelsOdometer(ConfOdometer config, Map<String, ConfMotor> motors, ImuComponent imu, HardwareMap hwMap, Logger logger) {

        mLogger         = logger;
        mReady          = true;
        mIsFirstTime    = true;

        mCurrentPose = new Pose2d(new Vector2d(0,0),0);
        mPoseHistory = new LinkedList<>();

        mPar = null;
        mPerp = null;
        mImu = imu;

        if(motors.containsKey(sParHwMapKey)) {
            List<ConfMotor.Controller> conf = motors.get(sParHwMapKey).controllers();
            if(conf.size() == 1) {
                DcMotorEx motor = hwMap.tryGet(DcMotorEx.class,conf.get(0).mapName());
                if(motor != null) { mPar = new OverflowEncoder(new RawEncoder(motor)); }
                if(mPar != null && MotorComponent.sString2Direction.containsKey(conf.get(0).direction())) {
                    mPar.setDirection(Objects.requireNonNull(MotorComponent.sString2Direction.get(conf.get(0).direction())));
                }
                else if(mPar != null)                {
                    mPar.setDirection(DcMotorSimple.Direction.FORWARD);
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

        if(mPar == null)  { mReady = false; }
        if(mPerp == null) { mReady = false; }
        if(mImu == null)  { mReady = false; }

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
        if (parameters.containsKey(sInPerTickKey)) {
            Double param = parameters.get(sInPerTickKey);
            if(param != null) { mInPerTick = param; }
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

            Twist2dDual<Time> twist;

            PositionVelocityPair parPosVel = mPar.getPositionAndVelocity();
            PositionVelocityPair perpPosVel = mPerp.getPositionAndVelocity();

            double heading = mImu.heading();
            double headingVelocity = mImu.headingVelocity();
            if (Math.abs(headingVelocity - mLastHeadingVel) > Math.PI) {
                mVelocityOffset -= Math.signum(headingVelocity) * 2 * Math.PI;
            }
            mLastHeadingVel = headingVelocity;
            headingVelocity += mVelocityOffset;

            if (mIsFirstTime) {
                mIsFirstTime = false;

                twist = new Twist2dDual<>(
                        Vector2dDual.constant(new Vector2d(0.0, 0.0), 2),
                        DualNum.constant(0.0, 2)
                );
            }
            else {

                double parPosDelta  = parPosVel.position - mLastParPos;
                double perpPosDelta = perpPosVel.position - mLastPerpPos;
                double headingDelta = heading - mLastHeading;

                twist = new Twist2dDual<>(
                        new Vector2dDual<>(
                                new DualNum<Time>(new double[]{
                                        parPosDelta - mParYTicks * headingDelta,
                                        parPosVel.velocity - mParYTicks * headingVelocity,
                                }).times(mInPerTick),
                                new DualNum<Time>(new double[]{
                                        perpPosDelta - mPerpXTicks * headingDelta,
                                        perpPosVel.velocity - mPerpXTicks * headingVelocity,
                                }).times(mInPerTick)
                        ),
                        new DualNum<>(new double[]{
                                headingDelta,
                                headingVelocity,
                        })
                );
            }

            mLastParPos = parPosVel.position;
            mLastPerpPos = perpPosVel.position;
            mLastHeading = heading;

            mCurrentPose = mCurrentPose.plus(twist.value());

            mPoseHistory.add(mCurrentPose);
            while (mPoseHistory.size() > 100) {
                mPoseHistory.removeFirst();
            }

        }
    }

    @Override
    public Pose2d     getPose() {
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
