package org.firstinspires.ftc.teamcode.tuning;

/* System includes */
import android.os.Environment;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.Objects;


/* Qualcomm includes */
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Servo;

/* ACME robotics includes */
import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.config.ValueProvider;

/* Tools includes */
import org.firstinspires.ftc.teamcode.core.tools.Logger;

/* Configuration includes */
import org.firstinspires.ftc.teamcode.core.configuration.Configuration;
import org.firstinspires.ftc.teamcode.core.configuration.ConfServo;

/* Components includes */
import org.firstinspires.ftc.teamcode.core.components.ServoComponent;
import org.firstinspires.ftc.teamcode.core.components.ServoSingle;
import org.firstinspires.ftc.teamcode.core.components.SmartGamepad;

@Config
@TeleOp(name = "ServoTuning", group = "Tuning")
public class ServoTuning extends LinearOpMode {

    public enum Mode {
        FIRST,
        SECOND,
        BOTH
    }

    /* -------- Configuration variables -------- */
    public static double                INCREMENT_STEP  = 0.01;
    public static long                  SLEEP_MS        = 200;
    public static double                TARGET_POS      = 0.0;
    public static boolean               HOLD_POSITION   = false;

    /* ---------------- Members ---------------- */
    private Logger                      mLogger;

    private Configuration               mConfiguration;
    private Configuration               mUpdatedConfiguration;

    private ModeProvider                mMode;

    private SmartGamepad                mGamepad;


    /* ------- Preload for all servo data  ------ */

    // Each servos are manipulated as single servos, to enable servos couple to be moved
    // independantly while tuning their positions.

    // The servo selection config variables that can be updated by the dashboard
    private Map<String, Boolean>                mServoSelection;
    // Link between servo name and the associated harwareMap names (2 for coupled servos)
    private Map<String, List<String>>           mServosHw;
    private String                              mCurrentServo;

    // Link between hardwareMap name and the corresponding single servos
    private Map<String, ServoComponent>         mServos;
    // Link between servo hardwareMap name and their configuration (comes from the new configuration
    // So may be modified to issue an updated configuration file
    private Map<String, ConfServo.Controller>   mServoConfiguration;


    // Current servos configuration for easy access
    private List<ConfServo.Controller>          mCurrentConf;
    // Current servos hw component for easy access
    private List<ServoComponent>                mCurrentServoHw;



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

            mServoSelection = new LinkedHashMap<>();
            mServoConfiguration = new LinkedHashMap<>();
            mServos = new LinkedHashMap<>();
            mServosHw = new LinkedHashMap<>();

            mCurrentConf = new ArrayList<>();
            mCurrentServoHw = new ArrayList<>();

            Map<String, ConfServo> servosConfiguration = mUpdatedConfiguration.servos();

            // Parse the servos and extract all the information the tool will need once and for all
            for (Map.Entry<String, ConfServo> servo : servosConfiguration.entrySet()) {
                mServoSelection.put(servo.getKey(),false);

                List<String> mapping = new ArrayList<>();
                List<ConfServo.Controller> controllers = servo.getValue().controllers();
                for(int i_ctrl = 0; i_ctrl < controllers.size(); i_ctrl ++) {

                    ServoComponent local_servo = new ServoSingle(controllers.get(i_ctrl),hardwareMap,mLogger);
                    if(local_servo.isReady()) {
                        mServos.put(local_servo.getName(),local_servo);
                        mapping.add(local_servo.getName());
                        mServoConfiguration.put(local_servo.getName(),controllers.get(i_ctrl));
                    }
                }
                mServosHw.put(servo.getKey(),mapping);

            }

            // Add the servo selection variables on the dashboard
            for (Map.Entry<String, Boolean> selected : mServoSelection.entrySet()) {
                SelectedProvider provider = new SelectedProvider(mServoSelection, selected.getKey());
                FtcDashboard.getInstance().addConfigVariable(ServoTuning.class.getSimpleName(),selected.getKey(),provider);
            }

            FtcDashboard.getInstance().updateConfig();
            mLogger.update();

            waitForStart();

            mLogger.clear();

