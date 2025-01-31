/* -------------------------------------------------------
   Copyright (c) [2025] Nadege LEMPERIERE
   All rights reserved
   -------------------------------------------------------
   Motor tuning tool
   ------------------------------------------------------- */

package org.firstinspires.ftc.teamcode.tuning;

/* System includes */
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/* Android includes */
import android.os.Environment;

/* Qualcomm includes */
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

/* ACME robotics includes */
import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.config.ValueProvider;

/* Tools includes */
import org.firstinspires.ftc.teamcode.core.tools.Logger;

/* Configuration includes */
import org.firstinspires.ftc.teamcode.core.configuration.ConfMotor;
import org.firstinspires.ftc.teamcode.core.configuration.Configuration;

/* Components includes */
import org.firstinspires.ftc.teamcode.core.components.MotorComponent;
import org.firstinspires.ftc.teamcode.core.components.MotorSingle;
import org.firstinspires.ftc.teamcode.core.components.SmartGamepad;


@Config
@TeleOp(name = "MotorTuning", group = "Tuning")
public class MotorTuning extends LinearOpMode {

    public enum Mode {
        FIRST,
        SECOND,
        BOTH
    }

    public enum Direction {
        REVERSE,
        FORWARD
    }

    /* -------- Configuration variables -------- */
    public static long                  SLEEP_MS        = 200;
    
    /* ---------------- Members ---------------- */
    private Logger                      mLogger;

    private Configuration               mConfiguration;
    private Configuration               mUpdatedConfiguration;

    private ModeProvider                mMode;

    private SmartGamepad                mGamepad;


    /* ------- Preload for all motor data  ------ */

    // Each motors are manipulated as single motors, to enable motors couple to be moved
    // independantly while tuning their positions.

    // The motor selection config variables that can be updated by the dashboard
    private Map<String, Boolean>                mMotorSelection;
    // Link between motor name and the associated harwareMap names (2 for coupled motors)
    private Map<String, List<String>>           mMotorsHw;
    private String                              mCurrentMotor;

    // Link between hardwareMap name and the corresponding single motors
    private Map<String, MotorComponent>         mMotors;
    // Link between motor hardwareMap name and their configuration (comes from the new configuration
    // So may be modified to issue an updated configuration file
    private Map<String, ConfMotor.Controller>   mMotorConfiguration;


    // Current motors configuration for easy access
    private List<ConfMotor.Controller>          mCurrentConf;
    // Current motors hw component for easy access
    private List<MotorComponent>                mCurrentMotorHw;



