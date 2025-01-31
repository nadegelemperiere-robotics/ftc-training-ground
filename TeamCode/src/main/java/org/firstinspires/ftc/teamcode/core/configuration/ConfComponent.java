/* -------------------------------------------------------
   Copyright (c) [2025] Nadege LEMPERIERE
   All rights reserved
   -------------------------------------------------------
   Component configuration interface management
   ------------------------------------------------------- */

package org.firstinspires.ftc.teamcode.core.configuration;

/* System includes */
import org.json.JSONObject;

public interface ConfComponent {

    /* --------------- Accessors -------------- */
    boolean isValid();
    String  name();

    /* ------------------ I/O ----------------- */
    void    read(JSONObject reader);
    void    write(JSONObject writer);
    String  log();

}