package org.firstinspires.ftc.teamcode.opmodes.tests;

import com.acmerobotics.dashboard.config.Config;
import com.arcrobotics.ftclib.command.CommandOpMode;
import com.arcrobotics.ftclib.gamepad.GamepadEx;
import com.arcrobotics.ftclib.gamepad.GamepadKeys;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.commands.mech.LiftCommand;
import org.firstinspires.ftc.teamcode.subsystems.LiftSubsystem;

@TeleOp (name="LiftTest", group = "Test")
public class LitfTest extends CommandOpMode {

    private LiftSubsystem lift;
    private GamepadEx driverGamepad;

    @Override
    public void initialize() {
        // 1. 서브시스템 초기화
        lift = new LiftSubsystem(hardwareMap, "lift");

        // 2. GamepadEx 사용
        driverGamepad = new GamepadEx(gamepad1);

        // 3. 버튼 동작 바인딩
        driverGamepad.getGamepadButton(GamepadKeys.Button.X)
                .whenHeld(new LiftCommand(lift));

        register(lift);
    }

    @Override
    public void run() {
        super.run();
        telemetry.addData("Servo Position", lift.getPosition());
        telemetry.update();
    }
}