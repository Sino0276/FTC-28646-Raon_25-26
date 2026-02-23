package org.firstinspires.ftc.teamcode.commands.mech;

import com.acmerobotics.dashboard.config.Config;
import com.arcrobotics.ftclib.command.CommandBase;

import org.firstinspires.ftc.teamcode.subsystems.TurretSubsystem;

import java.util.function.DoubleSupplier;

@Config
public class TurretJoystickCommand extends CommandBase {

    public static double sensitivity; // 회전 속도 민감도
    private final TurretSubsystem turret;
    private final DoubleSupplier joystickInput;

    /**
     * @param turret 터렛 서브시스템
     * @param joystickInput 조이스틱의 X값을 받아올 함수 (예: () -> gamepad.getLeftX())
     */
    public TurretJoystickCommand(TurretSubsystem turret, DoubleSupplier joystickInput) {
        this.turret = turret;
        this.joystickInput = joystickInput;
        // 민감도 조절: 루프당 회전할 라디안 값 (이 값을 키우면 더 빨리 돕니다)
        this.sensitivity = 0.1;

        // 이 명령이 터렛을 제어함을 명시
        addRequirements(turret);
    }

    @Override
    public void execute() {
        // 1. 조이스틱 값 읽기
        double stickValue = joystickInput.getAsDouble();

        // 3. 방향 보정 및 회전량 계산
        // 일반적으로 Gamepad Y축은 위가 -1, 아래가 +1입니다.
        // 상황에 따라 부호를 반대로(-) 바꿔야 원흐는 방향으로 돌 수 있습니다.
        double deltaAngle = stickValue * sensitivity;

        // 4. 터렛 회전 (현재 각도에서 deltaAngle 만큼 더하기)
        turret.turnAsAngle(deltaAngle);

        // 5. (선택사항) PID 계수가 튜닝 중이라면 실시간 업데이트
        turret.updateCoefficients();
    }
}
