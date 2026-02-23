package org.firstinspires.ftc.teamcode.commands.groups;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.arcrobotics.ftclib.command.InstantCommand;
import com.arcrobotics.ftclib.command.ParallelDeadlineGroup;
import com.arcrobotics.ftclib.command.SequentialCommandGroup;
import com.arcrobotics.ftclib.command.WaitCommand;

import org.firstinspires.ftc.teamcode.Utils.DistanceShooterVel;
import org.firstinspires.ftc.teamcode.commands.mech.IntakeCommand;
import org.firstinspires.ftc.teamcode.commands.mech.LiftCommand;
import org.firstinspires.ftc.teamcode.commands.mech.TurretCommand;
import org.firstinspires.ftc.teamcode.commands.mech.WaitForTagCommand;
import org.firstinspires.ftc.teamcode.subsystems.FlywheelSubsystem;
import org.firstinspires.ftc.teamcode.subsystems.IntakeSubsystem;
import org.firstinspires.ftc.teamcode.subsystems.LiftSubsystem;
import org.firstinspires.ftc.teamcode.subsystems.TurretSubsystem;
import org.firstinspires.ftc.teamcode.subsystems.VisionSubsystem;

public class AutoShootSequenceCommand extends SequentialCommandGroup {

    private DistanceShooterVel distanceShooterVel;
    private TelemetryPacket packet;

    /**
     * 1. 터렛 초기 정렬 (1회)
     * 2. 오토 트랙킹 & 슈터 가동 (유지)
     * 3. 리프트만 3회 반복 동작 (연사)
     */
    public AutoShootSequenceCommand(TurretSubsystem turret, FlywheelSubsystem flywheel,
                                    LiftSubsystem lift, VisionSubsystem vision, IntakeSubsystem intakeBack, IntakeSubsystem intakeFront, int targetTagID) {

        distanceShooterVel = new DistanceShooterVel();
        packet = new TelemetryPacket();

        // 리프트 반복 동작을 담을 시퀀스 그룹 생성
        SequentialCommandGroup fireSequence = new SequentialCommandGroup();
        for (int i = 0; i < 3; i++) {
            packet.put("Cycle", i+1);
            FtcDashboard.getInstance().sendTelemetryPacket(packet);
            fireSequence.addCommands(
                    // 리프트 발사 (0.8초 동안 올라갔다가 자동으로 내려옴)
                    new LiftCommand(lift).withTimeout(800),
                    // 다음 발사 간격 (3초 대기)
                    new WaitCommand(4000)
            );
        }

        addCommands(
                // 태그 감지 될 때 까지 존버
                new InstantCommand(() -> new WaitForTagCommand(vision, targetTagID)),
                // 2. 오토 트랙킹을 켠 상태에서(Parallel), 슈팅 및 리프트 연사 진행(Deadline)
                new ParallelDeadlineGroup(
                        // [Deadline] 메인 시퀀스 (이게 끝나면 트랙킹도 같이 꺼짐)
                        new SequentialCommandGroup(
                                // (A) 트랙킹 안정화 대기 (0.3초)
                                new WaitCommand(2000),
                                // (B) 슈터 가동 (처음에 한 번만 거리 계산 후 계속 회전 유지)
                                new InstantCommand(() -> {
                                    double distance = vision.getDistance(targetTagID);
                                    if (distance == -1.0) {
                                        return;
                                    }

//                                    double targetRPM = flywheel.calculateShootingVelocity(distance);
                                    double targetRPM = distanceShooterVel.get(distance);


                                    if (targetRPM > 0) {
                                        flywheel.shoot(targetRPM);
                                    } else {
                                        flywheel.shoot(3471); // 기본값
                                    }
                                }),

                                // (C) RPM 도달 대기 (0.5초)
                                new WaitCommand(1000),

                                // (D) 위에서 만든 3회 발사 시퀀스 실행
                                fireSequence,

                                // (E) 모든 발사 종료 후 슈터 정지
                                new InstantCommand(flywheel::stop)
                        ),

                        // [Background] 터렛 오토 트랙킹
                        // Deadline(메인 시퀀스)이 진행되는 내내 계속 태그를 조준합니다.
                        new TurretTrackingTagCommand(turret, vision, targetTagID),
                        new IntakeCommand(intakeBack, 500),
                        new IntakeCommand(intakeFront, 500)
                )
        );
    }
}