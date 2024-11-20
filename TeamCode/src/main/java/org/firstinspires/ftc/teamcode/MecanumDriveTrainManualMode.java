/* -------------------------------------------------------
   Copyright (c) [2024] FASNY
   All rights reserved
   -------------------------------------------------------
   Chain Train Automonous Mode
   ------------------------------------------------------- */

package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;


/* Local includes */
import org.firstinspires.ftc.teamcode.functions.Piloting;
import org.firstinspires.ftc.teamcode.functions.Timing;
import org.firstinspires.ftc.teamcode.functions.Configuration;
import org.firstinspires.ftc.teamcode.functions.Localization;

/* Robot include */
import org.firstinspires.ftc.teamcode.configurations.MecanumDriveTrain;

@TeleOp()
public class MecanumDriveTrainManualMode extends OpMode {
        /** Class managing configuration and control of a basic chain train **/

        private Timing          m_timing;
        private Configuration   m_configuration;
        private Localization    m_localization;
        private Piloting        m_piloting;

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

                /* Initialize piloting */
                try {
                        m_piloting = Piloting.Builder(m_configuration,Piloting.Centricity.ROBOT, telemetry);
                        m_piloting.configure(hardwareMap, gamepad1, m_configuration);
                        telemetry.addLine("OpMode - Piloting initialized");
                } catch (Exception e) {
                        telemetry.addLine("OpMode - Failed to initialize piloting with error " + e);
                }

        }

        @Override
        public void loop(){

                /* Compute processing time */
                m_timing.update();

                /* Update localization */
                m_localization.update();

                /* Update piloting */
                m_piloting.update();


        }
}
