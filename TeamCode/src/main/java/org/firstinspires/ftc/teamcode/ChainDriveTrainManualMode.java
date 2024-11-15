/* -------------------------------------------------------
   Copyright (c) [2024] FASNY
   All rights reserved
   -------------------------------------------------------
   Chain Train Manual Mode
   ------------------------------------------------------- */

package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

/* Local includes */
import org.firstinspires.ftc.teamcode.functions.Piloting;
import org.firstinspires.ftc.teamcode.functions.Timing;
import org.firstinspires.ftc.teamcode.functions.Configuration;

/* Robot include */
import org.firstinspires.ftc.teamcode.configurations.ChainDriveTrain;

@TeleOp()
public class ChainDriveTrainManualMode extends OpMode {
        /** Class managing configuration and control of a basic chain train **/

        private Timing          m_timer;
        private Configuration   m_configuration;
        private Piloting        m_piloting;

        @Override
        public void init() {

                /* Logger configuration */
                telemetry.addLine("OpMode - Init done");

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
                        m_configuration.read(ChainDriveTrain.s_Configuration);
                        telemetry.addLine("OpMode - Configuration read");
                } catch (Exception e) {
                        telemetry.addLine("OpMode - Failed read configuration with error " + e);
                }

                /* Initialize piloting */
                try {
                        m_piloting = Piloting.Builder(m_configuration,telemetry);
                        m_piloting.configure(hardwareMap, gamepad1, m_configuration);
                        telemetry.addLine("OpMode - Piloting initialized");
                } catch (Exception e) {
                        telemetry.addLine("OpMode - Failed to initialize piloting with error " + e);
                }

        }

        @Override
        public void loop(){

                /* Compute processing time */
                double delay = m_timer.update();
                telemetry.addLine("OpMode - Delay : " + delay + " s");
                telemetry.addLine("OpMode - Processing frequency : " + m_timer.frequency());

                /* Update piloting */
                m_piloting.update();

        }
}
