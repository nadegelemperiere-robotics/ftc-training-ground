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

/* Configuration includes */
import org.firstinspires.ftc.teamcode.core.configuration.Configuration;

/* Functions includes */
import org.firstinspires.ftc.teamcode.core.functions.control.Control;


@TeleOp
public class TeleOpMode extends OpMode {

    Logger          mLogger;

    Configuration   mConfiguration;

    Control         mControl;


    @Override
    public void init(){

        try {
            // Log initialization
            mLogger = new Logger(telemetry, FtcDashboard.getInstance());

            // Configuration initialization
            mConfiguration = new Configuration(mLogger);
            mConfiguration.read();
            mConfiguration.log();

            mControl = new Control(List<gamepad> gamepads);
        }
        catch(Exception e){

        }

    }
    @Override
    public void loop (){

        try {

            mControl.loop();

        }
        catch(Exception e){

        }
        
    }
}
