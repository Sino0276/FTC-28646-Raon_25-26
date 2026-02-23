package org.firstinspires.ftc.teamcode.opmodes.auto;

import com.arcrobotics.ftclib.command.CommandOpMode;
import com.arcrobotics.ftclib.gamepad.GamepadKeys;
import com.arcrobotics.ftclib.hardware.motors.Motor;
import com.arcrobotics.ftclib.hardware.motors.MotorEx;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.Range;

@Autonomous(name = "GoingFront", group = "Autonomous")
public class GoingFront extends LinearOpMode {
    private MotorEx mtr_rr, mtr_rf, mtr_lr, mtr_lf;

    @Override
    public void runOpMode() throws InterruptedException {
        mtr_rr = new MotorEx(hardwareMap, "mtr_rr");
        mtr_rf = new MotorEx(hardwareMap, "mtr_rf");
        mtr_lr = new MotorEx(hardwareMap, "mtr_lr");
        mtr_lf = new MotorEx(hardwareMap, "mtr_lf");
        mtr_rr.setZeroPowerBehavior(Motor.ZeroPowerBehavior.BRAKE);
        mtr_rf.setZeroPowerBehavior(Motor.ZeroPowerBehavior.BRAKE);
        mtr_lr.setZeroPowerBehavior(Motor.ZeroPowerBehavior.BRAKE);
        mtr_lf.setZeroPowerBehavior(Motor.ZeroPowerBehavior.BRAKE);
        mtr_lr.setInverted(true);
        mtr_lf.setInverted(true);

        waitForStart();
        if (opModeIsActive()) {
            sleep(20000);
            drive(0, 0.5, 0);
//            wait(1000);
            sleep(1000);
            drive(0, 0, 0);
        }
    }

    private void drive(double x, double y, double rx) {
        mtr_lf.set(Range.clip(y + x + rx, -1, 1));
        mtr_rf.set(Range.clip(y - x - rx, -1, 1));
        mtr_lr.set(Range.clip(y - x + rx, -1, 1));
        mtr_rr.set(Range.clip(y + x - rx, -1, 1));
    }
}
