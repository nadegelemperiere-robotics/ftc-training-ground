/* -------------------------------------------------------
   Copyright (c) [2025] Nadege LEMPERIERE
   All rights reserved
   -------------------------------------------------------
   State context for state machine
   ------------------------------------------------------- */
package org.firstinspires.ftc.teamcode.core.orchestration.sequencer;

public class Context {

    State mState;

    public void setState(State state) {
        this.mState = state;
    }

    public void changeState() {
        mState.next(this);
    }

}