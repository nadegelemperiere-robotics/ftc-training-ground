/* -------------------------------------------------------
   Copyright (c) [2024] FASNY
   All rights reserved
   -------------------------------------------------------
   Chain Train Autonomous Mode
   ------------------------------------------------------- */

package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

/* Local includes */
import org.firstinspires.ftc.teamcode.functions.Timing;
import org.firstinspires.ftc.teamcode.functions.Configuration;

/* Robot include */
import org.firstinspires.ftc.teamcode.configurations.ChainDriveTrain;

@Autonomous()
public class ChainDriveTrainAutonomousMode extends OpMode {
        /** Class managing configuration and control of a basic chain train **/

        private Timing          m_timing;
        private Configuration   m_configuration;

        @Override
        public void init() {

                /* Logger configuration */
                telemetry.addLine("OpMode - Init done");

                /* Timing creation */
                try {
                        m_timing = new Timing(telemetry);
                        telemetry.addLine("OpMode - Timing initialized");
                } catch (Exception e) {
                        telemetry.addLine("OpMode - Failed to create timer with error " + e);
                }

                /* Parse robot configuration */
                try {
                        m_configuration = new Configuration(telemetry);
                        m_configuration.read(ChainDriveTrain.s_Configuration);
                        telemetry.addLine("OpMode - Configuration read");
                } catch (Exception e) {
                        telemetry.addLine("OpMode - Failed read configuration with error " + e);
                }


        }

        @Override
        public void loop(){

                /* Compute processing time */
                double delay = m_timing.update();
                telemetry.addLine("OpMode - Delay : " + delay + " s");
                telemetry.addLine("OpMode - Processing frequency : " + m_timing.frequency());


        }
}
