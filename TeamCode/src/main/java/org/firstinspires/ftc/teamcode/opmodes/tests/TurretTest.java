package org.firstinspires.ftc.teamcode.opmodes.tests;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.arcrobotics.ftclib.hardware.ServoEx;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

@Disabled
//@Config
@TeleOp(name = "TurretTest", group = "Test")
public class TurretTest extends OpMode {
    public static DcMotorSimple.Direction TURRET_DIRECTION = DcMotorSimple.Direction.FORWARD;
    public static DcMotorSimple.Direction SHOOTER_DIRECTION = DcMotorSimple.Direction.FORWARD;
    public static double POWER = 0.7;

    private DcMotorEx turret, shooter;

    private MultipleTelemetry multipleTelemetry;

    @Override
    public void init() {
        turret = hardwareMap.get(DcMotorEx.class, "turret");
        shooter = hardwareMap.get(DcMotorEx.class, "shooter");
        shooter.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        multipleTelemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());
    }

    @Override
    public void loop() {
        turret.setDirection(TURRET_DIRECTION);
        shooter.setDirection(SHOOTER_DIRECTION);

        turret.setPower(gamepad1.left_stick_y);
        shooter.setPower(gamepad1.right_stick_y);

        multipleTelemetry.addData("Left Stick Y (Turret Power)", gamepad1.left_stick_y);
        multipleTelemetry.addData("Right Stick Y (Shooter Power)", gamepad1.right_stick_y);
        multipleTelemetry.update();
    }
}
