/* -------------------------------------------------------
   Copyright (c) [2025] Nadege LEMPERIERE
   All rights reserved
   -------------------------------------------------------
   MotorComponent is an interface for motor management
   It supersedes DcMotorEx and provides additional capabilities
   such as :
   - Correcting orientation error on encoder
   - Synchronizing 2 coupled motors
   ------------------------------------------------------- */

package org.firstinspires.ftc.teamcode.core.components;

/* System includes */
import java.util.Map;

/* Qualcomm includes */
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;

/* Configuration includes */
import org.firstinspires.ftc.teamcode.core.configuration.ConfMotor;

/* Tools includes */
import org.firstinspires.ftc.teamcode.core.tools.Logger;


public interface MotorComponent {

    Map<String, DcMotor.Direction> sString2Direction = Map.of(
            "reverse", DcMotor.Direction.REVERSE,
            "forward",DcMotor.Direction.FORWARD
    );

    static MotorComponent factory(ConfMotor config, HardwareMap map, Logger logger) {

        MotorComponent result = null;

        // Configure motor
        if (config.shallMock()) { result = new MotorMock(config.name(), logger); }
        else if (config.controllers().size() == 1) { result = new MotorSingle(config, map, logger); }
        else if (config.controllers().size() == 2) { result = new MotorCoupled(config, map,  logger); }

        return result;

    }

    /* --------------------- Custom functions ---------------------- */

    String                      logPositions();
    boolean                     isReady();
    String                      getName();
    boolean                     getEncoderCorrection();

    /* --------------------- DcMotor functions --------------------- */

    boolean	                    isBusy();

    int	                        getCurrentPosition();
    DcMotor.RunMode	            getMode();
    int	                        getTargetPosition();
    DcMotorSimple.Direction     getDirection();
    DcMotor.ZeroPowerBehavior	getZeroPowerBehavior();
    double                      getPower();

    void	                    setMode(DcMotor.RunMode mode);
    void	                    setDirection(DcMotorSimple.Direction direction);
    void	                    setTargetPosition(int position);
    void	                    setZeroPowerBehavior(DcMotor.ZeroPowerBehavior zeroPowerBehavior);
    void                        setPower(double power);

    /* -------------------- DcMotorEx functions -------------------- */

    void                        setPIDFCoefficients(DcMotor.RunMode mode, PIDFCoefficients pidfCoefficients);
    PIDFCoefficients            getPIDFCoefficients(DcMotor.RunMode mode);
    void                        setTargetPositionTolerance(int tolerance);
    int                         getTargetPositionTolerance();
    double                      getVelocity();


}
