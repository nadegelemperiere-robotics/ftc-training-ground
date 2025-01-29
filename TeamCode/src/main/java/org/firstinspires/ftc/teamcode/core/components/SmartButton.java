/* -------------------------------------------------------
   Copyright (c) [2025] Nadege LEMPERIERE
   All rights reserved
   -------------------------------------------------------
   SmartButton class that manages gamepad button
   --> Adds the capability to detect when button is pressed
   and was released before, to trigger events only once on button
   pressed
   --> Adds the capability to use triggers has buttons with a
   threshold
   ------------------------------------------------------- */

package org.firstinspires.ftc.teamcode.core.components;

/* System includes */
import java.lang.reflect.Field;

/* Qualcomm includes */
import com.qualcomm.robotcore.hardware.Gamepad;

/* Tools includes */
import org.firstinspires.ftc.teamcode.core.tools.Logger;

public class SmartButton {

    static  final   double  sTriggerThreshold = 0;

                    Logger  mLogger;

                    Gamepad mGamepad;
                    String  mName;

                    boolean mWasPressed;
                    double  mMultiplier;

    public SmartButton(Gamepad gamepad, String name, Logger logger) {
        mLogger     = logger;
        mGamepad    = gamepad;
        mName       = name;
        mWasPressed = false;
        mMultiplier = 1.0;
    }
    public SmartButton(Gamepad gamepad, String name, Logger logger, double multiplier) {
        mLogger     = logger;
        mGamepad    = gamepad;
        mName       = name;
        mWasPressed = false;
        mMultiplier  = multiplier;
    }

    public boolean pressed() throws NoSuchFieldException, IllegalAccessException {
        boolean result = false;

        if(mGamepad != null) {

            Field field = Gamepad.class.getDeclaredField(mName);
            Object status = field.get(mGamepad);
            if(status != null) {
                if (field.getType() == boolean.class) {
                    result = (boolean) status;
                } else if (field.getType() == double.class) {
                    result = ((double) status * mMultiplier > sTriggerThreshold);
                } else if (field.getType() == float.class) {
                    result = ((float) status * mMultiplier > sTriggerThreshold);
                }
            }

        }
        
        return result;
    }

    public boolean pressedOnce() throws NoSuchFieldException, IllegalAccessException, NullPointerException {

        boolean is_pressed = this.pressed();
        boolean result = is_pressed && !mWasPressed;
        mWasPressed = is_pressed;
        
        return result;
    }
}
