/* -------------------------------------------------------
   Copyright (c) [2025] Nadege LEMPERIERE
   All rights reserved
   -------------------------------------------------------
   Localization using OTOS sparkfun component
   ------------------------------------------------------- */

package org.firstinspires.ftc.teamcode.core.components.odometers;

/* System includes */
import java.util.LinkedList;
import java.util.Map;
import java.util.List;
import java.util.Objects;

/* Qualcomm includes */
import com.acmerobotics.roadrunner.ftc.PositionVelocityPair;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.DcMotorEx;

/* ACME robotics includes */
import com.acmerobotics.roadrunner.ftc.Encoder;
import com.acmerobotics.roadrunner.ftc.OverflowEncoder;
import com.acmerobotics.roadrunner.ftc.RawEncoder;
import com.acmerobotics.roadrunner.MecanumKinematics;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.Vector2d;
import com.acmerobotics.roadrunner.DualNum;
import com.acmerobotics.roadrunner.Time;
import com.acmerobotics.roadrunner.Twist2dDual;
import com.acmerobotics.roadrunner.Vector2dDual;

/* Tools includes */
import org.firstinspires.ftc.teamcode.core.tools.Logger;

/* Configuration includes */
import org.firstinspires.ftc.teamcode.core.configuration.ConfOdometer;
import org.firstinspires.ftc.teamcode.core.configuration.ConfMotor;

public class DriveEncodersOdometer implements OdometerComponent {

    static  final String    sLeftFrontHwMapKey   = "front-left-wheel";
    static  final String    sLeftBackHwMapKey    = "back-left-wheel";
    static  final String    sRightFrontHwMapKey  = "front-right-wheel";
    static  final String    sRightBackHwMapKey   = "back-right-wheel";
    static  final String    sTrackWidthTicksKey  = "track-width-ticks";
    static  final String    sLateralInPerTickKey = "lateral-in-per-tick";
    static  final String    sInPerTickKey        = "in-per-tick";

    Logger                      mLogger;

    boolean                     mReady;
    boolean                     mIsFirstTime;

    Encoder                     mLeftFront;
    Encoder                     mRightFront;
    Encoder                     mLeftBack;
    Encoder                     mRightBack;
    ImuComponent                mImu;

    Pose2d                      mInitialPose;
    Pose2d                      mCurrentPose;
    public LinkedList<Pose2d>   mPoseHistory;

    double                      mInPerTick;
    double                      mTrackWidthTicks;
    double                      mLateralInPerTick;

    double                      mLastHeading;
    double                      mLastLeftFrontPos;
    double                      mLastLeftBackPos;
    double                      mLastRightFrontPos;
    double                      mLastRightBackPos;

    MecanumKinematics           mKinematics;

