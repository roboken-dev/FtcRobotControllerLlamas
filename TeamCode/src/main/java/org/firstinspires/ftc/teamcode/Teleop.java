package org.firstinspires.ftc.teamcode;

import android.app.Activity;
import android.graphics.Color;
import android.view.View;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DistanceSensor;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

import java.util.Locale;


@TeleOp(name = "TeleOp", group = "12806")
public class Teleop extends LinearOpMode {

    Robokenbot robot   = new Robokenbot();

    @Override
    public void runOpMode() throws InterruptedException {
        double speed_control = 0.5;
        double ArmSpeedControl = 0.6;


        robot.init(hardwareMap,this);

        // hsvValues is an array that will hold the hue, saturation, and value information.
        float hsvValues[] = {0F, 0F, 0F};

        // values is a reference to the hsvValues array.
        final float values[] = hsvValues;

        // sometimes it helps to multiply the raw RGB values with a scale factor
        // to amplify/attentuate the measured values.
        final double SCALE_FACTOR = 255;

        // get a reference to the RelativeLayout so we can change the background
        // color of the Robot Controller app to match the hue detected by the RGB sensor.
        int relativeLayoutId = hardwareMap.appContext.getResources().getIdentifier("RelativeLayout", "id", hardwareMap.appContext.getPackageName());
        final View relativeLayout = ((Activity) hardwareMap.appContext).findViewById(relativeLayoutId);

        robot.motorFrontRight.setDirection(DcMotor.Direction.REVERSE);
        robot.motorRearLeft.setDirection(DcMotor.Direction.REVERSE);
        robot.motorFrontLeft.setDirection(DcMotor.Direction.FORWARD);
        robot.motorRearRight.setDirection(DcMotor.Direction.FORWARD);



        telemetry.addData("Status", "Ready to Go");    //
        telemetry.update();

        waitForStart();

        while (opModeIsActive()) {
            // convert the RGB values to HSV values.
            // multiply by the SCALE_FACTOR.
            // then cast it back to int (SCALE_FACTOR is a double)
            Color.RGBToHSV((int) (robot.bottomSensorColor.red() * SCALE_FACTOR),
                    (int) (robot.bottomSensorColor.green() * SCALE_FACTOR),
                    (int) (robot.bottomSensorColor.blue() * SCALE_FACTOR),
                    hsvValues);

            // send the info back to driver station using telemetry function.
            telemetry.addData("Distance (cm)",
                    String.format(Locale.US, "%.02f", robot.sensorDistance.getDistance(DistanceUnit.CM)));
            telemetry.addData("Alpha", robot.sensorColor.alpha());
            telemetry.addData("Red  ", robot.sensorColor.red());
            telemetry.addData("Green", robot.sensorColor.green());
            telemetry.addData("Blue ", robot.sensorColor.blue());
            telemetry.addData("Hue", hsvValues[0]);

            // change the background color to match the color detected by the RGB sensor.
            // pass a reference to the hue, saturation, and value array as an argument
            // to the HSVToColor method.
            relativeLayout.post(new Runnable() {
                public void run() {
                    relativeLayout.setBackgroundColor(Color.HSVToColor(0xff, values));
                }
            });



            double G1rightStickY = gamepad1.right_stick_y;
            double G1leftStickY = gamepad1.left_stick_y;
            float G1rightTrigger = gamepad1.right_trigger;
            float G1leftTrigger = gamepad1.left_trigger;
            float G2leftTrigger = gamepad2.left_trigger;
            float G2rightTrigger = gamepad2.right_trigger;


            //Driver 1 wheel speed control
            if (gamepad1.dpad_up) {
                speed_control = 1;
                telemetry.addData("Status", "Setting Speed to 1");    //
                telemetry.update();
            }
            if (gamepad1.dpad_down) {
                speed_control = 0.25f;
                telemetry.addData("Status", "Setting Speed to .25");    //
                telemetry.update();
            }
            if (gamepad1.dpad_left) {
                speed_control = 0.5f;
                telemetry.addData("Status", "Setting Speed to .5");    //
                telemetry.update();
            }
            if (gamepad1.dpad_right) {
                speed_control = 0.5f;
                telemetry.addData("Status", "Setting Speed to .5");    //
                telemetry.update();
            }



            if (G1rightTrigger > 0 && G1leftTrigger == 0) {

                //Strafe Right
                robot.motorFrontLeft.setPower(-G1rightTrigger * speed_control);
                robot.motorRearLeft.setPower(G1rightTrigger * speed_control);
                robot.motorFrontRight.setPower(G1rightTrigger * speed_control);
                robot.motorRearRight.setPower(-G1rightTrigger * speed_control);

                telemetry.addData("Status", "Strafing Right");    //
                telemetry.update();

            } else if (G1leftTrigger > 0 && G1rightTrigger == 0) {

                //Strafe Left
                robot.motorFrontLeft.setPower(G1leftTrigger * speed_control);
                robot.motorRearLeft.setPower(-G1leftTrigger * speed_control);
                robot.motorFrontRight.setPower(-G1leftTrigger * speed_control);
                robot.motorRearRight.setPower(G1leftTrigger * speed_control);

                telemetry.addData("Status", "Strafing Left");    //
                telemetry.update();

            } else {

                //Tank Drive
                robot.motorFrontLeft.setPower(G1leftStickY * Math.abs(G1leftStickY) * speed_control);
                robot.motorRearLeft.setPower(G1leftStickY * Math.abs(G1leftStickY) * speed_control);
                robot.motorFrontRight.setPower(G1rightStickY * Math.abs(G1rightStickY)  * speed_control);
                robot.motorRearRight.setPower(G1rightStickY * Math.abs(G1rightStickY)  * speed_control);

                telemetry.addData("Status", "Moving");    //
                telemetry.update();

            }

            //Claw set position
            if (gamepad2.left_bumper) {
                robot.claw.setPosition(0.0);
                telemetry.addData("Status", "Claw");    //
                telemetry.update();
            }

            if (gamepad2.right_bumper) {
                robot.claw.setPosition(1.0);
                telemetry.addData("Status", "Claw");    //
                telemetry.update();
                idle();

            }



            if (gamepad2.left_stick_y > 0 || gamepad2.left_stick_y < 0) {
                robot.arm.setPower(gamepad2.left_stick_y * ArmSpeedControl+0.2);
            }

            if (gamepad2.left_stick_y < 0) {
                robot.arm.setPower(gamepad2.left_stick_y *( ArmSpeedControl));
            }

            if (gamepad2.left_stick_y == 0) {
                robot.arm.setPower(0.0);

            }

            if (gamepad2.y)
            {
                ArmSpeedControl = 1.0;
            }


            if (gamepad2.a)
            {
                ArmSpeedControl = 0.4;
            }
            if (gamepad2.b)
            {
                ArmSpeedControl = 0.8;
            }

            if (G2leftTrigger > 0) {
                robot.spinner.setPower(0.6);
            } else if (G2rightTrigger > 0) {
                robot.spinner.setPower(-0.6);
            } else {
                robot.spinner.setPower(0);
            }

        }

        // Set the panel back to the default color
        relativeLayout.post(new Runnable() {
            public void run() {
                relativeLayout.setBackgroundColor(Color.WHITE);
            }
        });
    }
}
