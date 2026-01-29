package org.firstinspires.ftc.teamcode.subsystems;

import com.acmerobotics.dashboard.config.Config;
import com.arcrobotics.ftclib.command.SubsystemBase;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

@Config
public class LiftSubsystem extends SubsystemBase {
    private final Servo servo;

    // 위치 값들을 서브시스템 내부에서 관리하면 좋습니다.
    public static double MAX = 0.6;
    public static double MIN = 0.85;

    public LiftSubsystem(HardwareMap hardwareMap, String name) {
        servo = hardwareMap.get(Servo.class, name);
        servo.setDirection(Servo.Direction.FORWARD);
    }
    public LiftSubsystem(HardwareMap hardwareMap, String name, double max, double min) {
        servo = hardwareMap.get(Servo.class, name);
        servo.setDirection(Servo.Direction.FORWARD);
    }


    public void setPosition(double position) {
        servo.setPosition(position);
    }

    public double getPosition() {
        return servo.getPosition();
    }
}