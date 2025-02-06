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

package org.firstinspires.ftc.teamcode.core.components.gamepads;

/* System includes */
import java.lang.reflect.Field;

/* Qualcomm includes */
import com.qualcomm.robotcore.hardware.Gamepad;

/* Tools includes */
import org.firstinspires.ftc.teamcode.core.tools.Logger;

public class SmartAxis {

                    Logger  mLogger;

                    Gamepad mGamepad;
                    String  mName;

                    double  mMultiplier;

    public SmartAxis(Gamepad gamepad, String name, Logger logger) {
        mLogger     = logger;
        mGamepad    = gamepad;
        mName       = name;
        mMultiplier = 1.0;
    }
    public SmartAxis(Gamepad gamepad, String name, Logger logger, double multiplier) {
        mLogger     = logger;
        mGamepad    = gamepad;
        mName       = name;
        mMultiplier = multiplier;
    }

    public double value() throws NoSuchFieldException, IllegalAccessException {

        double result = 0;

        if(mGamepad != null) {

            Field field = Gamepad.class.getDeclaredField(mName);
            Object status = field.get(mGamepad);
            if(status != null) {
                if (field.getType() == double.class) {
                    result = ((double) status * mMultiplier);
                } else if (field.getType() == float.class) {
                    result = ((float) status * mMultiplier);
                }
            }

        }
        
        return result;
    }

}
