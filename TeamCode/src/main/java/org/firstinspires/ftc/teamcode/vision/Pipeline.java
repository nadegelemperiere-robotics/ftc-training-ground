/* -------------------------------------------------------
   Copyright (c) [2024] FASNY
   All rights reserved
   -------------------------------------------------------
   Drive Train vision Pipeline
   ------------------------------------------------------- */
package org.firstinspires.ftc.teamcode.vision;

/* Ftc Controller includes */
import org.firstinspires.ftc.robotcore.external.Telemetry;

/* OpenCV includes */
import org.openftc.easyopencv.OpenCvPipeline;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

public class Pipeline extends OpenCvPipeline {

    Telemetry m_logger;

    public Pipeline(Telemetry logger) {
        m_logger = logger;
    }

    @Override
    public Mat processFrame(Mat input) {
        m_logger.addData("Pipeline","Processing Frame");
        // Return the processed frame
        return input;
    }
}
