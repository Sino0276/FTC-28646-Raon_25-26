package org.firstinspires.ftc.teamcode.subsystems;

import android.util.Size;

import com.acmerobotics.dashboard.FtcDashboard;
import com.arcrobotics.ftclib.command.SubsystemBase;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;

import java.util.ArrayList;
import java.util.List;

/**
 * DistanceUnit: Inch
 * AngleUnit: Radian
 */
public class VisionSubsystem extends SubsystemBase {

    // --- 튜닝용 상수 (Dashboard) ---
    public static boolean ENABLE_LIVE_VIEW = true; // 경기 중에는 false로 꺼서 CPU 절약 권장
    public static boolean STREAM_CAMERA = true; // 대시보드 스트리밍 여부

    // --- 하드웨어 객체 ---
    private final AprilTagProcessor aprilTag;
    private final VisionPortal visionPortal;

    // --- 데이터 저장소 ---
    private List<AprilTagDetection> currentDetections = new ArrayList<>();

    public VisionSubsystem(HardwareMap hardwareMap, String webcamName) {
        // 1. AprilTag 프로세서 빌드
        aprilTag = new AprilTagProcessor.Builder()
                .setOutputUnits(DistanceUnit.INCH, AngleUnit.RADIANS)
                // ftc-docs.firstinspires.org/en/latest/programming_resources/vision/camera_calibration/camera-calibration.html
                // .setLensIntrinsics(...) // 필요 시 카메라 캘리브레이션 값 입력 (중요)
                // fx, fy, cx, cy
                .build();

        // 2. VisionPortal 빌드 (더 효율적인 MJPEG 포맷 사용 권장)
        VisionPortal.Builder builder = new VisionPortal.Builder()
                .setCamera(hardwareMap.get(WebcamName.class, webcamName))
                .setCameraResolution(new Size(640, 480)) // 해상도가 높으면 FPS가 떨어짐
                .enableLiveView(ENABLE_LIVE_VIEW)
                .setStreamFormat(VisionPortal.StreamFormat.MJPEG)
                .addProcessor(aprilTag);

        visionPortal = builder.build();

        // 3. FtcDashboard 스트리밍 시작 (선택 사항)
        if (STREAM_CAMERA) {
            FtcDashboard.getInstance().startCameraStream(visionPortal, 30);
        }
    }

    /**
     * 매 루프마다 스케줄러에 의해 자동 호출.
     * 감지된 태그 정보를 최신 상태로 업데이트.
     */
    @Override
    public void periodic() {
        // 현재 화면에 보이는 모든 태그 정보를 가져와 리스트에 저장
        currentDetections = aprilTag.getDetections();
    }

    /**
     * 특정 ID의 태그 정보를 반환. (없으면 null)
     */
    public AprilTagDetection getDetection(int id) {
        for (AprilTagDetection detection : currentDetections) {
            if (detection.id == id) {
                return detection;
            }
        }
        return null;
    }

    /**
     * 특정 ID의 AprilTag가 현재 감지되고 있는지 확인.
     */
    public boolean isTagVisible(int id) {
        return getDetection(id) != null;
    }

    /**
     * [Flywheel용] 태그까지의 거리를 반환. (단위: inch)
     * 감지되지 않으면 -1을 반환.
     */
    public double getDistance(int id) {
        AprilTagDetection detection = getDetection(id);
        if (detection != null) {
            // ftcPose.y : 카메라 정면 방향 거리 (평면 거리)
            // ftcPose.range : 3차원 직선 거리 (대각선)

            return detection.ftcPose.y;
        }
        return -1.0;
    }

    /**
     * [Turret용] 태그가 카메라 중심에서 얼마나 틀어져 있는지 각도를 반환. (단위: Rad)
     * 감지되지 않으면 0을 반환.
     * 왼쪽이 (+), 오른쪽이 (-) 인지 확인 후 사용해야 합. (보통 bearing은 반시계(CCW)가 +)
     * 삼각함수 배울때 사용하는 단위원을 떠올리면 이해가 쉽다~
     */
    public double getAngle(int id) {
        AprilTagDetection detection = getDetection(id);
        if (detection != null) {
            // bearing: 카메라 중앙 기준 태그의 수평 각도
            return detection.ftcPose.bearing;
        }
        return 0.0;
    }

    /**
     * 카메라 스트리밍을 중지하고 자원을 해제.
     * (OpMode 종료 시 호출)
     */
    public void close() {
        if (visionPortal.getCameraState() == VisionPortal.CameraState.STREAMING) {
            visionPortal.stopStreaming();
        }
        visionPortal.close();
    }
}
