/* -------------------------------------------------------
   Copyright (c) [2025] Nadege LEMPERIERE
   All rights reserved
   -------------------------------------------------------
   Object collection logic
   ------------------------------------------------------- */

package org.firstinspires.ftc.teamcode.core.functions.control;

/* Sequencer includes */
import org.firstinspires.ftc.teamcode.core.orchestration.sequencer.State;

abstract class ControlState implements State {


    /* --------------- Accessors -------------- */
    /* ---------------- Driving --------------- */


    /* -------------- Collecting -------------- */
    abstract    void    extendIntakeSlides();
    abstract    void    extendOuttakeSlides();
    abstract    void    rollbackIntakeSlides();
    abstract    void    rollbackOuttakeSlides();
    abstract    void    stopIntakeSlides();
    abstract    void    stopOuttakeSlides();




}