    @Override
    public void runOpMode() {

        try {

            mLogger = new Logger(null,FtcDashboard.getInstance());

            mGamepad = new SmartGamepad(gamepad1, mLogger);

            mMode = new ModeProvider();
            mMode.set(Mode.FIRST);

            mConfiguration = new Configuration(mLogger);
            mConfiguration.read();
            mConfiguration.log();
            mUpdatedConfiguration = new Configuration(mConfiguration);

            mMotorSelection = new LinkedHashMap<>();
            mMotorConfiguration = new LinkedHashMap<>();
            mMotors = new LinkedHashMap<>();
            mMotorsHw = new LinkedHashMap<>();

            mCurrentConf = new ArrayList<>();
            mCurrentMotorHw = new ArrayList<>();

            Map<String, ConfMotor> motorsConfiguration = mUpdatedConfiguration.motors();

            // Parse the motors and extract all the information the tool will need once and for all
            for (Map.Entry<String, ConfMotor> motor : motorsConfiguration.entrySet()) {
                mMotorSelection.put(motor.getKey(),false);

                List<String> mapping = new ArrayList<>();
                List<ConfMotor.Controller> controllers = motor.getValue().controllers();
                for(int i_ctrl = 0; i_ctrl < controllers.size(); i_ctrl ++) {

                    MotorComponent local_motor = new MotorSingle(controllers.get(i_ctrl),hardwareMap,mLogger);
                    if(local_motor.isReady()) {
                        mMotors.put(local_motor.getName(),local_motor);
                        mapping.add(local_motor.getName());
                        mMotorConfiguration.put(local_motor.getName(),controllers.get(i_ctrl));
                    }
                }
                mMotorsHw.put(motor.getKey(),mapping);

            }

            // Add the motor selection variables on the dashboard
            for (Map.Entry<String, Boolean> selected : mMotorSelection.entrySet()) {
                SelectedProvider provider = new SelectedProvider(mMotorSelection, selected.getKey());
                FtcDashboard.getInstance().addConfigVariable(MotorTuning.class.getSimpleName(),selected.getKey(),provider);
            }

            FtcDashboard.getInstance().updateConfig();
            mLogger.update();

            waitForStart();

            mLogger.clear();

            while(opModeIsActive()) {

                /* Find current selected motor */
                String currentMotor = this.findSelectedMotor();
                mLogger.addData("Current Motor",currentMotor);

                /* Manage configuration change */
                if(!Objects.equals(currentMotor, mCurrentMotor))  {

                    // Stop current motors
                    this.powerMotors(0);

                    // Now we can forget the previously selected motors sice we no longer need them
                    mCurrentMotor = currentMotor;
                    // Select hardwaremotors and conf for current motor
                    this.updateCurrentMotors();
                    
                    // Initialize motor processing
                    mMode.set(Mode.FIRST);

                    // Adapt configuration
                    if(!mCurrentConf.isEmpty()) {
                        ReverseProvider reverse = new ReverseProvider(mCurrentConf.get(0));
                        FtcDashboard.getInstance().addConfigVariable(this.getClass().getSimpleName(),"REVERSE_1",reverse);

                        DirectionProvider direction = new DirectionProvider(mCurrentConf.get(0));
                        FtcDashboard.getInstance().addConfigVariable(this.getClass().getSimpleName(),"DIRECTION_1",direction);
                    }
                    else {
                        FtcDashboard.getInstance().removeConfigVariable(this.getClass().getSimpleName(),"REVERSE_1");
                        FtcDashboard.getInstance().removeConfigVariable(this.getClass().getSimpleName(),"DIRECTION_1");
                    }
                    if(mCurrentConf.size() >= 2) {

                        ReverseProvider reverse = new ReverseProvider(mCurrentConf.get(1));
                        FtcDashboard.getInstance().addConfigVariable(this.getClass().getSimpleName(),"REVERSE_2",reverse);

                        DirectionProvider direction = new DirectionProvider(mCurrentConf.get(1));
                        FtcDashboard.getInstance().addConfigVariable(this.getClass().getSimpleName(),"DIRECTION_2",direction);

                        FtcDashboard.getInstance().addConfigVariable(this.getClass().getSimpleName(),"MODE",mMode);
                    }
                    else {
                        FtcDashboard.getInstance().removeConfigVariable(this.getClass().getSimpleName(),"REVERSE_2");
                        FtcDashboard.getInstance().removeConfigVariable(this.getClass().getSimpleName(),"DIRECTION_2");
                        FtcDashboard.getInstance().removeConfigVariable(this.getClass().getSimpleName(),"MODE");
                    }

                    // Start motors

                    FtcDashboard.getInstance().updateConfig();

                }

                if( this.wasConfChanged())
                {
                    mLogger.addData("Conf","changed");
                    this.powerMotors(0);
                    this.reloadFromConf();

                    // After such a change, we don't want to go back to a coupled movement
                    // before the user makes sure it's ok
                    if(mMode.get() == Mode.BOTH) {mMode.set(Mode.FIRST);}

                    FtcDashboard.getInstance().updateConfig();
                }

                // Manage controls
                this.powerMotors(mGamepad.axes.left_stick_y.value());

                // Log motors state and updated configuration
                mLogger.addData("Mode",""+mMode.get());
                this.logMotorsState(mLogger);
                mUpdatedConfiguration.log();

                mLogger.update();

                // Give time for motor position change to occur
                sleep(SLEEP_MS);


            }

            mUpdatedConfiguration.write(Environment.getExternalStorageDirectory().getPath()
                    + "/FIRST/motor-tuning.json");
            mLogger.addLine("Updated configuration saved. You may retrieve it using <b>adb pull /sdcard/FIRST/motor-tuning.json</b>");
            mLogger.update();
        }
        catch(Exception e) {
            mLogger.addLine(e.toString());
            mLogger.update();
        }
    }

