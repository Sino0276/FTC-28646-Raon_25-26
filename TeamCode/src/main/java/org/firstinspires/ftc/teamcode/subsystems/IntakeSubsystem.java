package org.firstinspires.ftc.teamcode.subsystems;

import com.acmerobotics.dashboard.config.Config;
import com.arcrobotics.ftclib.command.SubsystemBase;
import com.arcrobotics.ftclib.hardware.motors.Motor;
import com.arcrobotics.ftclib.hardware.motors.MotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;

@Config
public class IntakeSubsystem extends SubsystemBase {
    // p, i, d, s, v, a
    public static double kp, ki, kd, ks, kv = 1.5, ka;

    private final double ticksPerRadian;
    private final MotorEx intakeMotor;

    public static class Builder {
        private final HardwareMap hardwareMap;
        private final String motorName;

        private Motor.GoBILDA motorType;
        private double gearRatio;
        private boolean reverse;
        private Motor.ZeroPowerBehavior zeroPowerBehavior;


        public Builder(HardwareMap hardwareMap, String motorName) {
            this.hardwareMap = hardwareMap;
            this.motorName = motorName;
        }

        public Builder motor(Motor.GoBILDA motorType){
            this.motorType = motorType;
            return this;
        }

        public Builder gearRatio(double gearRatio){
            this.gearRatio = gearRatio;
            return this;
        }

        public Builder reverse(boolean reverse){
            this.reverse = reverse;
            return this;
        }

        public Builder ZeroPowerBehavior(Motor.ZeroPowerBehavior zeroPowerBehavior) {
            this.zeroPowerBehavior = zeroPowerBehavior;
            return this;
        }

        public IntakeSubsystem build() {
            return new IntakeSubsystem(this);
        }
    }

    private IntakeSubsystem(Builder builder) {
        // 인테이크 모터 초기화
        intakeMotor = new MotorEx(builder.hardwareMap, builder.motorName, builder.motorType);

        // 모터 반전 (모터의 회전 방향이 반대라면 수정)
        intakeMotor.setInverted(builder.reverse);

        // 모터 모드
        intakeMotor.setRunMode(Motor.RunMode.VelocityControl);

        // ZeroPowerBehavior
        intakeMotor.setZeroPowerBehavior(builder.zeroPowerBehavior);

        // 1rad당 필요한 틱 수
        ticksPerRadian = (intakeMotor.getCPR() * builder.gearRatio) / (2 * Math.PI);

        // PIDF 계수 설정
        updateCoefficients();
    }

    public void updateCoefficients() {
        intakeMotor.setVeloCoefficients(kp, ki, kd);
        intakeMotor.setFeedforwardCoefficients(ks, kv, ka);
    }

    private double rpmToTps(double rpm) {
        return (rpm * intakeMotor.getCPR()) / 60.0;
    }

    public void spin(double rpm) {
        intakeMotor.setVelocity(rpmToTps(rpm));
    }

    public void stop() {
        spin(0);
        intakeMotor.stopMotor();
    }

}
