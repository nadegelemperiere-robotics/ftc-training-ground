/* -------------------------------------------------------
   Copyright (c) [2025] FASNY
   All rights reserved
   -------------------------------------------------------
   Controller managing mock servos
   ------------------------------------------------------- */

package org.firstinspires.ftc.teamcode.core.components;

/* Qualcomm includes */
import com.qualcomm.robotcore.hardware.ServoController;

/* Tools includes */
import org.firstinspires.ftc.teamcode.core.tools.Logger;


public class ServoControllerMock implements ServoControllerComponent {

    Logger                      mLogger;

    boolean                     mReady;

    ServoController.PwmStatus   mStatus;


    /* -------------- Constructors --------------- */
    public ServoControllerMock( Logger logger)
    {
        mReady  = true;
        mLogger = logger;
        mStatus = ServoController.PwmStatus.DISABLED;
    }

    /* --------------------- Custom functions ---------------------- */

    @Override
    public boolean                      isReady() { return mReady;}

    /* ----------------- ServoController functions ----------------- */

    @Override
    public void	                        pwmEnable(){
        mStatus = ServoController.PwmStatus.ENABLED;
    }

    @Override
    public void	                        pwmDisable(){
        mStatus = ServoController.PwmStatus.DISABLED;
    }

    @Override
    public ServoController.PwmStatus	getPwmStatus() { return mStatus; }

}
