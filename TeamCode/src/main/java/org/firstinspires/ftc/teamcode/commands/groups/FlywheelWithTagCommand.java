package org.firstinspires.ftc.teamcode.commands.groups;

import static org.firstinspires.ftc.teamcode.Utils.AprilTagPosition.APRILTAG_POS;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.arcrobotics.ftclib.command.CommandBase;
import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.Pose;

import org.firstinspires.ftc.teamcode.subsystems.FlywheelSubsystem;
import org.firstinspires.ftc.teamcode.subsystems.VisionSubsystem;

public class FlywheelWithTagCommand extends CommandBase {
    private final FlywheelSubsystem flywheel;
    private final VisionSubsystem vision;
    private final int tagID;

    private TelemetryPacket packet;


    /**
     * @param flywheel turretSubsystem
     * @param vision cameraSubsystem
     * @param tagID AprilTag ID
     */
    public FlywheelWithTagCommand(FlywheelSubsystem flywheel, VisionSubsystem vision, int tagID) {
        this.flywheel = flywheel;
        this.vision = vision;
        this.tagID = tagID;

        packet = new TelemetryPacket();

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

            // 2. 서브시스템에 거리(inch)를 주고 최적의 RPM 계산
            double targetRPM = flywheel.calculateShootingVelocity(distance);

            packet.put("Shooter_Current", flywheel.getCurrentRPM());
            packet.put("Shooter_Target", targetRPM);
            FtcDashboard.getInstance().sendTelemetryPacket(packet);

            // 모터 구동
            flywheel.shoot(targetRPM);
        }
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
