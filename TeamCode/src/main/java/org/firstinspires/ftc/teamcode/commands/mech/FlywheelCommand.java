package org.firstinspires.ftc.teamcode.commands.mech;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.arcrobotics.ftclib.command.CommandBase;

import org.firstinspires.ftc.teamcode.subsystems.FlywheelSubsystem;

public class FlywheelCommand extends CommandBase {

    private final FlywheelSubsystem flywheel;
    private final double targetRPM;
    private TelemetryPacket packet;

    /**
     *
     * @param subsystem 플라이휠 서브시스템 인스턴스
     * @param rpm       목표 RPM
     */
    public FlywheelCommand(FlywheelSubsystem subsystem, double rpm) {
        this.flywheel = subsystem;
        this.targetRPM = rpm;
        packet = new TelemetryPacket();

        addRequirements(subsystem);
    }

    // 커맨드 시작 시
    @Override
    public void initialize() {
        flywheel.shoot(targetRPM);
    }

    // 커맨드 실행 중
    @Override
    public void execute() {
        flywheel.updateCoefficients();

        packet.put("Shooter_Current", flywheel.getCurrentRPM());
        packet.put("Shooter_Target", targetRPM);
        FtcDashboard.getInstance().sendTelemetryPacket(packet);
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
