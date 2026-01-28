package org.firstinspires.ftc.teamcode.commands.mech;

import com.arcrobotics.ftclib.command.CommandBase;

import org.firstinspires.ftc.teamcode.subsystems.LiftSubsystem;

public class LiftCommand extends CommandBase {

    private LiftSubsystem lift;

    public LiftCommand(LiftSubsystem lift) {
        this.lift = lift;
    }
    public LiftCommand(LiftSubsystem lift, double max, double min) {
        this.lift = lift;
        LiftSubsystem.MAX = max;
        LiftSubsystem.MIN = min;
    }

    @Override
    public void initialize() {
        super.initialize();
        lift.setPosition(LiftSubsystem.MAX);
    }

    @Override
    public void execute() {
        super.execute();
        lift.setPosition(LiftSubsystem.MAX);
    }

    @Override
    public void end(boolean interrupted) {
        super.end(interrupted);
        lift.setPosition(LiftSubsystem.MIN);
    }
}