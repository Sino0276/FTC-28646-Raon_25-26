package org.firstinspires.ftc.teamcode.Utils;

import com.arcrobotics.ftclib.util.InterpLUT;
import com.arcrobotics.ftclib.util.LUT;
import com.pedropathing.geometry.Pose;

public class DistanceShooterVel {
    public static InterpLUT SHOOTER_VEL = new InterpLUT();

    public DistanceShooterVel() {
        SHOOTER_VEL = new InterpLUT();
        SHOOTER_VEL.add(0, 3380);
        SHOOTER_VEL.add(50, 3380);
        SHOOTER_VEL.add(54.9, 3380);
        SHOOTER_VEL.add(60, 3400);
        SHOOTER_VEL.add(66.7, 3385);
        SHOOTER_VEL.add(76, 3428);
        SHOOTER_VEL.add(81, 3390);
        SHOOTER_VEL.add(97, 3400);
        SHOOTER_VEL.add(116, 3471);
        SHOOTER_VEL.add(141, 3622);
        SHOOTER_VEL.add(204, 3622);
        SHOOTER_VEL.createLUT();
    }

    public double get(double distance) {
        return SHOOTER_VEL.get(distance);
    }

}
