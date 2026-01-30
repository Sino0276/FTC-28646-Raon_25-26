package org.firstinspires.ftc.teamcode.subsystems;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.arcrobotics.ftclib.command.SubsystemBase;
import com.arcrobotics.ftclib.hardware.motors.Motor;
import com.arcrobotics.ftclib.hardware.motors.MotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.Range;
// 지금당장떠나면아무도다치지않는다그러지않으면너희는모두죽어탐정놀이도이젠끝이다현실로돌아가면잊지말고전해라스텔라론헌터가너희의마지막을배웅했다는것을소탕시작액션원집행목표고정즉시처단프로토콜통과초토화작전집행깨어갔군한참이나기다렸다우린전에만난적이있지난스텔라론헌터샘이다일찍이네앞에나타나사실을알려주고싶었어하지만예상보다방해물이많군열한차례시도했지만모두실패로끝났지그러는사이에나도모르게이세계와긴밀이연결되어각본의구속에서벗어날수없게됐다엘리오말대로우리는이꿈의땅에서잊을수없는수확을얻게될테지나에겐그와카프카처럼사람의마음을꿰뚫어보는통찰력도은랑과블레이드처럼뛰어난특기도없다내가잘하는것들대부분은불쌍히여길필요없는악당에게만적용되지그러니내가사용할수있는수단도단하나뿐이다네게보여주기위한거야내전부를반딧불이처럼죽음을각오하고불속에뛰어들며살거야깨어난현실에서다시만나길

@Config
public class FlywheelSubsystem extends SubsystemBase {

    private MotorEx flywheelMotor;

    // 물리적 제원 (보정)
    public static double SHOOTER_EFFICIENCY = 0.3; // 효율계수 (0.0 ~ 1.0) 1은 슬립 X // 튜닝 할 것
    public static Double FLYWHEEL_RADIUS; // 바퀴 반지름 (inch) - null 가능
    private final Double GEAR_RATIO; // 기어비 - null 가능

    // 포물선 운동 상수
    private final Double delta_h; // 수직 높이 차이 (inch) - null 가능
    public static Double theta; // 기본 발사 각도 (rad) [내부 연산용] - null 가능

    // [단위 변환됨] 중력 가속도 (inch/s^2)
    // 기존 9800 mm/s^2 / 25.4 ≈ 385.82677
    private final double g = 385.827;

    // PIDF
    public static double kP = 5,
            kI = 0,
            kD = 0,
            kS = 1,
            kV = 2.0
                    ;

    // RPM 도달 허용 오차
    public static double VELOCITY_TOLERANCE = 50.0;

    private double targetRPM = 0.0;

    /**
     * 모터 초기화 및 설정 (간단한 버전 - FlywheelCommand용)
     * 물리적 제원 없이 기본 RPM 제어만 사용하는 경우
     * 
     * @param hardwareMap
     * @param motorName   모터 이름
     * @param motorType   모터 타입
     */
    public FlywheelSubsystem(HardwareMap hardwareMap, String motorName, Motor.GoBILDA motorType) {
        this.GEAR_RATIO = null;
        this.FLYWHEEL_RADIUS = null;
        this.delta_h = null;
        this.theta = null;

        // 플라이휠 초기화
        flywheelMotor = new MotorEx(hardwareMap, motorName, motorType);

        // 모터 반전 (모터의 회전 방향이 반대라면 수정)
        flywheelMotor.setInverted(true);

        // 모터 모드
        flywheelMotor.setRunMode(Motor.RunMode.VelocityControl);

        // PIDF 계수 설정
        updateCoefficients();

        // ZeroPowerBehavior
        flywheelMotor.setZeroPowerBehavior(Motor.ZeroPowerBehavior.FLOAT); // 굳이 플라이휠에 무리를 줄 필요는 없음
    }

    /**
     * 모터 초기화 및 설정 (FlywheelWithTagCommand용)
     * 물리적 제원을 포함한 거리 기반 RPM 계산이 필요한 경우
     * 
     * @param hardwareMap
     * @param motorName         모터 이름
     * @param motorType         모터 타입
     * @param gearRatio         기어비
     * @param shooterEfficiency 효율계수 (0.0 ~ 1.0)
     * @param flyWheelRadius    바퀴 반지름 (inch)
     * @param delta_h           수직 높이 차이 (inch)
     * @param theta             기본 발사 각도 (rad)
     */
    public FlywheelSubsystem(HardwareMap hardwareMap, String motorName, Motor.GoBILDA motorType,
            double gearRatio, double shooterEfficiency, double flyWheelRadius, double delta_h, double theta) {
        this.GEAR_RATIO = gearRatio;
        FlywheelSubsystem.SHOOTER_EFFICIENCY = shooterEfficiency;
        this.FLYWHEEL_RADIUS = flyWheelRadius;
        this.delta_h = delta_h;

        // 삼각함수(Math.tan, Math.cos) 계산을 위해 라디안 필수
        this.theta = theta;

        // 플라이휠 초기화
        flywheelMotor = new MotorEx(hardwareMap, motorName, motorType);

        // 모터 반전 (모터의 회전 방향이 반대라면 수정)
        flywheelMotor.setInverted(true);

        // 모터 모드
        flywheelMotor.setRunMode(Motor.RunMode.VelocityControl);

        // PIDF 계수 설정
        updateCoefficients();

        // ZeroPowerBehavior
        flywheelMotor.setZeroPowerBehavior(Motor.ZeroPowerBehavior.FLOAT); // 굳이 플라이휠에 무리를 줄 필요는 없음
    }