            while(opModeIsActive()) {

                /* Find current selected servo */
                String currentServo = this.findSelectedServo();
                mLogger.addData("Current Servo",currentServo);

                /* Manage configuration change */
                if(!Objects.equals(currentServo, mCurrentServo))  {

                    // Stop servos if they don't need to hold
                    if (!HOLD_POSITION) { this.stopServos(); }

                    // Now we can forget the previously selected servos sice we no longer need them
                    mCurrentServo = currentServo;
                    // Select hardwareservos and conf for current servo
                    this.updateCurrentServos();
                    
                    // Initialize servo processing
                    mMode.set(Mode.FIRST);

                    // Adapt configuration
                    if(!mCurrentConf.isEmpty()) {
                        ReverseProvider reverse = new ReverseProvider(mCurrentConf.get(0));
                        FtcDashboard.getInstance().addConfigVariable(this.getClass().getSimpleName(),"REVERSE_CONF_1",reverse);
                    }
                    else {
                        FtcDashboard.getInstance().removeConfigVariable(this.getClass().getSimpleName(),"REVERSE_CONF_1");
                    }
                    if(mCurrentConf.size() >= 2) {
                        ReverseProvider reverse = new ReverseProvider(mCurrentConf.get(1));
                        FtcDashboard.getInstance().addConfigVariable(this.getClass().getSimpleName(),"REVERSE_CONF_2",reverse);

                        FtcDashboard.getInstance().addConfigVariable(this.getClass().getSimpleName(),"MODE",mMode);
                    }
                    else {
                        FtcDashboard.getInstance().removeConfigVariable(this.getClass().getSimpleName(),"REVERSE_CONF_2");
                        FtcDashboard.getInstance().removeConfigVariable(this.getClass().getSimpleName(),"MODE");
                    }

                    // Start servos
                    this.startServos();
                    TARGET_POS = this.getPosition();

                    FtcDashboard.getInstance().updateConfig();

                }

                if( this.wasReverseChanged())
                {
                    this.stopServos();
                    this.reloadFromConf();

                    // After such a change, we don't want to go back to a coupled movement
                    // before the user makes sure it's ok
                    if(mMode.get() == Mode.BOTH) {mMode.set(Mode.FIRST);}

                    this.startServos();
                    TARGET_POS = this.getPosition();
                    FtcDashboard.getInstance().updateConfig();
                }




                // Manage controls
                if (mGamepad.buttons.left_bumper.pressedOnce()) {
                    TARGET_POS = Math.max(0.00, TARGET_POS - INCREMENT_STEP); // Decrease position but don't go below 0
                    FtcDashboard.getInstance().updateConfig();
                } else if (mGamepad.buttons.right_bumper.pressedOnce()) {
                    TARGET_POS = Math.min(1.00, TARGET_POS + INCREMENT_STEP); // Increase position but don't exceed 1
                    FtcDashboard.getInstance().updateConfig();
                }

                this.setPosition(TARGET_POS);

                // Log servos state and updated configuration
                mLogger.addData("Mode",""+mMode.get());
                this.logServosState(mLogger);
                mUpdatedConfiguration.log();

                mLogger.update();

                // Give time for servo position change to occur
                sleep(SLEEP_MS);


            }

