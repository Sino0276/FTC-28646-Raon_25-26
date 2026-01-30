package org.firstinspires.ftc.teamcode.opmodes.TeleOp;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.arcrobotics.ftclib.command.CommandOpMode;
import com.arcrobotics.ftclib.command.CommandScheduler;
import com.arcrobotics.ftclib.gamepad.GamepadEx;
import com.arcrobotics.ftclib.gamepad.GamepadKeys;
import com.arcrobotics.ftclib.hardware.motors.Motor;
import com.arcrobotics.ftclib.hardware.motors.MotorEx;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.teamcode.commands.groups.FlywheelWithTagCommand;
import org.firstinspires.ftc.teamcode.commands.groups.TurretTrackingTagCommand;
import org.firstinspires.ftc.teamcode.commands.mech.FlywheelCommand;
import org.firstinspires.ftc.teamcode.commands.mech.IntakeCommand;
import org.firstinspires.ftc.teamcode.commands.mech.LiftCommand;
import org.firstinspires.ftc.teamcode.commands.mech.TurretJoystickCommand;
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

    private MotorEx mtr_rr, mtr_rf, mtr_lr, mtr_lf;

    // 타겟 정보 (예: Red Alliance 골대)
    private int TARGET_TAG_ID = 20;

    private double POWER = 0.8,
                   BUMPER_POWER = 0.6;




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


        // red / blue 선택
        while (!isStarted()) {
            if (gamepad1.y || gamepad2.y) {
                telemetry.clearAll();
                telemetry.addData("State", "OK");
                telemetry.update();
                break;
            }
            telemetry.clearAll();
            // Blue Team
            if (gamepad1.x || gamepad2.x || TARGET_TAG_ID == 20) {
                TARGET_TAG_ID = 20;
                telemetry.addData("> BLUE", 20);
                telemetry.addData("RED", 24);
                telemetry.addData("Press Y", "To Confirm");
            }

            // Red Team
            if (gamepad1.b || gamepad2.b || TARGET_TAG_ID == 24) {
                TARGET_TAG_ID = 24;
                telemetry.addData("BLUE", 20);
                telemetry.addData("> RED", 24);
                telemetry.addData("Press Y", "To Confirm");
            }
            else {
                telemetry.addData("BLUE", 20);
                telemetry.addData("RED", 24);
            }
            telemetry.update();
        }




        // 2. 게임패드 초기화
        player1 = new GamepadEx(gamepad1);
        player2 = new GamepadEx(gamepad2);

        // 3. 버튼 바인딩
        // 터렛 조이스틱 제어 (Default Command)
        turret.setDefaultCommand(new TurretJoystickCommand(turret, () -> player2.getLeftX()));
        // 터렛 트랙킹
        player2.getGamepadButton(GamepadKeys.Button.A)
                        .toggleWhenPressed(new TurretTrackingTagCommand(turret, vision, TARGET_TAG_ID));

        // 슈터 키바인딩
//        player2.getGamepadButton(GamepadKeys.Button.B)
//                .toggleWhenPressed(new FlywheelCommand(flywheel, 3300));
        player2.getGamepadButton(GamepadKeys.Button.B)
                .toggleWhenPressed(new FlywheelWithTagCommand(flywheel, vision, TARGET_TAG_ID));

        // 인테이크 키바인딩
        player2.getGamepadButton(GamepadKeys.Button.LEFT_BUMPER)
                .whenHeld(new IntakeCommand(intakeFront, 500));
        player2.getGamepadButton(GamepadKeys.Button.RIGHT_BUMPER)
                .whileHeld(new IntakeCommand(intakeBack, 500));

        // 리프트 키바인딩
        player2.getGamepadButton(GamepadKeys.Button.X)
                .whenHeld(new LiftCommand(lift));

        // 구동 모터 초기화
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
        double rx = 0.7 * gamepad1.right_stick_x;

        if (player2.getButton(GamepadKeys.Button.RIGHT_BUMPER) || player2.getButton(GamepadKeys.Button.LEFT_BUMPER)) {
            mtr_lf.set(BUMPER_POWER * POWER * Range.clip(y + x + rx, -1, 1));
            mtr_rf.set(BUMPER_POWER * POWER * Range.clip(y - x - rx, -1, 1));
            mtr_lr.set(BUMPER_POWER * POWER * Range.clip(y - x + rx, -1, 1));
            mtr_rr.set(BUMPER_POWER * POWER * Range.clip(y + x - rx, -1, 1));
        } else {
            mtr_lf.set(POWER * Range.clip(y + x + rx, -1, 1));
            mtr_rf.set(POWER * Range.clip(y - x - rx, -1, 1));
            mtr_lr.set(POWER * Range.clip(y - x + rx, -1, 1));
            mtr_rr.set(POWER * Range.clip(y + x - rx, -1, 1));
        }

        // 상태 모니터링
        showTelemetry();
    }

    private void showTelemetry() {
        telemetry.addData("Turret Angle", turret.getAngle());
        telemetry.addData("Team", TARGET_TAG_ID == 20 ? "Blue" : "Red");
        telemetry.addData("TagID", TARGET_TAG_ID);
        telemetry.update();
    }

    private void exit() {
        turret.center();
        CommandScheduler.getInstance().cancelAll();
    }
}