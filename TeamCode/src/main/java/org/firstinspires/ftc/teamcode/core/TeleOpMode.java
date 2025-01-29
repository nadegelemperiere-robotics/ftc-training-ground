/* -------------------------------------------------------
   Copyright (c) [2025] Nadege LEMPERIERE
   All rights reserved
   -------------------------------------------------------
   Into-The-Deep TeleOp mode
   ------------------------------------------------------- */

package org.firstinspires.ftc.teamcode.core;

/* Qualcomm includes */
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

/* ACME robotics includes */
import com.acmerobotics.dashboard.FtcDashboard;

/* Tools includes */
import org.firstinspires.ftc.teamcode.core.tools.Logger;

@TeleOp
public class TeleOpMode extends OpMode {

    Logger mLogger;


    @Override
    public void init(){

        try {
            mLogger = new Logger(telemetry, FtcDashboard.getInstance());
        }
        catch(Exception e){

        }

    }
    @Override
    public void loop (){

        try {

        }
        catch(Exception e){

        }
        
    }
}
