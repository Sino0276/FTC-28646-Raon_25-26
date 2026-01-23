package org.firstinspires.ftc.teamcode.opmodes.tests;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.arcrobotics.ftclib.command.CommandOpMode;
import com.arcrobotics.ftclib.command.InstantCommand;
import com.arcrobotics.ftclib.gamepad.GamepadEx;
import com.arcrobotics.ftclib.gamepad.GamepadKeys;
import com.arcrobotics.ftclib.hardware.motors.Motor;
import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.Pose;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.commands.mech.FlywheelCommand;
import org.firstinspires.ftc.teamcode.commands.mech.TurretCommand;
import org.firstinspires.ftc.teamcode.pedroPathing.Constants;
import org.firstinspires.ftc.teamcode.subsystems.FlywheelSubsystem;
import org.firstinspires.ftc.teamcode.subsystems.TurretSubsystem;
import org.firstinspires.ftc.teamcode.subsystems.VisionSubsystem;

@TeleOp(name = "ttttteeeeessssttttt", group = "Test")
public class ttttttteeeeeeeeeesssssssssttttttt extends CommandOpMode {
    private FlywheelSubsystem flywheel;
    private TurretSubsystem turret;
    private VisionSubsystem vision;
    private Follower follower;

    // 게임패드 선언
    private GamepadEx driverOp;

    // 타겟 정보 (예: Red Alliance 골대)
    private final int TARGET_TAG_ID = 20;
    private final double GOAL_X = 60.0;
    private final double GOAL_Y = -36.0;

    @Override
    public void initialize() {
        // 0. 텔레메트리 설정
        telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());

        // 1. 하드웨어 & 서브시스템 초기화
        // FlywheelCommand는 물리적 제원이 필요 없으므로 간단한 생성자 사용
        flywheel = new FlywheelSubsystem(hardwareMap, "flywheelMotor", Motor.GoBILDA.BARE);
        turret = new TurretSubsystem(hardwareMap, "turretMotor", Motor.GoBILDA.RPM_312, (double) 70 / 10);
        vision = new VisionSubsystem(hardwareMap, "Webcam 1");

        // PedroPathing Follower 초기화
        follower = Constants.createFollower(hardwareMap);
        // 중요: 자율주행에서 끝난 위치를 받아오거나 초기 위치 설정
        follower.setStartingPose(new Pose(0, 0, 0));
        follower.startTeleopDrive();

        // 2. 게임패드 초기화
        driverOp = new GamepadEx(gamepad1);

        // 3. 버튼 바인딩 (여기가 핵심!)
        driverOp.getGamepadButton(GamepadKeys.Button.X)
                .toggleWhenActive(new FlywheelCommand(flywheel, 3000));

        // 4. 기본 구동 (디폴트 커맨드)
        // PedroPathing의 주행 로직을 루프마다 실행
        register(vision); // vision은 커맨드가 없어도 periodic()을 위해 등록 필요할 수 있음 (FTCLib 버전에 따라 다름)
    }

    @Override
    public void run() {
        // FTCLib의 스케줄러 실행 (서브시스템 periodic 자동 호출)
        super.run();

        // PedroPathing 업데이트 (매 루프 필수)
        follower.update();

        // 조이스틱 주행 제어
        follower.setTeleOpDrive(
                -gamepad1.left_stick_y,
                -gamepad1.left_stick_x,
                -gamepad1.right_stick_x,
                false // Robot Centric
        );

        // 상태 모니터링
        telemetry.addData("Vision", vision.isTagVisible(TARGET_TAG_ID) ? "Visible" : "Fail");
        telemetry.addData("Distance", vision.getDistance(TARGET_TAG_ID));
        telemetry.update();
    }
}