            mUpdatedConfiguration.write(Environment.getExternalStorageDirectory().getPath()
                    + "/FIRST/servo-tuning.json");
            mLogger.addLine("Updated configuration saved. You may retrieve it using <b>adb pull /sdcard/FIRST/servo-tuning.json</b>");
            mLogger.update();
        }
        catch(Exception e) {
            mLogger.addLine(e.toString());
            mLogger.update();
        }
    }

    private String findSelectedServo()
    {
        String result = "";
        for (Map.Entry<String, Boolean> selected : mServoSelection.entrySet()) {
            if(selected.getValue()) { result = selected.getKey(); }
        }
        return result;
    }

    private void   updateCurrentServos() {

        mCurrentServoHw.clear();
        mCurrentConf.clear();

        if (mServosHw.containsKey(mCurrentServo)) {
            // Retrieve the list of hardware servo associated to current selected servo
            // (may be coupled, so there may be up to 2 of them)
            List<String> hwServos = mServosHw.get(mCurrentServo);
            for (int i_servo = 0; i_servo < hwServos.size(); i_servo++) {
                String servoHwName = hwServos.get(i_servo);

                // Get current servo and its associated configuration
                // which might have been changed through the dashboard
                ServoComponent servo = null;
                ConfServo.Controller conf = null;

                // Find the single servo associated to the hardware name
                if (mServos.containsKey(servoHwName)) {
                    servo = mServos.get(servoHwName);
                }
                if (mServoConfiguration.containsKey(servoHwName)) {
                    conf = mServoConfiguration.get(servoHwName);
                }

                if (conf != null && servo != null) {
                    mCurrentConf.add(conf);
                    mCurrentServoHw.add(servo);
                }
            }
        }
    }

    private void stopServos() {
        for (int i_servo = 0; i_servo < mCurrentServoHw.size(); i_servo++) {
            ServoComponent hwServo = mCurrentServoHw.get(i_servo);
            if (hwServo != null) {
                hwServo.getController().pwmDisable();
            }
        }
    }

    private void startServos()
    {
        for (int i_servo = 0; i_servo < mCurrentServoHw.size(); i_servo++) {
            ServoComponent hwServo = mCurrentServoHw.get(i_servo);
            if (hwServo != null) {
                hwServo.getController().pwmEnable();
            }
        }
    }


    private void setPosition(double position) {

        for (int i_servo = 0; i_servo < mCurrentServoHw.size(); i_servo++) {
            ServoComponent hwServo = mCurrentServoHw.get(i_servo);
            if (hwServo != null) {
                // Depending on the coupled servo management mode, pilot the required servos
                if(i_servo == 0) {
                    if (mMode.get() == Mode.FIRST || mMode.get() == Mode.BOTH){
                        hwServo.setPosition(position);
                    }
                    else{
                        hwServo.getController().pwmDisable();
                    }
                }
                if(i_servo == 1) {
                    if (mMode.get() == Mode.SECOND || mMode.get() == Mode.BOTH){
                        hwServo.setPosition(position);
                    }
                    else{
                        hwServo.getController().pwmDisable();
                    }
                }
            }
        }
    }

    private double getPosition()
    {
        double result = -1;

        for (int i_servo = 0; i_servo < mCurrentServoHw.size(); i_servo++) {
            ServoComponent hwServo = mCurrentServoHw.get(i_servo);
            if (hwServo != null) {

                // Depending on the coupled servo management mode, pilot the required servos
                if (i_servo == 0) {
                    if (mMode.get() == Mode.FIRST || mMode.get() == Mode.BOTH) {
                        result = hwServo.getPosition();
                    }
                }
                if (i_servo == 1) {
                    if (mMode.get() == Mode.SECOND || mMode.get() == Mode.BOTH) {
                        result = hwServo.getPosition();
                    }
                }
            }
        }

        return result;
    }


    private boolean     wasReverseChanged()
    {
        boolean result = false;

        for (int i_servo = 0; i_servo < mCurrentServoHw.size(); i_servo++) {
            ServoComponent hwServo = mCurrentServoHw.get(i_servo);
            ConfServo.Controller conf = mCurrentConf.get(i_servo);

            if (hwServo != null && conf != null) {
                if ((hwServo.getDirection() == Servo.Direction.REVERSE) &&
                        !conf.shallReverse()) {
                    result = true;
                }
                if ((hwServo.getDirection() == Servo.Direction.FORWARD) &&
                        conf.shallReverse()) {
                    result = true;
                }
            }
        }

        return result;

    }

    private void        reloadFromConf()
    {
        boolean result = false;

        List<Integer> to_remove = new ArrayList<>();

        for (int i_servo = 0; i_servo < mCurrentConf.size(); i_servo++) {

            ConfServo.Controller conf = mCurrentConf.get(i_servo);

            if (conf != null) {
                ServoComponent hwServo = new ServoSingle(conf,hardwareMap,mLogger);
                if(hwServo.isReady()) {
                    mCurrentServoHw.set(i_servo,hwServo);
                    mServos.put(hwServo.getName(),hwServo);
                }
                else {
                    to_remove.add(i_servo);
                    mServos.remove(hwServo.getName());
                    mServoConfiguration.remove(hwServo.getName());
                    mServosHw.get(mCurrentServo).remove(hwServo.getName());
                }
            }
        }

        for(int i_servo = 0; i_servo < to_remove.size(); i_servo ++) {
            mCurrentServoHw.remove((int)to_remove.get(i_servo));
            mCurrentConf.remove((int)to_remove.get(i_servo));
        }

    }

    private void logServosState(Logger logger) {
        logger.addLine("CURRENT SERVOS");

        for (int i_servo = 0; i_servo < mCurrentServoHw.size(); i_servo++) {
            ServoComponent hwServo = mCurrentServoHw.get(i_servo);
            ConfServo.Controller conf = mCurrentConf.get(i_servo);
            if (hwServo != null && conf != null) {

                // Log servo state
                logger.addLine("--> Servo " + i_servo);
                logger.addLine("-----> HwMap : " + hwServo.getName());
                logger.addLine("-----> ShallReverse : " + conf.shallReverse());
                logger.addLine("-----> Direction : " + hwServo.getDirection());
                logger.addLine("-----> Position : " + hwServo.getPosition());
                logger.addLine("-----> Power : " + hwServo.getController().getPwmStatus());
            }
        }
    }




    // SelectedProvider updates the servos selection states
    // Since Map<String, Boolean> is not a simple type, it's managed as
    // pointer, when we change it in the provider, it's changed in the
    // global class.
    // When we select a new servo, we make sure to deselect all the others
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
    // Since ConfServo.Controller is not a simple type, it's managed as
    // pointer, when we change it in the provider, it's changed in the
    // global configuration
    static class ReverseProvider implements ValueProvider<Boolean> {
        ConfServo.Controller mController;
        public ReverseProvider( ConfServo.Controller controller) {
            mController = controller;
        }
        @Override
        public Boolean get()           { return mController.shallReverse(); }
        @Override
        public void set(Boolean Value) { mController.shallReverse(Value);   }
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