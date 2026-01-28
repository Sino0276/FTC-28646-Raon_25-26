package org.firstinspires.ftc.teamcode.opmodes.TeleOp;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.arcrobotics.ftclib.command.CommandOpMode;
import com.arcrobotics.ftclib.command.CommandScheduler;
import com.arcrobotics.ftclib.gamepad.GamepadEx;
import com.arcrobotics.ftclib.gamepad.GamepadKeys;
import com.arcrobotics.ftclib.hardware.motors.Motor;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorEx;

import org.firstinspires.ftc.teamcode.commands.groups.TurretTrackingTagCommand;
import org.firstinspires.ftc.teamcode.commands.mech.FlywheelCommand;
import org.firstinspires.ftc.teamcode.commands.mech.IntakeCommand;
import org.firstinspires.ftc.teamcode.commands.mech.LiftCommand;
import org.firstinspires.ftc.teamcode.commands.mech.TurretJoystickCommand;
import org.firstinspires.ftc.teamcode.commands.mech.WaitForTagCommand;
import org.firstinspires.ftc.teamcode.subsystems.FlywheelSubsystem;
import org.firstinspires.ftc.teamcode.subsystems.IntakeSubsystem;
import org.firstinspires.ftc.teamcode.subsystems.LiftSubsystem;
import org.firstinspires.ftc.teamcode.subsystems.TurretSubsystem;
import org.firstinspires.ftc.teamcode.subsystems.VisionSubsystem;

@TeleOp(name = "DriveTest", group = "TeleOp")
public class DriveTest extends CommandOpMode {
    private FlywheelSubsystem flywheel;
    private TurretSubsystem turret;
    private VisionSubsystem vision;
    private IntakeSubsystem intakeFront, intakeBack;
    private LiftSubsystem lift;
    // private Follower follower;

    // 게임패드 선언
    private GamepadEx player1, player2;

    private DcMotorEx mtr_rr, mtr_rf, mtr_lr, mtr_lf;

    // 타겟 정보 (예: Red Alliance 골대)
    private final int TARGET_TAG_ID = 20;

    @Override
    public void initialize() {
        // 스케줄러 리셋 (이전 OpMode의 잔여 커맨드 제거)
        CommandScheduler.getInstance().reset();

        // 0. 텔레메트리 설정
        telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());

        // 1. 하드웨어 & 서브시스템 초기화
        flywheel = new FlywheelSubsystem(hardwareMap, "flywheel", Motor.GoBILDA.BARE, 1, 0.58, 48, 984.5 - 0, Math.toRadians(50));
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

        // 2. 게임패드 초기화
        player1 = new GamepadEx(gamepad1);
        player2 = new GamepadEx(gamepad2);

        // 3. 버튼 바인딩
        // 터렛 조이스틱 제어 (Default Command)
        turret.setDefaultCommand(new TurretJoystickCommand(turret, () -> player2.getLeftX()));
//        player2.getGamepadButton(GamepadKeys.Button.A)
//                        .whileHeld(new TurretTrackingTagCommand(turret, vision, ));

        // 슈터 키바인딩
        player2.getGamepadButton(GamepadKeys.Button.B)
                .toggleWhenPressed(new FlywheelCommand(flywheel, 5500));

        // 인테이크 키바인딩
        player2.getGamepadButton(GamepadKeys.Button.DPAD_UP)
                .toggleWhenPressed(new IntakeCommand(intakeFront, 500));
        player2.getGamepadButton(GamepadKeys.Button.DPAD_DOWN)
                .toggleWhenPressed(new IntakeCommand(intakeBack, 500));

        // 리프트 키바인딩
        player2.getGamepadButton(GamepadKeys.Button.X)
                .whenHeld(new LiftCommand(lift));

        // 구동 모터 초기화
        mtr_rr = hardwareMap.get(DcMotorEx.class, "mtr_rr");
        mtr_rf = hardwareMap.get(DcMotorEx.class, "mtr_rf");
        mtr_lr = hardwareMap.get(DcMotorEx.class, "mtr_lr");
        mtr_lf = hardwareMap.get(DcMotorEx.class, "mtr_lf");
        mtr_lr.setDirection(DcMotorEx.Direction.REVERSE);
        mtr_lf.setDirection(DcMotorEx.Direction.REVERSE);
    }

    @Override
    public void run() {
        if (isStopRequested()) {
            exit();
            return;
        }

        // [중요] GamepadEx 상태 업데이트
        // 이 코드가 없으면 toggleWhenPressed 등의 버튼 이벤트가 발생하지 않습니다.
        player2.readButtons();
        // player1.readButtons(); // player1도 버튼 바인딩을 쓴다면 주석 해제

        // FTCLib의 스케줄러 실행 (서브시스템 periodic 및 커맨드 실행)
        super.run();

        // 수동 주행 제어 (gamepad1 직접 사용)
        double x = gamepad1.left_stick_x;
        double y = -gamepad1.left_stick_y;
        double rx = gamepad1.right_stick_x;

        mtr_lf.setPower(0.6 * (y + x + rx));
        mtr_rf.setPower(0.6 * (y - x - rx));
        mtr_lr.setPower(0.6 * (y - x + rx));
        mtr_rr.setPower(0.6 * (y + x - rx));

        // 상태 모니터링
        showTelemetry();
    }

    private void showTelemetry() {
        telemetry.addData("Turret Angle", turret.getAngle());
        telemetry.addData("P2 LeftX", player2.getLeftX());
        telemetry.update();
    }

    private void exit() {
        turret.center();
        CommandScheduler.getInstance().cancelAll();
    }
}