    private String findSelectedMotor()
    {
        String result = "";
        for (Map.Entry<String, Boolean> selected : mMotorSelection.entrySet()) {
            if(selected.getValue()) { result = selected.getKey(); }
        }
        return result;
    }

    private void   updateCurrentMotors() {

        mCurrentMotorHw.clear();
        mCurrentConf.clear();

        if (mMotorsHw.containsKey(mCurrentMotor)) {
            // Retrieve the list of hardware motor associated to current selected motor
            // (may be coupled, so there may be up to 2 of them)
            List<String> hwMotors = mMotorsHw.get(mCurrentMotor);
            if(hwMotors != null) {
                for (int i_motor = 0; i_motor < hwMotors.size(); i_motor++) {
                    String motorHwName = hwMotors.get(i_motor);

                    // Get current motor and its associated configuration
                    // which might have been changed through the dashboard
                    MotorComponent motor = null;
                    ConfMotor.Controller conf = null;

                    // Find the single motor associated to the hardware name
                    if (mMotors.containsKey(motorHwName)) {
                        motor = mMotors.get(motorHwName);
                    }
                    if (mMotorConfiguration.containsKey(motorHwName)) {
                        conf = mMotorConfiguration.get(motorHwName);
                    }

                    if (conf != null && motor != null) {
                        mCurrentConf.add(conf);
                        mCurrentMotorHw.add(motor);
                    }
                }
            }
        }
    }


    private void powerMotors(double Value) {
        mLogger.addData("Power", ""+Value);
        for (int i_motor = 0; i_motor < mCurrentMotorHw.size(); i_motor++) {
            MotorComponent hwMotor = mCurrentMotorHw.get(i_motor);
            if (hwMotor != null) {

                if (i_motor == 0) {
                    if (mMode.get() == Mode.FIRST || mMode.get() == Mode.BOTH) {
                        hwMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
                        hwMotor.setPower(Value);
                    }
                }
                if (i_motor == 1) {
                    if (mMode.get() == Mode.SECOND || mMode.get() == Mode.BOTH) {
                        hwMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
                        hwMotor.setPower(Value);
                    }
                }


            }
        }
    }

    private boolean     wasConfChanged()
    {
        boolean result = false;

        for (int i_motor = 0; i_motor < mCurrentMotorHw.size(); i_motor++) {
            MotorComponent hwMotor = mCurrentMotorHw.get(i_motor);
            ConfMotor.Controller conf = mCurrentConf.get(i_motor);

            if (hwMotor != null && conf != null) {
                if ((hwMotor.getDirection() == DcMotor.Direction.REVERSE) &&
                        conf.direction().equals("forward")) {
                    result = true;
                }
                if ((hwMotor.getDirection() == DcMotor.Direction.FORWARD) &&
                        conf.direction().equals("reverse")) {
                    result = true;
                }
                if (hwMotor.getEncoderCorrection() &&
                        !conf.shallReverseEncoder()) {
                    result = true;
                }
                if (!hwMotor.getEncoderCorrection() &&
                        conf.shallReverseEncoder()) {
                    result = true;
                }
            }
        }

        return result;

    }

    private void        reloadFromConf()
    {
        List<Integer> to_remove = new ArrayList<>();

        for (int i_motor = 0; i_motor < mCurrentConf.size(); i_motor++) {

            ConfMotor.Controller conf = mCurrentConf.get(i_motor);

            if (conf != null) {
                MotorComponent hwMotor = new MotorSingle(conf,hardwareMap,mLogger);
                if(hwMotor.isReady()) {
                    mCurrentMotorHw.set(i_motor,hwMotor);
                    mMotors.put(hwMotor.getName(),hwMotor);
                }
                else {
                    to_remove.add(i_motor);
                    mMotors.remove(hwMotor.getName());
                    mMotorConfiguration.remove(hwMotor.getName());
                    Objects.requireNonNull(mMotorsHw.get(mCurrentMotor)).remove(hwMotor.getName());
                }
            }
        }

        for(int i_motor = 0; i_motor < to_remove.size(); i_motor ++) {
            mCurrentMotorHw.remove((int)to_remove.get(i_motor));
            mCurrentConf.remove((int)to_remove.get(i_motor));
        }

    }

