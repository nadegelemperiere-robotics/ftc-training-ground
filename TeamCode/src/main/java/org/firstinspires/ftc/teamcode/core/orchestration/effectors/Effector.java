/* -------------------------------------------------------
   Copyright (c) [2025] Nadege LEMPERIERE
   All rights reserved
   -------------------------------------------------------
   Component configuration interface management
   ------------------------------------------------------- */

package org.firstinspires.ftc.teamcode.core.orchestration.effectors;

/* System includes */
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.core.configuration.ConfMotor;
import org.firstinspires.ftc.teamcode.core.tools.Logger;
import org.json.JSONObject;

public interface Effector {

    static Effector factory(ConfMotor config, HardwareMap map, Logger logger) {
        Effector result = null;
        return result;
    }

    /* --------------- Accessors -------------- */
    boolean isValid();
    String  name();

    /* ------------------ I/O ----------------- */
    void    read(JSONObject reader);
    void    write(JSONObject writer);
    String  log();
    String  getPosition();
    void    persist();

    /* ---------------- Actions --------------- */
    void    setPosition(String position, double tolerance, int timeout);
    void    setPosition(String position, double tolerance);

}