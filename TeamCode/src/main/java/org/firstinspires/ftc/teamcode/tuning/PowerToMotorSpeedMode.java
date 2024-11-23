/* -------------------------------------------------------
   Copyright (c) [2024] FASNY
   All rights reserved
   -------------------------------------------------------
   Chain Train Manual Mode
   ------------------------------------------------------- */

package org.firstinspires.ftc.teamcode.tuning;

/* System includes */
import java.io.IOException;
import java.io.File;
import java.io.FileWriter;
import java.util.Map;

/* Android includes */
import android.os.Environment;

/* Qualcomm includes */
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotorEx;

/* Roadrunner includes */
import com.acmerobotics.roadrunner.ftc.Encoder;
import com.acmerobotics.roadrunner.ftc.LogWriter;

/* Ftc Controller includes */
import org.firstinspires.ftc.robotcore.external.Telemetry;

/* Local includes */
import org.firstinspires.ftc.teamcode.configurations.MecanumDriveTrain;
import org.firstinspires.ftc.teamcode.functions.Timing;
import org.firstinspires.ftc.teamcode.functions.Configuration;
import org.firstinspires.ftc.teamcode.robot.Drive;

import java.io.IOException;


@TeleOp()
public class PowerToMotorSpeedMode extends LinearOpMode {
        /** Class managing configuration and control of a basic chain train **/

        private Timing          m_timing;

        private Configuration   m_configuration;

        private Drive           m_drive;

        @Override
        public void runOpMode() {

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

                try {
                        m_drive = new Drive(telemetry);
                        m_drive.configure(hardwareMap, m_configuration);
                        PowerToMotorSpeedMode.check_drive(m_configuration.drive(), m_drive, telemetry);
                        telemetry.addLine("OpMode - Drive initialized");
                } catch (Exception e) {
                        telemetry.addLine("OpMode - Failed drive initialization with error " + e);
                }


                Map<String, DcMotorEx> wheels = m_drive.wheels();
                Map<String, Encoder> encoders = m_drive.encoders();

                /* Prepare log file */
                FileWriter writer = null;
                try {
                        String filepath = Environment.getExternalStorageDirectory().getPath() + "/power-to-motor-speed-mode.csv";
                        writer = new FileWriter(filepath);
                        StringBuilder headers = new StringBuilder("time-ms,pwr-cmd");
                        for (Map.Entry<String, DcMotorEx> entry : wheels.entrySet()) {
                                headers.append(",").append(entry.getKey()).append("-pwr-mes");
                                headers.append(",").append(entry.getKey()).append("-pos");
                                headers.append(",").append(entry.getKey()).append("-vel");
                                headers.append(",").append(entry.getKey()).append("-posraw");
                                headers.append(",").append(entry.getKey()).append("-velraw");
                        }
                        headers.append("\n");
                        writer.write(headers.toString());
                } catch (IOException e) {
                        telemetry.addLine("OpMode - Failed to create log file with error " + e);
                }

                /* Logger configuration */
                telemetry.addLine("OpMode - Init done");
                telemetry.update();


                waitForStart();

                double time = 0;
                for (int iteration = 0; iteration <= 2500; iteration ++ ) {

                        telemetry.addLine("Iteration : " + iteration);

                        double power = (iteration / 50) * 1.0 / 50;
                        time += m_timing.update() * 1000;
                        StringBuilder data = new StringBuilder(String.format("%3.3f",time));
                        data.append(",").append(String.format("%1.3f",power));
                        for (Map.Entry<String, DcMotorEx> entry : wheels.entrySet()) {
                                entry.getValue().setPower(power);
                                data.append(",").append(String.format("%1.3f",entry.getValue().getPower()));
                                data.append(",").append(String.format("%d",encoders.get(entry.getKey()).getPositionAndVelocity().position));
                                data.append(",").append(String.format("%d",encoders.get(entry.getKey()).getPositionAndVelocity().velocity));
                                data.append(",").append(String.format("%d",encoders.get(entry.getKey()).getPositionAndVelocity().rawPosition));
                                data.append(",").append(String.format("%d",encoders.get(entry.getKey()).getPositionAndVelocity().rawVelocity));
                        }
                        data.append("\n");
                        try {
                                writer.write(data.toString());
                        } catch (IOException e) {
                                    telemetry.addLine("OpMode - Failed to write in log file with error " + e);
                        }

                        telemetry.update();
                }

            try {
                writer.close();
            } catch (IOException e) {
                    telemetry.addLine("OpMode - Failed to close log file with error " + e);
            }


        }

        private static void check_drive(String mode, Drive drive, Telemetry logger) throws IOException {

                if(mode.equals("mecanum")) {
                        if (drive.wheel("left-front-wheel") == null) {
                                logger.addLine("Missing left front wheel motor");
                                throw new IOException("Drive configuration failed");
                        }
                        if (drive.wheel("right-front-wheel") == null) {
                                logger.addLine("Missing right front wheel motor");
                                throw new IOException("Drive configuration failed");
                        }
                        if (drive.wheel("left-back-wheel") == null) {
                                logger.addLine("Missing left back wheel motor");
                                throw new IOException("Drive configuration failed");
                        }
                        if (drive.wheel("right-back-wheel") == null) {
                                logger.addLine("Missing right back wheel motor");
                                throw new IOException("Drive configuration failed");
                        }
                }
                else if(mode.equals("tank")) {
                        if (drive.wheel("left-wheel") == null) {
                                logger.addLine("Missing left wheel motor");
                                throw new IOException("Drive configuration failed");
                        }
                        if (drive.wheel("right-wheel") == null) {
                                logger.addLine("Missing right wheel motor");
                                throw new IOException("Drive configuration failed");
                        }
                }
                else {
                        throw new IOException("Unmanaged drive mode : " + mode);
                }

        }


}
