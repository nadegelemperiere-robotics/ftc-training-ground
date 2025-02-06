/* -------------------------------------------------------
   Copyright (c) [2025] Nadege LEMPERIERE
   All rights reserved
   -------------------------------------------------------
   State interface for state machine
   ------------------------------------------------------- */
package org.firstinspires.ftc.teamcode.core.orchestration.sequencer;

public interface State {

    void    next(Context context);

}