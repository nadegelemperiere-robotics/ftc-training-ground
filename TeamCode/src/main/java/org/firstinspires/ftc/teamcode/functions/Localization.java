/* -------------------------------------------------------
   Copyright (c) [2024] FASNY
   All rights reserved
   -------------------------------------------------------
   Robot absolute location estimation
   ------------------------------------------------------- */
package org.firstinspires.ftc.teamcode.functions;


/* System includes */
import android.util.Size;

import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

/* Qualcomm includes */
import com.qualcomm.robotcore.hardware.HardwareMap;

/* Ftc Controller includes */
import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Position;
import org.firstinspires.ftc.robotcore.external.navigation.YawPitchRollAngles;
import org.firstinspires.ftc.robotcore.external.navigation.Pose3D;
import org.firstinspires.ftc.robotcore.external.matrices.VectorF;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;
import org.firstinspires.ftc.vision.apriltag.AprilTagMetadata;

/* Local includes */
import org.firstinspires.ftc.teamcode.functions.Configuration;
import org.firstinspires.ftc.teamcode.robots.Component;


public class Localization {

    private Telemetry                   m_logger;
    private List<AprilTagProcessor>     m_tag_processors;
    private List<WebcamName>            m_cameras;
    private List<VisionPortal>          m_vision_portals;

    /* Robot current position and orientation in FTC reference frame */
    private Position                    m_current_position;
    private YawPitchRollAngles          m_current_orientation;

    public Localization(Telemetry logger) {
        m_logger  = logger;
        m_cameras = new ArrayList<>();
        m_vision_portals = new ArrayList<>();
        m_tag_processors = new ArrayList<>();
    }

    public void configure(HardwareMap map, Configuration config) throws IOException {
        m_cameras.clear();

        List<Component> cameras = config.get("type","camera");
        for (Component component : cameras) {

            m_logger.addLine("Localization - Found camera : " + component.m_name);
            WebcamName webcam = map.get(WebcamName.class, component.m_hardware);
            AprilTagProcessor processor = AprilTagProcessor.easyCreateWithDefaults();
            m_cameras.add(webcam);
            m_tag_processors.add(processor);
            m_vision_portals.add(new VisionPortal.Builder()
                    .setCamera(webcam)
                    .enableLiveView(true)
                    .setCameraResolution(new Size(640,480))
                    .setAutoStartStreamOnBuild(true)
                    .addProcessor(processor)
                    .build());
        }

    }

    public void update() {


        for (AprilTagProcessor processor : m_tag_processors) {
            List<AprilTagDetection> detections = processor.getDetections();
            for (AprilTagDetection detection : detections) {

                m_current_position = Localization.compute_position(detection.robotPose, detection.frameAcquisitionNanoTime);
                m_current_orientation = Localization.compute_orientation(detection.robotPose, detection.frameAcquisitionNanoTime);

                m_logger.addLine(String.format("\n==== (ID %d) %s", detection.id, detection.metadata.name));
                m_logger.addLine(String.format("XYZ %6.1f %6.1f %6.1f  (inch)",
                        m_current_position.x,
                        m_current_position.y,
                        m_current_position.z));
                m_logger.addLine(String.format("PRY %6.1f %6.1f %6.1f  (deg)",
                        m_current_orientation.getPitch(AngleUnit.DEGREES),
                        m_current_orientation.getRoll(AngleUnit.DEGREES),
                        m_current_orientation.getYaw(AngleUnit.DEGREES)));

            }
        }


    }

    public void stop() {

        m_logger.addLine("Localization - Stopping video portal");
        for (VisionPortal portal : m_vision_portals) {
            portal.close();
        }

    }

    static private YawPitchRollAngles compute_orientation(Pose3D pose, long date){

        double yaw = pose.getOrientation().getYaw(AngleUnit.DEGREES) + 90;
        double roll = pose.getOrientation().getRoll(AngleUnit.DEGREES);
        double pitch = pose.getOrientation().getPitch(AngleUnit.DEGREES) + 90;
        return new YawPitchRollAngles(AngleUnit.DEGREES, yaw, pitch, roll,date);

    }
    static private Position compute_position(Pose3D pose, long date){
        return pose.getPosition();
    }
}
