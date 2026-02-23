package org.firstinspires.ftc.teamcode.opmodes.tests;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.qualcomm.robotcore.eventloop.opmode.*;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;

//@Disabled
@Config
@TeleOp(name = "ShooterTest", group = "Test")
public class ShooterTest extends LinearOpMode {

    public static PIDFCoefficients SHOOTER_PID = new PIDFCoefficients(100, 0.3, 0.5, 11);

    public static int TPS = 1000;
    private enum State {
        ACTIVE,     // 회전 중
        UNACTIVE    // !회전 중
    }
    private State currentState = State.UNACTIVE;

    private FtcDashboard dashboard = FtcDashboard.getInstance();
    private TelemetryPacket packet = new TelemetryPacket();

    private DcMotorEx flyWheel1;
    private DcMotorEx flyWheel2;

    @Override
    public void runOpMode() {
        flyWheel2 = hardwareMap.get(DcMotorEx.class, "shooterLeft");
        flyWheel1 = hardwareMap.get(DcMotorEx.class, "shooterRight");

        waitForStart();
        if (opModeIsActive()) {
            mtrInit();
            mtrLoad();
            while (opModeIsActive()) {

                if (gamepad1.xWasPressed()) {
                    switch (currentState) {
                        case ACTIVE:
                            currentState = State.UNACTIVE;
                            mtrSetPower(0);

                            break;

                        case UNACTIVE:
                            currentState = State.ACTIVE;
                            mtrSetVelocity(TPS);
                            break;
                    }
                } else if (gamepad1.y) {
                    mtrLoad();
                }
                showData();
            }
        }
    }

    private void mtrSetVelocity(int TPS) {
        flyWheel1.setVelocity(TPS);
        flyWheel2.setVelocity(TPS);
    }

    private void mtrSetPower(double power) {
        flyWheel1.setPower(power);
        flyWheel2.setPower(power);
    }

    private void mtrInit() {
        flyWheel1.setDirection(DcMotorEx.Direction.FORWARD);
        flyWheel2.setDirection(DcMotorEx.Direction.REVERSE);
        flyWheel1.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        flyWheel2.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        flyWheel1.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        flyWheel2.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        flyWheel1.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        flyWheel2.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
    }

    private void mtrLoad() {
        dashboard.updateConfig();
        flyWheel1.setVelocityPIDFCoefficients(SHOOTER_PID.p, SHOOTER_PID.i, SHOOTER_PID.d, SHOOTER_PID.f);
        flyWheel2.setVelocityPIDFCoefficients(SHOOTER_PID.p, SHOOTER_PID.i, SHOOTER_PID.d, SHOOTER_PID.f);
    }

    private void showData() {
        packet.put("TARGET", TPS);
        packet.put("leftVelocity", flyWheel1.getVelocity());
        packet.put("rightVelocity", flyWheel2.getVelocity());
        packet.put("leftEncoder", flyWheel1.getCurrentPosition());
        packet.put("rightEncoder", flyWheel2.getCurrentPosition());

        dashboard.sendTelemetryPacket(packet);
    }


}
