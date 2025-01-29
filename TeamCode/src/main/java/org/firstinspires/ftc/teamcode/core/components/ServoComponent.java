/* -------------------------------------------------------
   Copyright (c) [2025] Nadege LEMPERIERE
   All rights reserved
   -------------------------------------------------------
   ServoComponent is an interface for servo management
   It supersedes Servo and provides additional capabilities
   such as :
   - Synchronizing 2 coupled servos
   ------------------------------------------------------- */

package org.firstinspires.ftc.teamcode.core.components;

/* Qualcomm includes */
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

/* Tools includes */
import org.firstinspires.ftc.teamcode.core.tools.Logger;

/* Configuration includes */
import org.firstinspires.ftc.teamcode.core.configuration.ConfServo;

public interface ServoComponent {

    static ServoComponent factory(ConfServo config, HardwareMap map, Logger logger) {

        ServoComponent result = null;

        // Configure motor
        if (config.shallMock()) { result = new ServoMock(config.name(), logger); }
        else if (config.controllers().size() == 1) { result = new ServoSingle(config, map, logger); }
        else if (config.controllers().size() == 2) { result = new ServoCoupled(config, map,  logger); }

        return result;

    }

    /* --------------------- Custom functions ---------------------- */


    boolean                     isReady();
    String                      getName();

    /* ---------- Servo methods override --------- */

    ServoControllerComponent    getController();

    Servo.Direction             getDirection();
    double	                    getPosition();
    void	                    scaleRange(double min, double max);

    void	                    setDirection(Servo.Direction direction);
    void	                    setPosition(double position);

}
