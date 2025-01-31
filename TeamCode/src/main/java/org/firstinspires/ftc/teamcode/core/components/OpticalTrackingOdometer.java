/* -------------------------------------------------------
   Copyright (c) [2025] Nadege LEMPERIERE
   All rights reserved
   -------------------------------------------------------
   Localization using OTOS sparkfun component
   ------------------------------------------------------- */

package org.firstinspires.ftc.teamcode.core.components;

/* System includes */
import java.util.LinkedList;
import java.util.Map;

/* Qualcomm includes */
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.hardware.sparkfun.SparkFunOTOS;

/* ACME robotics includes */
import com.acmerobotics.roadrunner.ftc.SparkFunOTOSCorrected;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.Vector2d;
import static com.acmerobotics.roadrunner.ftc.OTOSKt.OTOSPoseToRRPose;
import static com.acmerobotics.roadrunner.ftc.OTOSKt.RRPoseToOTOSPose;

/* FTC controller includes */
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

/* Tools includes */
import org.firstinspires.ftc.teamcode.core.tools.Logger;

/* Configuration includes */
import org.firstinspires.ftc.teamcode.core.configuration.ConfOdometer;

public class OpticalTrackingOdometer implements OdometerComponent {

    static  final String sHwMapKey          = "hwmap";
    static  final String sHeadingRatioKey   = "heading-ratio";
    static  final String sPositionRatioKey  = "position-ratio";
    static  final String sXOffsetKey        = "x-offset";
    static  final String sYOffsetKey        = "y-offset";
    static  final String sHeadingOffsetKey  = "heading-offset";

    Logger                      mLogger;

    boolean                     mReady;

    SparkFunOTOSCorrected       mOtos;

    Pose2d                      mCurrentPose;
    public LinkedList<Pose2d>   mPoseHistory;

    public  OpticalTrackingOdometer(ConfOdometer config, HardwareMap hwMap, Logger logger) {

        mLogger = logger;
        mReady  = true;

        mOtos        = null;
        mCurrentPose = new Pose2d(new Vector2d(0,0),0);
        mPoseHistory = new LinkedList<>();

        Map<String,String> hw = config.mapName();
        if(hw.containsKey(sHwMapKey)) {
            mOtos = hwMap.tryGet(SparkFunOTOSCorrected.class,hw.get(sHwMapKey));
            if(mOtos == null) { mLogger.addLine("Could not find sensor " + hw.get(sHwMapKey) + " in configuration"); }
        }

        if(mOtos == null) { mReady = false; }

        SparkFunOTOS.Pose2D offset = new SparkFunOTOS.Pose2D(0,0,0);
        double headingRatio = 1.0;
        double positionRatio = 1.0;

        Map<String, Double> parameters = config.parameters();
        if (parameters.containsKey(sHeadingRatioKey)) {
            Double param = parameters.get(sHeadingRatioKey);
            if(param != null) { headingRatio = param; }
        }
        if (parameters.containsKey(sPositionRatioKey)) {
            Double param = parameters.get(sPositionRatioKey);
            if(param != null) { positionRatio = param; }
        }
        if (parameters.containsKey(sXOffsetKey)) {
            Double param = parameters.get(sXOffsetKey);
            if(param != null) { offset = new SparkFunOTOS.Pose2D(param,offset.y,offset.h); }
        }
        if (parameters.containsKey(sYOffsetKey)) {
            Double param = parameters.get(sYOffsetKey);
            if(param != null) { offset = new SparkFunOTOS.Pose2D(offset.x,param,offset.h); }
        }
        if (parameters.containsKey(sHeadingOffsetKey)) {
            Double param = parameters.get(sHeadingOffsetKey);
            if(param != null) { offset = new SparkFunOTOS.Pose2D(offset.x,offset.y,param); }
        }

        if(mReady) {
            mOtos.setLinearUnit(DistanceUnit.INCH);
            mOtos.setAngularUnit(AngleUnit.RADIANS);

            mOtos.setOffset(offset);
            mOtos.setLinearScalar(positionRatio);
            mOtos.setAngularScalar(headingRatio);
        }

        Pose2d origin = new Pose2d(new Vector2d(0,0),0);

        this.setPose(origin);
    }

    @Override
    public boolean  isReady() {
        return mReady;
    }

    @Override
    public void     setPose(Pose2d current) {
        if(mReady) {
            mOtos.setPosition(RRPoseToOTOSPose(current));
        }
    }

    @Override
    public void     update() {
        if(mReady) {

            SparkFunOTOS.Pose2D otosPose = new SparkFunOTOS.Pose2D();
            SparkFunOTOS.Pose2D otosVel = new SparkFunOTOS.Pose2D();
            SparkFunOTOS.Pose2D otosAcc = new SparkFunOTOS.Pose2D();

            mOtos.getPosVelAcc(otosPose,otosVel,otosAcc);
            mCurrentPose = OTOSPoseToRRPose(otosPose);

            // RR standard
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
            mLogger.addData("x","" + mCurrentPose.position.x + " inches");
            mLogger.addData("y","" + mCurrentPose.position.y + " inches");
            mLogger.addData("heading","" + mCurrentPose.heading.toDouble() + " rad");
        }
    }
}
