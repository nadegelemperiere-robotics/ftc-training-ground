package org.firstinspires.ftc.teamcode;


import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.TouchSensor;
import com.qualcomm.robotcore.hardware.ColorSensor;


@TeleOp()
public class HelloWorld extends OpMode {

    private TouchSensor touchSensor;
    private ColorSensor colorSensor;
    private int         increment;

    @Override
    public void init() {
        telemetry.addData("Hello","World");
        touchSensor = hardwareMap.touchSensor.get("touchSensor");
        colorSensor = hardwareMap.colorSensor.get("colorSensor");
        increment = 0;
    }

    @Override
    public void loop() {
        increment += 1;
        telemetry.addData("Here :",increment) ;
        telemetry.addData("Touch sensor state :",this.getTouchSensorState()) ;
        telemetry.addData("Color Sensor State",this.getColor());
    }

    public boolean getTouchSensorState() {
        return touchSensor.isPressed();
    }

    public String getColor()
    {
        return Integer.toString(colorSensor.red()) + ',' + Integer.toString(colorSensor.green()) + ',' + Integer.toString(colorSensor.blue());
    }


}