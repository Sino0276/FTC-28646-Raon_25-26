package org.firstinspires.ftc.teamcode.Utils;

import com.arcrobotics.ftclib.util.LUT;
import com.pedropathing.geometry.Pose;

public class AprilTagPosition {
    /// Pedro Pathing 좌표계 기준으로 작성 (단위: inch)
    public static LUT<Integer, Pose> APRILTAG_POS = new LUT<Integer, Pose>()
    {{
        add(20, new Pose(11, 137.089, 0));          // BLUE
        add(24, new Pose(131.730, 137.089, 0));     // RED
    }};

}