    private void logMotorsState(Logger logger) {
        logger.addLine("CURRENT MOTORS");

        for (int i_motor = 0; i_motor < mCurrentMotorHw.size(); i_motor++) {
            MotorComponent hwMotor = mCurrentMotorHw.get(i_motor);
            ConfMotor.Controller conf = mCurrentConf.get(i_motor);
            if (hwMotor != null && conf != null) {

                // Log motor state
                logger.addLine("--> Motor " + i_motor);
                logger.addLine("-----> HwMap : " + hwMotor.getName());
                logger.addLine("-----> ShallReverse : " + conf.shallReverseEncoder());
                logger.addLine("-----> ConfDirection : " + conf.direction());
                logger.addLine("-----> Direction : " + hwMotor.getDirection());
                logger.addLine("-----> Position : " + hwMotor.getCurrentPosition());
                logger.addLine("-----> Power : " + hwMotor.getPower());
                logger.addLine("-----> Mode : " + hwMotor.getMode());
            }
        }
    }




    // SelectedProvider updates the motors selection states
    // Since Map<String, Boolean> is not a simple type, it's managed as
    // pointer, when we change it in the provider, it's changed in the
    // global class.
    // When we select a new motor, we make sure to deselect all the others
    static class SelectedProvider implements ValueProvider<Boolean> {
        Map<String, Boolean> mAllSelection;
        String mCurrentSelection;

        public SelectedProvider(Map<String, Boolean> selection, String current) {
            mAllSelection = selection;
            mCurrentSelection = current;
        }

        @Override
        public Boolean get() {
            return mAllSelection.get(mCurrentSelection);
        }

        @Override
        public void set(Boolean Value) {

            if (Value) {
                for (Map.Entry<String, Boolean> selected : mAllSelection.entrySet()) {
                    selected.setValue(false);
                }
            }
            mAllSelection.put(mCurrentSelection, Value);
        }
    }


    // ReverseProvider updates the controller reverse configuration
    // Since ConfMotor.Controller is not a simple type, it's managed as
    // pointer, when we change it in the provider, it's changed in the
    // global configuration
    static class ReverseProvider implements ValueProvider<Boolean> {
        ConfMotor.Controller mController;
        public ReverseProvider( ConfMotor.Controller controller) {
            mController = controller;
        }
        @Override
        public Boolean get()           { return mController.shallReverseEncoder(); }
        @Override
        public void set(Boolean Value) { mController.shallReverseEncoder(Value);   }
    }

    // DirectionProvider updates the controller reverse configuration
    // Since ConfMotor.Controller is not a simple type, it's managed as
    // pointer, when we change it in the provider, it's changed in the
    // global configuration
    static class DirectionProvider implements ValueProvider<Direction> {
        ConfMotor.Controller mController;
        public DirectionProvider( ConfMotor.Controller controller) {
            mController = controller;
        }
        @Override
        public Direction get()           {
            Direction result = Direction.FORWARD;

            String direction = mController.direction();
            if(direction.equals("reverse")) { result = Direction.REVERSE; }

            return result;
        }
        @Override
        public void set(Direction Value) {
            if (Value == Direction.REVERSE) { mController.direction("reverse"); }
            if (Value == Direction.FORWARD) { mController.direction("forward"); }
        }
    }


    // Since mode is a simple type, even with the appropriate constructor,
    // mMode will only be updated locally by the dashboard. 
    // We'll have to make sure the code access the mode from the provider using the get
    // Method, since it's the only place the pdated information can be found
    static class ModeProvider implements ValueProvider<Mode> {
        Mode mMode;
        @Override
        public Mode get()              { return mMode;  }
        @Override
        public void set(Mode Value)    { mMode = Value; }
    }



}