package org.firstinspires.ftc.teamcode.commands.mech;

import com.arcrobotics.ftclib.command.CommandBase;

import org.firstinspires.ftc.teamcode.subsystems.IntakeSubsystem;

public class IntakeCommand extends CommandBase {
    private final IntakeSubsystem intake;
    private final double targetRPM;

    public IntakeCommand(IntakeSubsystem subsystem, double rpm) {
        this.intake = subsystem;
        this.targetRPM = rpm;

        addRequirements(subsystem);
    }

    // 커맨드 시작 시
    @Override
    public void initialize() {
        intake.spin(targetRPM);
        
    }

    // 커맨드 실행 중
    @Override
    public void execute() {
        intake.updateCoefficients();
    }

    // 커맨드 종료 시
    @Override
    public void end(boolean interrupted) {
        intake.stop();
    }

    // 커맨드 종료 조건
    @Override
    public boolean isFinished() {
        // 종료 조건이 없음
        // 강제 종료 될때 까지 계속 반복
        return false;
    }
}