    /**
     * PIDF 계수 업데이트
     */
    public void updateCoefficients() {
        flywheelMotor.setVeloCoefficients(kP, kI, kD);
        flywheelMotor.setFeedforwardCoefficients(kS, kV);
    }

    public double getCurrentRPM() {
        // TPS -> RPM
        return tpsToRpm(flywheelMotor.getVelocity());
    }

    public double getActivityEfficient(double distance) {
        return ((0.0013 * distance) + 0.2119);
    }

    private double rpmToTps(double rpm) {
        return (rpm * flywheelMotor.getCPR()) / 60.0;
    }

    private double tpsToRpm(double tps) {
        return (tps * 60.0) / flywheelMotor.getCPR();
    }

    /**
     * 플라이휠 속도 제어
     * 
     * @param rpm
     */
    public void shoot(double rpm) {
        // PIDF계수 재설정
        updateCoefficients();

        rpm = Range.clip(rpm, -flywheelMotor.getMaxRPM(), flywheelMotor.getMaxRPM());
        this.targetRPM = rpm;

        // RPM -> TPS (Ticks Per Second) 변환 공식
        // TPS = (RPM * CPR) / 60
        double targetTPS = (rpm * flywheelMotor.getCPR()) / 60.0;

        flywheelMotor.setVelocity(targetTPS);
    }

    /**
     * 플라이휠 정지
     */
    public void stop() {
        shoot(0);
        flywheelMotor.stopMotor();
    }

    /**
     * 플라이휠이 목표 RPM에 도달했는지 확인
     * 발사 가능 유무
     * 
     * @return
     */
    public boolean isReady() {
        double cpr = flywheelMotor.getCPR();
        double currentRPM = (flywheelMotor.getCorrectedVelocity() * 60) / cpr;

        return targetRPM > 0 && Math.abs(targetRPM - Math.abs(currentRPM)) <= VELOCITY_TOLERANCE;
    }

    /**
     * @param distance distanceUnit: inch
     * @return 모터 RPM
     * @throws IllegalStateException 물리적 제원이 설정되지 않은 경우
     */
    public double calculateShootingVelocity(double distance) {
        // 물리적 제원이 설정되지 않은 경우 예외 발생
        if (FLYWHEEL_RADIUS == null || GEAR_RATIO == null || delta_h == null || theta == null) {
            throw new IllegalStateException(
                    "calculateShootingVelocity()를 사용하려면 물리적 제원(gearRatio, flyWheelRadius, delta_h, theta)이 필요합니다. " +
                            "전체 파라미터를 받는 생성자를 사용하세요.");
        }

        // distance(inch), delta_h(inch) -> tan(theta)는 무차원
        // theta는 이미 생성자에서 rad로 변환됨
        double discriminant = (distance * Math.tan(theta)) - delta_h;

        if (discriminant > 0) {
            // g(inch/s^2), distance(inch) -> 결과 v0는 inch/s
            double v0 = Math.sqrt(
                    (g * distance * distance) / (2 * Math.pow(Math.cos(theta), 2) * discriminant));

            // 선속도(inch/s) -> 모터 RPM 변환
            return calculateRPMFromVelocity(v0, distance);
        } else {
            // 발사 불가능한 각도(위치)라면 모터 회전x
            return 0;
        }
    }

    /**
     * 선속도(inch/s)를 모터 RPM으로 변환
     */
    private double calculateRPMFromVelocity(double v0, double distance) {
        // 물리적 제원이 설정되지 않은 경우 예외 발생 (이미 calculateShootingVelocity에서 체크되지만 안전을 위해)
        if (FLYWHEEL_RADIUS == null || GEAR_RATIO == null) {
            throw new IllegalStateException("물리적 제원이 설정되지 않았습니다.");
        }

        // 1. 휠의 접선 속도 (inch/s)
        double wheelTangentialVelocity = v0 / SHOOTER_EFFICIENCY;
//        double wheelTangentialVelocity = v0 / getActivityEfficient(distance);

        // 2. 휠의 각속도 (rad/s) = v / r
        // v (inch/s) / r (inch) = rad/s (단위 약분됨)
        double angularVelocityRadPerSec = wheelTangentialVelocity / FLYWHEEL_RADIUS;

        // 3. 각속도(rad/s) -> RPM 변환
        // 1 rad/s = 60 / 2pi RPM
        double flywheelRPM = angularVelocityRadPerSec * (60.0 / (2 * Math.PI));

        // 4. 모터 기어비 적용 (터렛 쪽 기어비 공식과 동일)
        return flywheelRPM * GEAR_RATIO;
    }

}
