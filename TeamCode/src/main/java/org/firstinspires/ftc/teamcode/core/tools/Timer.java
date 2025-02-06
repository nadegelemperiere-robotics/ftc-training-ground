/* -------------------------------------------------------
   Copyright (c) [2025] Nadege LEMPERIERE
   All rights reserved
   -------------------------------------------------------
   Timer
   ------------------------------------------------------- */

package org.firstinspires.ftc.teamcode.core.tools;

public class Timer {

    Logger          mLogger;

    private long    mStartTime;
    private boolean mIsRunning;
    private boolean mHasAlreadyBeenCalled;
    private int     mTarget;

    public Timer(Logger logger)
    {
        mIsRunning              = false;
        mHasAlreadyBeenCalled   = false;
        mLogger                 = logger;
    }

    public void set(int milliseconds)
    {
        mStartTime            = System.nanoTime();
        mIsRunning            = true;
        mTarget               = milliseconds;
        mHasAlreadyBeenCalled = true;
    }

    public boolean isOn()
    {
        if(mHasAlreadyBeenCalled) {
            double delta = (System.nanoTime() - mStartTime) / 1_000_000.0;
            if (delta >= mTarget) { mIsRunning = false; }
        }
        return mIsRunning;
    }

    public boolean isOff()
    {
        if(mHasAlreadyBeenCalled) {
            double delta = (System.nanoTime() - mStartTime) / 1_000_000.0;
            if (delta >= mTarget) { mIsRunning = false; }
        }
        return !mIsRunning;
    }

}