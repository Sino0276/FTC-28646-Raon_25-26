package org.firstinspires.ftc.teamcode.commands.groups;

import static org.firstinspires.ftc.teamcode.Utils.AprilTagPosition.APRILTAG_POS;

import com.acmerobotics.dashboard.config.Config;
import com.arcrobotics.ftclib.command.CommandBase;
import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.Pose;

import org.firstinspires.ftc.teamcode.subsystems.TurretSubsystem;
import org.firstinspires.ftc.teamcode.subsystems.VisionSubsystem;

/**
 * 1. Import 확인: com.pedropathing.localization.Pose 인지 geometry.Pose 인지 확인하여 빨간 줄이 뜨면 수정해주세요. <p>
 * 2. 부호 테스트: 실제 주행 시 터렛이 반대로 돌면 + vision.getAngle을 -로, 혹은 fieldHeading - robotHeading을 + 등으로 상황에 맞춰 부호만 바꿔주시면 됩니다.
 */
@Config
public class TurretTrackingTagCommand extends CommandBase {
    public static double sensitivity = 0.3;

    private final TurretSubsystem turret;
    private final VisionSubsystem vision;
    private final int tagID;

    /**
     * @param turret turretSubsystem
     * @param vision cameraSubsystem
     * @param tagID AprilTag ID
     */

    public TurretTrackingTagCommand(TurretSubsystem turret, VisionSubsystem vision, int tagID) {
        this.turret = turret;
        this.vision = vision;
        this.tagID = tagID;

        // Vision은 읽기만 할것이기에 서브시스템에 추가(독점)하지 않음 -> 병렬 실행 가능
        addRequirements(turret);
    }

    // 커맨드 실행 중
    @Override
    public void execute() {
        // 1. Vision 기반 추적 (Closed-Loop)
        if (vision.isTagVisible(tagID)) {
            // 공식: 현재 터렛 절대 각도 + 카메라가 본 오차 각도
            /*
            B. Vision Latency 보정 부재

            파일: TurretTrackingTagCommand.java

            현상: targetAngle = turret.getAngle() + vision.getAngle(tagID)

            문제: 카메라는 과거의 정보(수십~수백 ms 전)를 줍니다. 터렛이 회전하고 있는 도중에 이 수식을 쓰면, 카메라가 찍힌 시점의 터렛 각도가 아닌 '현재' 터렛 각도에 '과거' 오차를 더하게 되어 **오실레이션(떨림)**이 발생할 수 있습니다.

            해결: 터렛이 고속으로 움직일 때는 오차가 큽니다. 터렛이 거의 멈췄을 때만 비전 보정을 하거나, poseNet 처럼 카메라 캡처 타임스탬프를 이용한 복잡한 보정이 필요합니다. (초보자라면 일단 감수하고 사용하되, P게인을 낮게 잡으세요.)
             */
            double targetAngle = turret.getAngle() + (sensitivity * vision.getAngle(tagID));   // 부호 확인
            // 최종 명령: 계산된 절대 각도로 회전
            turret.turnToAngle(targetAngle);
        } else {
            // Tag 인식X -> 행동X
            turret.stop();
        }
    }

    // 커맨드 종료 시
    @Override
    public void end(boolean interrupted) {
        turret.stop();
    }

    // 커맨드 종료 조건
    @Override
    public boolean isFinished() {
        // 종료 조건이 없음
        // 강제 종료 될때 까지 계속 반복
        return false;
    }

}
