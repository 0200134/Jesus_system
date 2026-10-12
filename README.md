
# ✝️ J.E.S.U.S (Java Ejectable Survival Unlimited Sustainable System)

> **"구형 공공 레거시와 뻗어버린 엔터프라이즈 시스템을 위한 궁극의 자가 치유(Self-Healing) 수호자 에이전트"**

---

## 🛡️ Overview (시스템 개요)
**J.E.S.U.S**는 외부 인프라 오케스트레이터(Kubernetes, systemd 등)의 도움 없이, **애플리케이션 코어 내부**에서 직접 시스템의 생체 신호(Process, DB, JVM, Disk I/O)를 실시간으로 감시하고, 장애 발생 시 스스로 좀비 프로세스를 처형한 뒤 **Nix Flakes** 기반의 순정 아티팩트로 즉각 부활시키는 하드코어 자급자족형 워치독 시스템입니다.

---

## ⚡ Key Features (핵심 기능)

* **Autonomous Process Excision & Resurrection (자율 사출 및 부활)**
  * Java 17 `ProcessHandle` API를 활용하여 응답 없는 레거시(eGovFrame) 프로세스를 정밀 타격(`SIGKILL`)하고, `nix run`을 통해 외부 환경 오염이 없는 순정 상태로 1초 내외 재스폰.
* **Multi-Layer Vitals Sentinel (다중 계층 복합 생체 감시망)**
  * **eGovFrame DB 연동 검사**: JDBC 커넥션 유효성(`conn.isValid`)을 통해 DB 락 및 세션 풀 고갈 실시간 포착.
  * **JVM 심층 진단**: 가비지 컬렉터 뻗기 직전의 힙 메모리 고갈(92% 임계치) 및 쓰레드 데드락 선제 방어.
  * **디스크 및 I/O 가드**: 디스크 여유 공간 고갈 및 파일시스템 Read-Only 잠김(I/O Lock) 실시간 감시.
* **Enterprise Observability (엔터프라이즈 관측성)**
  * Spring Boot Actuator 연동 커스텀 헬스 인디케이터(`_actuator/health`) 제공.
  * 긴급 상황 시 수동으로 사출 및 부활을 집행할 수 있는 REST API 엔드포인트(`/api/jesus/eject-and-resurrect`) 탑재.
* **Omni-Platform Nix Flakes Arena**
  * Linux 및 macOS 등 이질적인 환경에서도 비트 단위로 일치하는 격리된 빌드 및 실행 보장.

---

## 📂 Architecture & Directory Structure

```text
Jesus_system/
├── .github/
│   └── workflows/
│       └── ci.yml                     # GitHub Actions CI/CD 파이프라인
├── flake.nix                          # 멀티 플랫폼 Nix 셸 및 빌드 아키텍처
├── pom.xml                            # Maven 표준 빌드 설정 (Spring Boot 3.3.0)
└── src/
    └── main/
        └── java/
            └── com/
                └── jesus/
                    └── system/
                        ├── JesusApplication.java             # 부트스트랩 메인
                        ├── JesusWatchdogAgent.java           # 프로세스 생체 감시 & 사출 코어
                        ├── JesusHealthIndicator.java         # 액추에이터 헬스 연동
                        ├── JesusCommandController.java       # 긴급 수동 제어 REST API
                        ├── JesusEgovWatchdogService.java     # eGovFrame DB 및 WAS 복합 검증
                        ├── JesusJvmDiagnosticsService.java   # JVM 힙 및 데드락 선제 방어
                        └── JesusFileIoAndDiskGuardService.java # 디스크 I/O 및 Read-Only 락 가드



🚀 Quick Start & Build


1. Nix 환경 진입 (Recommended)


nix develop



2. Maven 로컬 빌드 및 패키징


mvn clean package -DskipTests



3. 애플리케이션 구동


mvn spring-boot:run



📊 CI/CD Status


J.E.S.U.S CI/CD Pipeline


---


