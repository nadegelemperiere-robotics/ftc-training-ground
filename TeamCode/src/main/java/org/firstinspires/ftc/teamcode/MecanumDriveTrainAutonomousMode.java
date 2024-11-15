/* -------------------------------------------------------
   Copyright (c) [2024] FASNY
   All rights reserved
   -------------------------------------------------------
   Chain Train Automonous Mode
   ------------------------------------------------------- */

package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;


/* Local includes */
import org.firstinspires.ftc.teamcode.functions.Timing;
import org.firstinspires.ftc.teamcode.functions.Configuration;
import org.firstinspires.ftc.teamcode.functions.Localization;

/* Robot include */
import org.firstinspires.ftc.teamcode.configurations.MecanumDriveTrain;

@Autonomous()
public class MecanumDriveTrainAutonomousMode extends OpMode {
        /** Class managing configuration and control of a basic chain train **/

        private Timing           m_timer;               
        private Configuration   m_configuration;
        private Localization    m_localization;

        @Override
        public void init() {

                /* Logger configuration */
                telemetry.addLine("OpMode - Init start");

                /* Timing creation */
                try {
                        m_timer = new Timing();
                        telemetry.addLine("OpMode - Timing initialized");
                } catch (Exception e) {
                       telemetry.addLine("OpMode - Failed to create timer with error " + e);
                }

                /* Parse robot configuration */
                try {
                        m_configuration = new Configuration(telemetry);
                        m_configuration.read(MecanumDriveTrain.s_Configuration);
                        telemetry.addLine("OpMode - Configuration read");
                } catch (Exception e) {
                        telemetry.addLine("OpMode - Failed read configuration with error " + e);
                }

                /* Initialize localization */
                try {
                m_localization = new Localization(telemetry);
                m_localization.configure(hardwareMap,m_configuration);
                telemetry.addLine("OpMode - Localization initialized");
                } catch (Exception e) {
                        telemetry.addLine("OpMode - Failed to initialize location with error " + e);
                }
                
                telemetry.addLine("OpMode - Init done");

        }

        @Override
        public void loop(){

                /* Compute processing time */
                double delay = m_timer.update();
                telemetry.addLine("OpMode - Delay : " + delay + " s");
                telemetry.addLine("OpMode - Processing frequency : " + m_timer.frequency());

                /* Update localization */
                m_localization.update();


        }
        
        @Override
        public void stop(){

                telemetry.addLine("OpMode - Stopping start");
                m_localization.stop();
                telemetry.addLine("OpMode - Stopping start");

        }
}
