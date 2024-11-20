/* -------------------------------------------------------
   Copyright (c) [2024] FASNY
   All rights reserved
   -------------------------------------------------------
   Angles manipulation functions
   ------------------------------------------------------- */

package org.firstinspires.ftc.teamcode.tools;

public class Angles {

    public enum Direction {
        UP,
        DOWN,
        RIGHT,
        LEFT,
        FRONT,
        BACK,
        UNKNOWN
    };

    public static Direction determineFacingDirection(double roll, double pitch, double yaw) {

        Direction result = Direction.UNKNOWN;

        // Normalize angles to range [-180, 180]
        roll    = normalize(roll);
        pitch   = normalize(pitch);
        yaw     = normalize(yaw);

        // Define a small tolerance for angle comparisons
        double tolerance = 15.0;

        // Check pitch for up/down
        if (Math.abs(pitch - 90) <= tolerance) {
            result = Direction.UP;
        } else if (Math.abs(pitch + 90) <= tolerance) {
            result =  Direction.DOWN;
        }

        // Check yaw for front/back
        if (Math.abs(yaw) <= tolerance && Math.abs(pitch) <= tolerance) {
            result =  Direction.FRONT;
        } else if (Math.abs(yaw - 180) <= tolerance || Math.abs(yaw + 180) <= tolerance) {
            result = Direction.BACK;
        }

        // Check roll for right/left
        if (Math.abs(yaw - 90) <= tolerance && Math.abs(pitch) <= tolerance) {
            result = Direction.LEFT;
        } else if (Math.abs(yaw + 90) <= tolerance && Math.abs(pitch) <= tolerance) {
            result = Direction.RIGHT;
        }

        return result;
    }

    private static double normalize(double angle) {

        double result = angle;

        while (result > 180) {
            result -= 360;
        }
        while (result < -180) {
            result += 360;
        }
        return result;
    }

}
