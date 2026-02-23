package org.firstinspires.ftc.teamcode.subsystems;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.arcrobotics.ftclib.command.SubsystemBase;
import com.arcrobotics.ftclib.hardware.motors.Motor;
import com.arcrobotics.ftclib.hardware.motors.MotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.Range;

@Config
public class TurretSubsystem extends SubsystemBase {

    public static final double MAX_ANGLE = 0.85, MIN_ANGLE = -0.85;

    private MotorEx turretMotor;
    private final double ticksPerRadian; // 1rad 당 필요한 틱 수 (자동 계산됨)
    private double power = 0.5;

    // PIDF
    public static double
            kP = 0.02,
            kS = 0,
            kV = 0;

    public static double TOLERANCE = 100;



    /**
     * @param hardwareMap 하드웨어 맵
     * @param motorName   설정된 모터 이름
     * @param motorType   사용하는 모터의 종류 (예: Motor.GoBILDA.RPM_312)
     * @param gearRatio   외부 기어비 (예: 5.0 = 5:1 감속)
     */
    public TurretSubsystem(HardwareMap hardwareMap, String motorName, Motor.GoBILDA motorType, double gearRatio) {
        // 모터 초기화
        turretMotor = new MotorEx(hardwareMap, motorName, motorType);

        // 모터 반전 (모터의 회전 방향이 반대라면 수정)
        turretMotor.setInverted(false);

        // 모터 모드
        turretMotor.setRunMode(Motor.RunMode.PositionControl);

        // PIDF 계수 설정
        updateCoefficients();

        // ZeroPowerBehavior
        turretMotor.setZeroPowerBehavior(Motor.ZeroPowerBehavior.BRAKE);

        // 3. 기어비를 이용한 틱 변환 상수 계산
        // 공식: (모터 CPR * 외부 기어비) / (2pi)
        this.ticksPerRadian = (turretMotor.getCPR() * gearRatio) / (2 * Math.PI);
    }

    public void updateCoefficients() {
        turretMotor.setPositionCoefficient(kP);
        turretMotor.setFeedforwardCoefficients(kS, kV);
    }

    /**
     * @param power Range: -1.0 ~ 1.0
     */
    private void setSpeed(double power) {
        this.power = Range.clip(power, -1, 1);
    }

    /**
     * 터렛을 특정 각도로 회전시킵니다.
     * @param radian 목표 각도 (라디안 단위)
     */
    public void turnToAngle(double radian) {
        // 각도 정규화 (-pi/2 ~ pi/2)
//        double targetAngle = Range.clip(normalizeAngle(radian), -Math.PI / 2, Math.PI / 2);
        // 각도 정규화 (-1 ~ 1)
        double targetAngle = Range.clip(normalizeAngle(radian), MIN_ANGLE, MAX_ANGLE);
        int targetTicks = (int) (targetAngle * ticksPerRadian);

        // 위치 제어로 변경 후 이동 (예시)
        turretMotor.setRunMode(Motor.RunMode.PositionControl);
        turretMotor.setTargetPosition(targetTicks);
        turretMotor.set(power);

    }

    /**
     * 현재 위치를 기준으로 특정 각도만큼 더 회전시킵니다. (상대 좌표)
     * @param radian 더 회전할 각도 (라디안 단위)
     */
    public void turnAsAngle(double radian) {
        double currentAngle = getAngle();
        double newTargetAngle = currentAngle + radian;

        // 절대 좌표 이동 함수 호출 (안전 범위 제한 등 재사용)
        turnToAngle(newTargetAngle);
    }

    /**
     * 입력된 각도를 -pi ~ pi 범위로 정규화합니다.
     */
    private double normalizeAngle(double radian) {
        while (radian > Math.PI) radian -= 2 * Math.PI;
        while (radian <= -Math.PI) radian += 2 * Math.PI;
        return radian;
    }

    /**
     * 현재 터렛의 각도를 반환합니다.
     * @return 각도 (라디안 단위)
     */
    public double getAngle() {
        return turretMotor.getCurrentPosition() / ticksPerRadian;
    }

    /**
     * 터렛을 정면(0도)으로 정렬합니다.
     */
    public void center() {
        turnToAngle(0);
    }

    public void stop() {
        turretMotor.stopMotor();
    }
}
