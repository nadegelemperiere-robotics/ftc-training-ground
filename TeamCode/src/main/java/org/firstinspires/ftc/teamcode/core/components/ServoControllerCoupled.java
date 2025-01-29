/* -------------------------------------------------------
   Copyright (c) [2025] FASNY
   All rights reserved
   -------------------------------------------------------
   Coupled Controller managing coupled servos together
   ------------------------------------------------------- */

package org.firstinspires.ftc.teamcode.core.components;

/* Qualcomm includes */
import com.qualcomm.robotcore.hardware.ServoController;

/* Tools includes */
import org.firstinspires.ftc.teamcode.core.tools.Logger;


public class ServoControllerCoupled implements ServoControllerComponent {

    Logger                  mLogger;

    boolean                 mReady;

    ServoController         mFirst;
    ServoController         mSecond;

    /* -------------- Constructors --------------- */
    public ServoControllerCoupled(ServoController first, ServoController second, Logger logger)
    {
        mReady  = true;

        mLogger = logger;

        mFirst  = first;
        mSecond = second;

        if(mFirst  == null) { mReady = false; }
        if(mSecond == null) { mReady = false; }
    }

    /* --------------------- Custom functions ---------------------- */

    @Override
    public boolean                      isReady() { return mReady;}


    /* ----------------- ServoController functions ----------------- */

    @Override
    public void	                        pwmEnable(){
        if(mReady) {
            mFirst.pwmEnable();
            mSecond.pwmDisable();
        }
    }

    @Override
    public void	                        pwmDisable(){
        if(mReady) {
            mFirst.pwmDisable();
            mSecond.pwmDisable();
        }
    }
    @Override
    public ServoController.PwmStatus	getPwmStatus(){
        ServoController.PwmStatus result = ServoController.PwmStatus.DISABLED;
        if(mReady) {
            result = mFirst.getPwmStatus();
        }
        return result;
    }

}
