/* -------------------------------------------------------
   Copyright (c) [2025] Nadege LEMPERIERE
   All rights reserved
   -------------------------------------------------------
   Motor tuning tool
   ------------------------------------------------------- */

package org.firstinspires.ftc.teamcode.tuning;

/* System includes */
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Map;
import java.util.List;

/* Android includes */
import android.os.Environment;

/* Qualcomm includes */
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

/* ACME robotics includes */
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.Rotation2d;
import com.acmerobotics.roadrunner.Vector2d;
import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.config.ValueProvider;

/* Tools includes */
import org.firstinspires.ftc.teamcode.core.components.OpticalTrackingOdometer;
import org.firstinspires.ftc.teamcode.core.tools.Logger;

/* Configuration includes */
import org.firstinspires.ftc.teamcode.core.configuration.ConfOdometer;
import org.firstinspires.ftc.teamcode.core.configuration.Configuration;

/* Components includes */
import org.firstinspires.ftc.teamcode.core.components.OdometerComponent;
import org.firstinspires.ftc.teamcode.core.components.OpticalTrackingOdometer;

@Config
@TeleOp(name = "OTOSTuning", group = "Tuning")
public class OTOSTuning extends LinearOpMode {

    public enum Tuning {
        HEADING_RATIO,
        HEADING_OFFSET,
        POSITION_RATIO,
        POSITION_OFFSET
    }

    public enum Step {
        STOP,
        INIT,
        PROCESSING,
        UPDATE
    }

    /* -------- Configuration variables -------- */
    public static Step                  STEP    = Step.STOP;
    public static Tuning                TUNING  = Tuning.HEADING_RATIO;

    /* ---------------- Members ---------------- */
    private Logger                      mLogger;
    private Tuning                      mPreviousTuning;

    private Configuration               mConfiguration;
    private Configuration               mUpdatedConfiguration;
    private ConfOdometer                mOtosConfiguration;
    private ConfOdometer                mPreviousOtosConfiguration;

    private OdometerComponent           mOdometer;

    private  double                     mHeadingRatioRadTurned;
    private  Rotation2d                 mHeadingRatioLastHeading;
    private  DistanceProvider           mPositionRatioDistance;

    /* -------------- Line / items -------------- */
    private  List<Logger.Item>          mTemporaryItems;



