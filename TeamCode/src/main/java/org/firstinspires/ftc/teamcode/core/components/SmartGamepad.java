/* -------------------------------------------------------
   Copyright (c) [2025] Nadege LEMPERIERE
   All rights reserved
   -------------------------------------------------------
   Gamepad extended management
   ------------------------------------------------------- */

package org.firstinspires.ftc.teamcode.core.components;

/* Qualcomm includes */
import com.qualcomm.robotcore.hardware.Gamepad;

/* Tools includes */
import org.firstinspires.ftc.teamcode.core.tools.Logger;

public class SmartGamepad {

    public  static  class     Buttons {
        public SmartButton a;
        public SmartButton b;
        public SmartButton x;
        public SmartButton y;

        public SmartButton dpad_up;
        public SmartButton dpad_down;
        public SmartButton dpad_left;
        public SmartButton dpad_right;

        public SmartButton left_bumper;
        public SmartButton left_trigger;

        public SmartButton left_stick_x_right;
        public SmartButton left_stick_x_left;
        public SmartButton left_stick_y_up;
        public SmartButton left_stick_y_down;
        public SmartButton left_stick_button;

        public SmartButton right_bumper;
        public SmartButton right_trigger;

        public SmartButton right_stick_x_right;
        public SmartButton right_stick_x_left;
        public SmartButton right_stick_y_up;
        public SmartButton right_stick_y_down;
        public SmartButton right_stick_button;
    }

    public static class Axes {

        public SmartAxis left_stick_x;
        public SmartAxis left_stick_y;
        public SmartAxis left_trigger;
        public SmartAxis right_stick_x;
        public SmartAxis right_stick_y;
        public SmartAxis right_trigger;

    }

    public  Buttons buttons;
    public  Axes    axes;

            Logger  mLogger;

    public              SmartGamepad(Gamepad gamepad, Logger logger) {

        mLogger = logger;

        buttons = new Buttons();
        buttons.a = new SmartButton(gamepad, "a", logger);
        buttons.b = new SmartButton(gamepad, "b", logger);
        buttons.x = new SmartButton(gamepad, "x", logger);
        buttons.y = new SmartButton(gamepad, "y", logger);

        buttons.dpad_up = new SmartButton(gamepad, "dpad_up", logger);
        buttons.dpad_down = new SmartButton(gamepad, "dpad_down", logger);
        buttons.dpad_left = new SmartButton(gamepad, "dpad_left", logger);
        buttons.dpad_right = new SmartButton(gamepad, "dpad_right", logger);

        buttons.left_bumper        = new SmartButton(gamepad, "left_bumper", logger);
        buttons.left_trigger       = new SmartButton(gamepad, "left_trigger", logger);
        buttons.left_stick_x_left  = new SmartButton(gamepad, "left_stick_x", logger, -1.0);
        buttons.left_stick_x_right = new SmartButton(gamepad, "left_stick_x", logger, 1.0);
        buttons.left_stick_y_up    = new SmartButton(gamepad, "left_stick_y", logger, -1.0);
        buttons.left_stick_y_down  = new SmartButton(gamepad, "left_stick_y", logger, 1.0);
        buttons.left_stick_button  = new SmartButton(gamepad, "left_stick_button", logger);

        buttons.right_bumper        = new SmartButton(gamepad, "right_bumper", logger);
        buttons.right_trigger       = new SmartButton(gamepad, "right_trigger", logger);
        buttons.right_stick_x_left  = new SmartButton(gamepad, "right_stick_x", logger, -1.0);
        buttons.right_stick_x_right = new SmartButton(gamepad, "right_stick_x", logger, 1.0);
        buttons.right_stick_y_up    = new SmartButton(gamepad, "right_stick_y", logger, -1.0);
        buttons.right_stick_y_down  = new SmartButton(gamepad, "right_stick_y", logger, 1.0);
        buttons.right_stick_button  = new SmartButton(gamepad, "right_stick_button", logger);

        axes = new Axes();
        axes.left_stick_x = new SmartAxis(gamepad, "left_stick_x", logger);
        axes.left_stick_y = new SmartAxis(gamepad, "left_stick_y", logger, -1.0);
        axes.left_trigger = new SmartAxis(gamepad, "left_trigger", logger);
        axes.right_stick_x = new SmartAxis(gamepad, "right_stick_x", logger);
        axes.right_stick_y = new SmartAxis(gamepad, "right_stick_y", logger, -1.0);
        axes.right_trigger = new SmartAxis(gamepad, "right_trigger", logger);


    }

}

