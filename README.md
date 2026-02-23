# 🤖 FTC Team 28646 · 라온봇 (Raon Bot)

> **FIRST Tech Challenge** 한국 KLA & KRC 대회 출전 로봇 코드

[![FTC SDK](https://img.shields.io/badge/FTC%20SDK-11.0.0-blue?logo=android)](https://github.com/FIRST-Tech-Challenge/FtcRobotController)
[![PedroPathing](https://img.shields.io/badge/PedroPathing-2.0.5-green)](https://pedropathing.com/)
[![FTCLib](https://img.shields.io/badge/FTCLib-Command--Based-orange)](https://ftclib.org/)

---

## 📋 팀 정보

| 항목 | 내용 |
|:---|:---|
| **팀 번호** | 28646 |
| **팀 이름** | 라온봇 (Raon Bot) |
| **소속** | 라온고등학교 |
| **출전 대회** | KLA (Korea League Autonomy), KRC (Korea Robot Championship) |
| **개발 언어** | Java |
| **FTC SDK 버전** | 11.0.0 |

---

## 🏗️ 아키텍처 개요

이 프로젝트는 **FTCLib의 Command-Based 패턴**을 기반으로 설계되었습니다.  
각 하드웨어를 독립적인 서브시스템으로 분리하여 결합도를 낮추고, 커맨드를 통해 행동을 조합합니다.

```
TeamCode/
└── src/main/java/org/firstinspires/ftc/teamcode/
    ├── opmodes/
    │   ├── auto/       # 자율주행 OpMode
    │   ├── teleop/     # 텔레오프 OpMode
    │   └── tests/      # 하드웨어 테스트 OpMode
    ├── subsystems/     # 서브시스템 (하드웨어 추상화 레이어)
    ├── commands/
    │   ├── mech/       # 개별 메커니즘 커맨드
    │   └── groups/     # 복합 커맨드 시퀀스
    ├── pedroPathing/   # 경로 추종 설정 및 튜닝
    └── Utils/          # 유틸리티 클래스
```

---

## 🔧 주요 서브시스템

### 🛞 구동계 (Drivetrain)
- **형태**: 메카넘 휠 (Mecanum Wheel) 4륜 구동
- **모터**: `mtr_lf` / `mtr_lr` / `mtr_rf` / `mtr_rr` (GoBILDA)
- **경로 추종**: [PedroPathing](https://pedropathing.com/) 라이브러리 적용
- **로컬라이저**: GoBILDA Pinpoint 오도메트리 (`pinpoint`)

### 🎯 플라이휠 (Flywheel)
- **목적**: 링 발사체(링) 발사
- **제어**: PIDF 속도 제어 (Velocity Control)
- **핵심 기능**: AprilTag로 측정한 거리를 기반으로 포물선 운동 공식을 역산하여 **발사에 필요한 RPM을 자동 계산**

### 🔄 터렛 (Turret)
- **목적**: 플라이휠 발사 방향 조준
- **제어**: 엔코더 기반 위치 제어
- **핵심 기능**: AprilTag 비전으로 타겟을 실시간 추적하는 **오토 조준(Auto-Tracking)**

### 📸 비전 (Vision)
- **목적**: 골대의 AprilTag 인식 및 거리/각도 측정
- **카메라**: `Webcam 1`
- **타겟**: AprilTag ID `20` (레드 얼라이언스 골대)

### 📦 기타 서브시스템
- **Intake**: 링 수집 (전방 / 후방 모터)
- **Lift**: 링 발사 트리거 (선형 리프트)

---

## 🕹️ OpMode 목록

### 자율주행 (Autonomous)

| OpMode 이름 | 설명 |
|:---|:---|
| `BlueBack` | 블루 얼라이언스 후방 시작 위치. 오토 조준 후 3회 연사 시퀀스 실행 |
| `GoingFront` | 전방 이동 자율주행 |
| `GoingDiagonalBlue` | 블루 얼라이언스 대각선 이동 자율주행 |

**자율주행 발사 시퀀스 흐름** (`AutoShootSequenceCommand`):
1. AprilTag 탐색 및 대기
2. 터렛 오토 트랙킹 시작 (Background)
3. 비전 안정화 대기 (2초)
4. 거리 측정 → RPM 계산 → 플라이휠 가동
5. **3회 연속 발사** (리프트 동작 반복)
6. 플라이휠 정지

### 텔레오프 (TeleOp)

| OpMode 이름 | 설명 |
|:---|:---|
| `Drive` | 메인 드라이빙 OpMode |
| `DriveTest` | 드라이브 테스트용 |
| `TurretZeropointSetting` | 터렛 영점 설정 |

**드라이브 컨트롤** (`Drive`):

| 입력 | 동작 |
|:---|:---|
| `Gamepad1` 좌 스틱 | 전후/좌우 이동 (Robot Centric) |
| `Gamepad1` 우 스틱 | 회전 |
| `Gamepad2` X 버튼 | 플라이휠 + 비전 오토 조준 토글 |
| `Gamepad2` Y 버튼 | 터렛 오토 트랙킹 토글 |

---

## 📚 사용 라이브러리

| 라이브러리 | 버전 | 용도 |
|:---|:---|:---|
| [FTC SDK](https://github.com/FIRST-Tech-Challenge/FtcRobotController) | 11.0.0 | FTC 기반 프레임워크 |
| [FTCLib](https://ftclib.org/) | (내장) | Command-Based 패턴, 모터/게임패드 추상화 |
| [PedroPathing](https://pedropathing.com/) | 2.0.5 | 자율주행 경로 추종 |
| [FTC Dashboard](https://acmerobotics.github.io/ftc-dashboard/) | 0.5.1 | 실시간 텔레메트리 및 튜닝 |

---

## ⚙️ 빌드 및 배포

### 사전 요구사항
- Android Studio (최신 권장)
- FTC 로봇 컨트롤러 앱이 설치된 Android 기기

### 빌드 방법

```bash
# 프로젝트 클론
git clone https://github.com/Sino0276/FTC-28646-Raon_25-26.git

# Android Studio에서 열기 후 Gradle 동기화
# File > Open > 클론한 폴더 선택
```

### 배포
1. Android Studio에서 로봇 컨트롤러 기기 선택
2. `TeamCode` 모듈 빌드 및 배포 (`Run > Run 'TeamCode'`)

---

## 🔩 하드웨어 설정 (Hardware Map)

로봇 컨트롤러 앱의 하드웨어 설정 파일에서 아래 이름을 그대로 사용해야 합니다.

| 하드웨어 ID | 종류 | 설명 |
|:---|:---|:---|
| `mtr_lf` | DC Motor | 좌전방 구동 모터 |
| `mtr_lr` | DC Motor | 좌후방 구동 모터 |
| `mtr_rf` | DC Motor | 우전방 구동 모터 |
| `mtr_rr` | DC Motor | 우후방 구동 모터 |
| `pinpoint` | GoBILDA Pinpoint | 오도메트리 로컬라이저 |
| `flywheelMotor` | DC Motor (BARE) | 플라이휠 모터 |
| `turretMotor` | DC Motor (312 RPM) | 터렛 회전 모터 |
| `intakeMotor` | DC Motor (312 RPM) | 인테이크 모터 |
| `Webcam 1` | USB 카메라 | AprilTag 비전 카메라 |
| `lift` | DC Motor | 리프트 모터 |

---

## 📄 라이선스

이 프로젝트는 [BSD 3-Clause License](LICENSE)를 따릅니다.

---

<div align="center">
  <sub>Made with ❤️ by Team 28646 · 라온봇</sub>
</div>
