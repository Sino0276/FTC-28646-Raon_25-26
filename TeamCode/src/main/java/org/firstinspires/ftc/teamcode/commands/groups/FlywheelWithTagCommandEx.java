package org.firstinspires.ftc.teamcode.commands.groups;

import static org.firstinspires.ftc.teamcode.Utils.AprilTagPosition.APRILTAG_POS;

import com.arcrobotics.ftclib.command.CommandBase;
import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.Pose;

import org.firstinspires.ftc.teamcode.subsystems.FlywheelSubsystem;
import org.firstinspires.ftc.teamcode.subsystems.VisionSubsystem;

/**
 * FlywheelWithTagCommand.java의 확장으로 태그가 인식되지 않은 상황에서도,
 * 사전에 입력된 aprilTag의 위치를 받아와 거리에 따른 포물선 함수를 계산하여 슈터의 속도를 능동적으로 조절합니다.
 */
public class FlywheelWithTagCommandEx extends CommandBase {
    private final FlywheelSubsystem flywheel;
    private final VisionSubsystem vision;
    private final Follower follower;
    private final int tagID;


    /**
     * @param flywheel turretSubsystem
     * @param vision cameraSubsystem
     * @param follower PedroPathing follower
     * @param tagID AprilTag ID
     */
    public FlywheelWithTagCommandEx(FlywheelSubsystem flywheel, VisionSubsystem vision, Follower follower, int tagID) {
        this.flywheel = flywheel;
        this.vision = vision;
        this.tagID = tagID;
        this.follower = follower;

        // Vision은 읽기만 할것이기에 서브시스템에 추가(독점)하지 않음 -> 병렬 실행 가능
        addRequirements(flywheel);
    }

    // 커맨드 실행 중
    @Override
    public void execute() {
        double distance = 0.0;

        // 1. Vision 기반 거리 측정 (Closed-Loop, 1순위)
        if (vision.isTagVisible(this.tagID)) {
            distance = vision.getDistance(tagID);
        }

        // 2. Odometry 기반 거리 계산 (Open-Loop, 2순위)
        else if (APRILTAG_POS.containsKey(tagID)){
            Pose tagPose = APRILTAG_POS.get(tagID);
            Pose robotPose = follower.getPose();

            // 피타고라스 정리로 수평 거리 계산 (단위: inch)
            // (높이 차이는 Subsystem 내부 물리 공식에 포함되어 있으므로 수평 거리만 구함)
            assert tagPose != null : "APRILTAG_POS에 해당 태그에 맞는 값이 없음. AprilTagPosition.java를 확인하거나 Tag ID가 정확한지 확인.";
            double deltaX = tagPose.getX() - robotPose.getX();
            double deltaY = tagPose.getY() - robotPose.getY();
            distance = Math.hypot(deltaX, deltaY);
        }

        // 3. 태그 정보가 없으면 기본 RPM으로 회전
        else {
            return;
        }

        // 3. 서브시스템에 거리(inch)를 주고 최적의 RPM 계산
        double targetRPM = flywheel.calculateShootingVelocity(distance);

        // 모터 구동
        flywheel.shoot(targetRPM);
    }

    // 커맨드 종료 시
    @Override
    public void end(boolean interrupted) {
        flywheel.stop();
    }

    // 커맨드 종료 조건
    @Override
    public boolean isFinished() {
        // 종료 조건이 없음
        // 강제 종료 될때 까지 계속 반복
        return false;
    }
}
