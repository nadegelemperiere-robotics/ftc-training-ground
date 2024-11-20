/* -------------------------------------------------------
   Copyright (c) [2024] FASNY
   All rights reserved
   -------------------------------------------------------
   Robot time estimation
   ------------------------------------------------------- */
package org.firstinspires.ftc.teamcode.functions;

/* System includes */
import org.firstinspires.ftc.robotcore.external.Telemetry;

import java.util.LinkedList;

public class Timing {

    private Telemetry           m_logger;

    private long                m_last_call_time;   // Timestamp of the previous call
    private LinkedList<Double>  m_intervals;        // List of recent intervals in seconds
    private int                 m_max_intervals;    // Maximum number of intervals to consider for mean frequency

    public Timing(Telemetry logger)
    {
        m_last_call_time = -1;
        m_intervals = new LinkedList<>();
        m_max_intervals = 20;
        m_logger = logger;
    }

    public void reset()
    {
        m_last_call_time = -1;
        m_intervals.clear();
    }

    public double update() {

        double result = -1;

        long current_time = System.nanoTime(); // Get current time in nanoseconds

        // Calculate frequency if there was a previous call
        if (m_last_call_time != -1) {
            result = (current_time - m_last_call_time) * 1.0 / 1_000_000_000.0;

            m_intervals.add(result);
            if (m_intervals.size() > m_max_intervals) {
                m_intervals.removeFirst(); // Maintain only the latest `maxIntervals` intervals
            }
        }

        // Update last call time for the next interval
        m_last_call_time = current_time;

        m_logger.addLine(String.format("\n===> TIMING "));
        m_logger.addLine(String.format("PF %6.1f ms %6.1f Hz",
            result * 1000,
            this.frequency()));
            
        return result;

    }

    public double frequency() {

        double result = -1;
        
        if (!m_intervals.isEmpty()) {
            double sum = 0;
            for (double interval : m_intervals) { sum += interval; }
            result = m_intervals.size() / sum;
        }

        return result;
    }
}
