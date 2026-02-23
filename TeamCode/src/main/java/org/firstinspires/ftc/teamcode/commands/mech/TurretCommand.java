package org.firstinspires.ftc.teamcode.commands.mech;

import com.arcrobotics.ftclib.command.CommandBase;

import org.firstinspires.ftc.teamcode.subsystems.TurretSubsystem;

public class TurretCommand extends CommandBase {

    private final TurretSubsystem turret;
    private final double targetAngle;

    public TurretCommand(TurretSubsystem subsystem, double radian) {
        this.turret = subsystem;
        this.targetAngle = radian;

        addRequirements(subsystem);
    }

    @Override
    public void initialize() {
        turret.turnToAngle(targetAngle);
    }

    @Override
    public void execute() {
        turret.updateCoefficients();
    }

    @Override
    public void end(boolean interrupted) {
        turret.stop();
    }

    @Override
    public boolean isFinished() {
        // [수정 핵심] Math.abs() 추가
        // 현재 각도와 목표 각도의 '차이(절댓값)'가 허용 오차보다 작으면 종료
        double error = Math.abs(turret.getAngle() - targetAngle);

        // 1. 목표에 도달했거나
        if (error < TurretSubsystem.TOLERANCE) {
            return true;
        }

        return false;
    }
}
