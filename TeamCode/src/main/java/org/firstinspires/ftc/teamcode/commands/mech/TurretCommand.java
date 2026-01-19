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
        if ((turret.getAngle() - targetAngle) < TurretSubsystem.TOLERANCE ) {
            return true;
        }

        return false;
    }
}
