
Electrical eGov Resilience

Free and Open-Source Research for Resilient Electrical Infrastructure

Free. Open. Verifiable. Resilient.

Our Mission

Electrical eGov Resilience is an open-source initiative dedicated to improving the security, reliability, and recoverability of critical electrical infrastructure.

Built upon the ideas of the J.E.S.U.S (Java Ejectable Survival Unlimited Sustainable System) project, it aims to combine Java-based system monitoring, eGovFrame integration, and electrical-grid resilience simulations.

Our mission is to make defensive infrastructure research freely accessible to developers, researchers, students, and institutions worldwide.

Core Principles

- Free Forever: Open-source software freely available to everyone.
- Defense First: Focus on prevention, monitoring, and recovery.
- Safety by Design: No direct control of operational power-grid equipment.
- Open Verification: Transparent tests, source code, and technical documentation.
- Global Collaboration: Contributions welcomed regardless of country or institution.

Technology Direction

- Java 21
- eGovFrame and Spring ecosystem
- J.E.S.U.S-inspired system monitoring
- Microgrid outage simulation
- Backup-power endurance analysis
- Auditable recovery planning

Project Status

Early-stage development. The current GitHub repository does not yet contain a verified production-ready implementation.

Development branch: "Dev"

Stable branch: "main"

Proposed Electrical Resilience Foundation

The long-term vision is to establish an independent, international, nonprofit open-source organization supporting research, education, and collaboration in electrical infrastructure resilience.

The foundation has not yet been established.

Licensing

MIT License. Commercial and noncommercial use are permitted under its terms. Required copyright and license notices from upstream projects will be retained.

한국어 소개

Electrical eGov Resilience는 전력망 사이버보안과 국가 핵심 인프라의 복원력 향상을 목표로 하는 무료 오픈소스 연구 프로젝트입니다.

Java, 전자정부 표준프레임워크, Jesus_system 기반 시스템 감시 기술을 활용하여 가상 전력망의 장애 분석과 안전한 복구 지원 기술을 연구합니다.

장기적으로 국제 개발자와 연구자가 공동 운영하는 독립적인 비영리 오픈소스 재단으로 성장하는 것을 목표로 합니다.

함께 검증하고, 함께 복원력을 높입니다.

compatible  with:  https://www.egovframe.go.kr/home/sub.do?menuNo=107


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
        ├── java/com/jesus/system/
        │   ├── JesusApplication.java              # 부트스트랩 메인
        │   ├── JesusWatchdogAutoConfiguration.java # 스프링 부트 스타터 자동 설정
        │   ├── config/
        │   │   └── WatchdogProperties.java        # jesus.watchdog.* 외부화 설정
        │   ├── core/
        │   │   ├── ProcessLifecycleManager.java   # 사출(Eject) & Nix 부활 단일 코어
        │   │   └── VitalsRegistry.java            # 모니터 생체 신호 레지스트리
        │   ├── monitor/
        │   │   ├── WatchdogAgent.java             # 0.5초 액추에이터 헬스 핑 & 사출 루프
        │   │   ├── AbyssProbeMonitor.java         # Raw TCP 소켓 저수준 프로브
        │   │   ├── EgovWatchdogMonitor.java       # eGovFrame DB & WAS 복합 검증
        │   │   ├── JvmDiagnosticsMonitor.java     # JVM 힙 & 데드락 선제 방어
        │   │   ├── DiskIoGuardMonitor.java        # 디스크 I/O & Read-Only 락 가드
        │   │   └── HttpVitalsProbe.java           # 공용 HTTP 생존 프로브
        │   ├── api/
        │   │   └── WatchdogCommandController.java # 긴급 수동 제어 REST API
        │   └── health/
        │       └── WatchdogHealthIndicator.java   # 실제 생체 신호 기반 헬스 인디케이터
        └── resources/
            ├── application.yml                    # 서버 포트(9090) 및 워치독 임계치 설정
            └── META-INF/spring/                   # 스타터 모드 자동 설정 등록



🚀 Quick Start & Build


1. Nix 환경 진입 (Recommended)


nix develop



2. Maven 로컬 빌드 및 패키징


mvn clean verify



3. 애플리케이션 구동


mvn spring-boot:run



📦 기존 Spring 앱에 적용 (Adoptable Starter 모드)


다른 Spring Boot 3 애플리케이션(예: 현대화된 eGovFrame 서비스)에 의존성 하나로 워치독 전체를 코어 내부에 내장할 수 있습니다:


<dependency>
    <groupId>com.jesus.system</groupId>
    <artifactId>jesus-watchdog-agent</artifactId>
    <version>1.0.0</version>
</dependency>


클래스패스에 추가되는 것만으로 모든 모니터·헬스 인디케이터·REST API가 자동 등록됩니다. 비활성화는 jesus.watchdog.enabled=false.


⚠️ javax.* 시대의 구형 eGovFrame(Spring Boot 2.x)에는 Jakarta 네임스페이스 충돌로 직접 내장 불가 — 그 경우 지금처럼 독립 실행(사이드카) 모드로 구동하세요.



🧪 Test & Formal Verification (Nix)


1. 재현 가능한 Nix 환경 내부에서 전체 테스트 스위트 실행


nix develop -c mvn clean verify



2. 배포 아티팩트를 순수 Nix Derivation으로 빌드 (의존성 해시 고정, 오프라인 빌드)


nix build .#jesus-server-17



ℹ️ 최초 nix build는 mvnHash 불일치 오류로 실패합니다 — 오류 메시지에 출력된 해시를 flake.nix의 mvnHash에 붙여넣고 재실행하세요. 이후 빌드는 완전히 재현 가능합니다.



📊 CI/CD Status


J.E.S.U.S CI/CD Pipeline


---


