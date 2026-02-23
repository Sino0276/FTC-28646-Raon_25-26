package org.firstinspires.ftc.teamcode.opmodes.auto;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.arcrobotics.ftclib.command.CommandOpMode;
import com.arcrobotics.ftclib.command.CommandScheduler;
import com.arcrobotics.ftclib.command.InstantCommand;
import com.arcrobotics.ftclib.command.SequentialCommandGroup;
import com.arcrobotics.ftclib.hardware.motors.Motor;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.teamcode.commands.groups.AutoShootSequenceCommand;
import org.firstinspires.ftc.teamcode.commands.mech.TurretCommand;
import org.firstinspires.ftc.teamcode.subsystems.FlywheelSubsystem;
import org.firstinspires.ftc.teamcode.subsystems.IntakeSubsystem;
import org.firstinspires.ftc.teamcode.subsystems.LiftSubsystem;
import org.firstinspires.ftc.teamcode.subsystems.TurretSubsystem;
import org.firstinspires.ftc.teamcode.subsystems.VisionSubsystem;

@Autonomous(name = "BlueBack", group = "Autonomous")
public class BlueBack extends CommandOpMode {

    private FlywheelSubsystem flywheel;
    private TurretSubsystem turret;
    private VisionSubsystem vision;
    private IntakeSubsystem intakeFront, intakeBack;
    private LiftSubsystem lift;

    private int TARGET_TAG_ID = 20;

    @Override
    public void initialize() {
        // 스케줄러 리셋 (이전 OpMode의 잔여 커맨드 제거)
        CommandScheduler.getInstance().reset();

        // 0. 텔레메트리 설정
        telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());

        // 1. 하드웨어 & 서브시스템 초기화
//        flywheel = new FlywheelSubsystem(hardwareMap, "flywheel", Motor.GoBILDA.BARE, 1, 0.304, 1.8897637795275593, 25.19685039370079, Math.toRadians(43.2));
        flywheel = new FlywheelSubsystem(hardwareMap, "flywheel", Motor.GoBILDA.BARE, 1, 0.4, 1.8897637795275593, 25.19685039370079, 52);
        turret = new TurretSubsystem(hardwareMap, "turret", Motor.GoBILDA.RPM_312, (double) 80 / 10);
        vision = new VisionSubsystem(hardwareMap, "Webcam 1");
        intakeFront = new IntakeSubsystem.Builder(hardwareMap, "intakeFront")
                .motor(Motor.GoBILDA.RPM_1150)
                .gearRatio(1)
                .reverse(false)
                .ZeroPowerBehavior(Motor.ZeroPowerBehavior.BRAKE)
                .build();

        intakeBack = new IntakeSubsystem.Builder(hardwareMap, "intakeBack")
                .motor(Motor.GoBILDA.RPM_1150)
                .gearRatio(1)
                .reverse(true)
                .ZeroPowerBehavior(Motor.ZeroPowerBehavior.BRAKE)
                .build();

        lift = new LiftSubsystem(hardwareMap, "lift");

        // 서브시스템 등록 (Default Command 실행 및 주기적 업데이트 보장)
        register(turret, flywheel, intakeFront, intakeBack, lift, vision);

        /// //////////////////////////////////////////

        schedule(new AutoShootSequenceCommand(turret, flywheel, lift, vision, intakeBack, intakeFront, TARGET_TAG_ID));
    }


}