    public  DriveEncodersOdometer(ConfOdometer config, Map<String, ConfMotor> motors, ImuComponent imu, HardwareMap hwMap, Logger logger) {

        mLogger      = logger;
        mReady       = true;
        mIsFirstTime = true;

        mCurrentPose = new Pose2d(new Vector2d(0,0),0);
        mPoseHistory = new LinkedList<>();

        mLeftFront  = null;
        mLeftBack   = null;
        mRightFront = null;
        mRightBack  = null;
        mImu        = imu;

        if(motors.containsKey(sLeftFrontHwMapKey)) {
            List<ConfMotor.Controller> conf = motors.get(sLeftFrontHwMapKey).controllers();
            if(conf.size() == 1) {
                DcMotorEx motor = hwMap.tryGet(DcMotorEx.class,conf.get(0).mapName());
                if(motor != null) { mLeftFront = new OverflowEncoder(new RawEncoder(motor)); }
                if(mLeftFront != null && MotorComponent.sString2Direction.containsKey(conf.get(0).direction())) {
                    mLeftFront.setDirection(Objects.requireNonNull(MotorComponent.sString2Direction.get(conf.get(0).direction())));
                }
                else if(mLeftFront != null)                {
                    mLeftFront.setDirection(DcMotorSimple.Direction.FORWARD);
                }
            }
        }
        if(motors.containsKey(sLeftBackHwMapKey)) {
            List<ConfMotor.Controller> conf = motors.get(sLeftBackHwMapKey).controllers();
            if(conf.size() == 1) {
                DcMotorEx motor = hwMap.tryGet(DcMotorEx.class,conf.get(0).mapName());
                if(motor != null) { mLeftBack = new OverflowEncoder(new RawEncoder(motor)); }
                if(mLeftBack != null && MotorComponent.sString2Direction.containsKey(conf.get(0).direction())) {
                    mLeftBack.setDirection(Objects.requireNonNull(MotorComponent.sString2Direction.get(conf.get(0).direction())));
                }
                else if(mLeftBack != null)                {
                    mLeftBack.setDirection(DcMotorSimple.Direction.FORWARD);
                }
            }
        }
        if(motors.containsKey(sRightFrontHwMapKey)) {
            List<ConfMotor.Controller> conf = motors.get(sRightFrontHwMapKey).controllers();
            if(conf.size() == 1) {
                DcMotorEx motor = hwMap.tryGet(DcMotorEx.class,conf.get(0).mapName());
                if(motor != null) { mRightFront = new OverflowEncoder(new RawEncoder(motor)); }
                if(mRightFront != null && MotorComponent.sString2Direction.containsKey(conf.get(0).direction())) {
                    mRightFront.setDirection(Objects.requireNonNull(MotorComponent.sString2Direction.get(conf.get(0).direction())));
                }
                else if(mRightFront != null)                {
                    mRightFront.setDirection(DcMotorSimple.Direction.FORWARD);
                }
            }
        }
        if(motors.containsKey(sRightBackHwMapKey)) {
            List<ConfMotor.Controller> conf = motors.get(sRightBackHwMapKey).controllers();
            if(conf.size() == 1) {
                DcMotorEx motor = hwMap.tryGet(DcMotorEx.class,conf.get(0).mapName());
                if(motor != null) { mRightBack = new OverflowEncoder(new RawEncoder(motor)); }
                if(mRightBack != null && MotorComponent.sString2Direction.containsKey(conf.get(0).direction())) {
                    mRightBack.setDirection(Objects.requireNonNull(MotorComponent.sString2Direction.get(conf.get(0).direction())));
                }
                else if(mRightBack != null)                {
                    mRightBack.setDirection(DcMotorSimple.Direction.FORWARD);
                }
            }
        }

        if(mLeftFront == null)  { mReady = false; }
        if(mLeftBack == null)   { mReady = false; }
        if(mRightFront == null) { mReady = false; }
        if(mRightBack == null)  { mReady = false; }

        mInPerTick  = 1.0;
        mLateralInPerTick  = 1.0;
        mTrackWidthTicks = 1.0;

        Map<String, Double> parameters = config.parameters();
        if (parameters.containsKey(sInPerTickKey)) {
            Double param = parameters.get(sInPerTickKey);
            if(param != null) { mInPerTick = param; }
        }
        if (parameters.containsKey(sTrackWidthTicksKey)) {
            Double param = parameters.get(sTrackWidthTicksKey);
            if(param != null) { mTrackWidthTicks = param; }
        }
        if (parameters.containsKey(sLateralInPerTickKey)) {
            Double param = parameters.get(sLateralInPerTickKey);
            if(param != null) { mLateralInPerTick = param; }
        }

        mKinematics = new MecanumKinematics(
                mInPerTick * mTrackWidthTicks, mInPerTick / mLateralInPerTick);

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

            PositionVelocityPair leftFrontPosVel = mLeftFront.getPositionAndVelocity();
            PositionVelocityPair leftBackPosVel = mLeftBack.getPositionAndVelocity();
            PositionVelocityPair rightBackPosVel = mRightBack.getPositionAndVelocity();
            PositionVelocityPair rightFrontPosVel = mRightFront.getPositionAndVelocity();

            double heading = mImu.heading();

            if (!mIsFirstTime) {
                mIsFirstTime = true;


                twist = new Twist2dDual<>(
                        Vector2dDual.constant(new Vector2d(0.0, 0.0), 2),
                        DualNum.constant(0.0, 2)
                );
            }
            else {

                double headingDelta = heading - mLastHeading;
                twist = mKinematics.forward(new MecanumKinematics.WheelIncrements<>(
                        new DualNum<Time>(new double[]{
                                (leftFrontPosVel.position - mLastLeftFrontPos),
                                leftFrontPosVel.velocity,
                        }).times(mInPerTick),
                        new DualNum<Time>(new double[]{
                                (leftBackPosVel.position - mLastLeftBackPos),
                                leftBackPosVel.velocity,
                        }).times(mInPerTick),
                        new DualNum<Time>(new double[]{
                                (rightBackPosVel.position - mLastRightBackPos),
                                rightBackPosVel.velocity,
                        }).times(mInPerTick),
                        new DualNum<Time>(new double[]{
                                (rightFrontPosVel.position - mLastRightFrontPos),
                                rightFrontPosVel.velocity,
                        }).times(mInPerTick)
                ));

                twist = new Twist2dDual<>(
                        twist.line,
                        DualNum.cons(headingDelta, twist.angle.drop(1))
                );
            }


            mLastLeftFrontPos = leftFrontPosVel.position;
            mLastLeftBackPos = leftBackPosVel.position;
            mLastRightBackPos = rightBackPosVel.position;
            mLastRightFrontPos = rightFrontPosVel.position;

            mLastHeading = heading;

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
