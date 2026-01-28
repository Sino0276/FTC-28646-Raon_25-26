package org.firstinspires.ftc.teamcode.opmodes.TeleOp;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.arcrobotics.ftclib.hardware.motors.Motor;
import com.arcrobotics.ftclib.hardware.motors.MotorEx;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

@TeleOp(name = "TurretZeropointSetting", group = "TeleOp")
public class TurretZeropointSetting extends OpMode {

    private MotorEx turretMotor;
    private MultipleTelemetry telemetry;

    @Override
    public void init() {
        telemetry = new MultipleTelemetry(FtcDashboard.getInstance().getTelemetry());

        turretMotor = new MotorEx(hardwareMap, "turret", Motor.GoBILDA.RPM_312);
        turretMotor.resetEncoder();
        turretMotor.motorEx.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
    }

    @Override
    public void loop() {
        telemetry.addData("heading", turretMotor.getCurrentPosition());
        telemetry.update();
    }
}