    @Override
    public void runOpMode() {

        try {

            mLogger = new Logger(null,FtcDashboard.getInstance());

            mPositionRatioDistance  = new DistanceProvider();

            mTemporaryItems = new ArrayList<>();

            mPreviousTuning = TUNING;

            mConfiguration = new Configuration(mLogger);
            mConfiguration.read();
            mConfiguration.log();
            mUpdatedConfiguration = new Configuration(mConfiguration);

            Map<String,ConfOdometer>    odometers = mUpdatedConfiguration.odometers();
            mOtosConfiguration = null;
            if(odometers.containsKey("otos")) {
                mOtosConfiguration = odometers.get("otos");
            }
            if(mOtosConfiguration == null) throw new InvalidParameterException("Not otos based odometer found in configuration");
            mPreviousOtosConfiguration = new ConfOdometer(mOtosConfiguration);

            for (Map.Entry<String, Double> param : mOtosConfiguration.parameters().entrySet()) {
                ParamProvider parameter = new ParamProvider(mOtosConfiguration.parameters(), param.getKey());
                FtcDashboard.getInstance().addConfigVariable(this.getClass().getSimpleName(),param.getKey(),parameter);
            }
            mOdometer = OdometerComponent.factory(mOtosConfiguration, null, null, hardwareMap, mLogger);

            FtcDashboard.getInstance().updateConfig();
            mLogger.update();

            waitForStart();

            mLogger.clear();

            while(opModeIsActive()) {

                // Manage configuration change
                if(!mPreviousOtosConfiguration.equals(mOtosConfiguration)) {
                    mLogger.addData("Configuration","changed");
                    FtcDashboard.getInstance().updateConfig();
                    mOdometer = OdometerComponent.factory(mOtosConfiguration, null, null, hardwareMap, mLogger);
                    mPreviousOtosConfiguration = new ConfOdometer(mOtosConfiguration);
                    STEP = Step.STOP;
                }

                // Manage tuning change
                if(mPreviousTuning != TUNING) {
                    mLogger.addData("items","" + mTemporaryItems.size());
                    for(int i_item = 0; i_item < mTemporaryItems.size(); i_item ++) {
                        mLogger.removeItem(mTemporaryItems.get(i_item));
                    }
                    mTemporaryItems.clear();
                    STEP = Step.STOP;
                    mPreviousTuning = TUNING;
                }

                // Manage step change
                String description;
                switch (TUNING) {
                    case HEADING_RATIO:
                        description = "<p style=\"font-weight: bold; font-size: 14px\"> ------------------------- </p>" +
                                "<p style=\"font-weight: bold; font-size: 14px\"> OTOS HEADING RATIO TUNER </p>" +
                                "<p style=\"font-size: 12px\"> Switch to PROCESSING and rotate the robot on the ground 10 times. </p>" +
                                "<p style=\"font-size: 12px\"> Make sure you mark the starting orientation precisely. </p>"+
                                "<p style=\"font-size: 12px\"> Make sure you fit exactly to this orientation at the end of your test. </p>" +
                                "<p style=\"font-size: 12px\"> Finally step to the UPDATE step to update the configuration with the value. </p>";
                        mLogger.addLine(description);
                        FtcDashboard.getInstance().removeConfigVariable(this.getClass().getSimpleName(),"DISTANCE");
                        this.processHeadingRatio(); break;
                    case HEADING_OFFSET:
                        description = "<p style=\"font-weight: bold; font-size: 14px\"> ------------------------- </p>" +
                                "<p style=\"font-weight: bold; font-size: 14px\"> OTOS HEADING OFFSET TUNER </p>" +
                                "<p style=\"font-size: 12px\"> Line the side of the robot against a wall and switch to PROCESSING </p>" +
                                "<p style=\"font-size: 12px\"> Then push the robot forward some distance. </p>"+
                                "<p style=\"font-size: 12px\"> Finally step to the UPDATE step to update the configuration with the value. </p>";
                        mLogger.addLine(description);
                        FtcDashboard.getInstance().removeConfigVariable(this.getClass().getSimpleName(),"DISTANCE");
                        this.processHeadingOffset(); break;
                    case POSITION_RATIO:
                        description = "<p style=\"font-weight: bold; font-size: 14px\"> ------------------------- </p>" +
                                "<p style=\"font-weight: bold; font-size: 14px\"> OTOS POSITION RATIO TUNER </p>" +
                                "<p style=\"font-size: 12px\"> Set the distance your about to move in the Tuning parameters and switch to PROCESSING </p>" +
                                "<p style=\"font-size: 12px\"> Then push the robot forward this same distance (make sure you measure it precisely). </p>" +
                                "<p style=\"font-size: 12px\"> Finally, step to the UPDATE step to update the configuration with the value. </p>";
                        mLogger.addLine(description);
                        FtcDashboard.getInstance().addConfigVariable(this.getClass().getSimpleName(),"DISTANCE",mPositionRatioDistance);
                        this.processPositionRatio(); break;
                    case POSITION_OFFSET:
                        description = "<p style=\"font-weight: bold; font-size: 14px\"> ------------------------- </p>" +
                                "<p style=\"font-weight: bold; font-size: 14px\"> OTOS POSITION OFFSET TUNER </p>" +
                                "<p style=\"font-size: 12px\"> Line the robot against the corner of two walls facing forward and switch to PROCESSING </p>" +
                                "<p style=\"font-size: 12px\"> Then rotate the robot exactly 180 degrees and press it back into the corner. </p>" +
                                "<p style=\"font-size: 12px\"> Finally, step to the UPDATE step to update the configuration with the value. </p>";
                        mLogger.addLine(description);
                        FtcDashboard.getInstance().removeConfigVariable(this.getClass().getSimpleName(),"DISTANCE");
                        this.processPositionOffset(); break;
                }


                mOdometer.update();
                mOdometer.log();

                mLogger.addLine(mOtosConfiguration.log());
                mUpdatedConfiguration.log();

                mLogger.update();
            }

            mUpdatedConfiguration.write(Environment.getExternalStorageDirectory().getPath()
                    + "/FIRST/otos-tuning.json");
            mLogger.addLine("Updated configuration saved. You may retrieve it using <b>adb pull /sdcard/FIRST/otos-tuning.json</b>");
            mLogger.update();
        }
        catch(Exception e) {
            mLogger.addLine(e.toString());
            mLogger.update();
        }
    }

    void processHeadingRatio() {

        switch(STEP) {
            case INIT:
                mHeadingRatioRadTurned = 0;
                mHeadingRatioLastHeading = Rotation2d.fromDouble(0);
                mOdometer.setPose(new Pose2d(new Vector2d(0, 0), 0));

                break;

            case PROCESSING:
                Pose2d pose = mOdometer.getPose();
                mHeadingRatioRadTurned += pose.heading.minus(mHeadingRatioLastHeading);
                mHeadingRatioLastHeading = pose.heading;
                mTemporaryItems.add(mLogger.addData("Uncorrected Degrees Turned", "" + Math.round(Math.toDegrees(mHeadingRatioRadTurned))));
                mTemporaryItems.add(mLogger.addData("Calculated Heading Ratio", "" + 3600 / Math.toDegrees(mHeadingRatioRadTurned)));
                break;

            case UPDATE :
                mOtosConfiguration.parameters().put(OpticalTrackingOdometer.sHeadingRatioKey, 3600 / Math.toDegrees(mHeadingRatioRadTurned));
                break;
        }

    }

    void processHeadingOffset() {

        Pose2d pose;

        switch(STEP) {
            case INIT :
                mOdometer.setPose(new Pose2d(new Vector2d(0,0),0));
                break;
            case PROCESSING :
                pose = mOdometer.getPose();
                double offset = Math.atan2(pose.position.y,pose.position.x);
                mTemporaryItems.add(mLogger.addData("Heading Offset (radians, enter this one into SparkFunOTOSDrive!)","" + offset));
                mTemporaryItems.add(mLogger.addData("Heading Offset (degrees)","" + Math.toDegrees(offset)));
                break;
            case UPDATE :
                pose = mOdometer.getPose();
                mOtosConfiguration.parameters().put(OpticalTrackingOdometer.sHeadingOffsetKey, Math.atan2(pose.position.y,pose.position.x));
                break;
        }

    }

    void processPositionRatio() {

        Pose2d pose;
        double distance;

        switch(STEP) {
            case INIT:

                mOdometer.setPose(new Pose2d(new Vector2d(0, 0), 0));
                break;
            case PROCESSING:
                pose = mOdometer.getPose();
                distance = Math.sqrt(pose.position.x * pose.position.x + pose.position.y * pose.position.y);
                mTemporaryItems.add(mLogger.addData("Uncorrected Inches Moved", "" + distance));
                mTemporaryItems.add(mLogger.addData("Calculated Position Ratio", "" + mPositionRatioDistance.get() / distance));
                break;
            case UPDATE:
                pose = mOdometer.getPose();
                distance = Math.sqrt(pose.position.x * pose.position.x + pose.position.y * pose.position.y);
                mOtosConfiguration.parameters().put(OpticalTrackingOdometer.sPositionRatioKey, mPositionRatioDistance.get() / distance);
                break;
        }

    }

    void processPositionOffset() {

        Pose2d pose;

        switch(STEP) {
            case STOP:
                break;
            case INIT:
                mOdometer.setPose(new Pose2d(new Vector2d(0, 0), 0));
                break;
            case PROCESSING:
                pose = mOdometer.getPose();
                if (Math.abs(Math.toDegrees(pose.heading.toDouble())) > 175) {
                    mTemporaryItems.add(mLogger.addData("X Offset", "" + -0.5 * pose.position.x));
                    mTemporaryItems.add(mLogger.addData("Y Offset", "" + -0.5 * pose.position.y));
                }
                else {
                    mLogger.addLine( "<p style=\"font-size: 12px\">Rotate the robot 180 degrees and align it to the corner again.</p>");
                }
                break;
            case UPDATE:
                pose = mOdometer.getPose();
                mOtosConfiguration.parameters().put(OpticalTrackingOdometer.sXOffsetKey, -0.5 * pose.position.x);
                mOtosConfiguration.parameters().put(OpticalTrackingOdometer.sYOffsetKey, -0.5 * pose.position.y);
                break;
        }

    }



    // Since mode is a simple type, even with the appropriate constructor,
    // mMode will only be updated locally by the dashboard.
    // We'll have to make sure the code access the mode from the provider using the get
    // Method, since it's the only place the pdated information can be found
    static class ParamProvider implements ValueProvider<Double> {
        Map<String,Double>  mParameters;
        String              mKey;
        public ParamProvider(Map<String,Double> parameters, String key) {
            mParameters = parameters;
            mKey = key;
        }
        @Override
        public Double get() {
            double result = 0;
            if (mParameters.containsKey(mKey)) {
                Double param = mParameters.get(mKey);
                if(param != null) { result = param; }
            }
            return result;
        }
        @Override
        public void set(Double Value)    {
            if (mParameters.containsKey(mKey)) {
                mParameters.put(mKey, Value);
            }
        }
    }


    // Since double is a simple type, even with the appropriate constructor,
    // mDistance will only be updated locally by the dashboard.
    // We'll have to make sure the code access the mode from the provider using the get
    // Method, since it's the only place the updated information can be found
    static class DistanceProvider implements ValueProvider<Double> {
        double mDistance;
        @Override
        public Double   get()                { return mDistance;  }
        @Override
        public void     set(Double Value)    { mDistance = Value; }
    }
}