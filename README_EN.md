📄 README_EN.md (Global Edition)


# ✝️ J.E.S.U.S (Java Ejectable Survival Unlimited Sustainable System)

> **"The Ultimate Self-Healing Watchdog Agent for Legacy eGovFrame & Enterprise Systems"**

---

## 🛡️ Overview
**J.E.S.U.S** is an autonomous, self-healing watchdog agent designed for legacy enterprise and public sector Java/Spring environments (such as eGovFrame). Without relying on external infrastructure orchestrators (like Kubernetes or systemd), J.E.S.U.S operates directly within the application core to continuously monitor system vitals (Process, DB, JVM, and Disk I/O). Upon detecting anomalies or freezes, it independently excises the zombie process and instantly respawns a pristine, immutable artifact via **Nix Flakes**.

---

## ⚡ Key Features

* **Autonomous Process Excision & Resurrection**
  * Leverages Java 17 `ProcessHandle` API to precisely target and forcibly terminate (`SIGKILL`) unresponsive legacy processes, followed by instant unpolluted respawning via `nix run`.
* **Multi-Layer Vitals Sentinel**
  * **eGovFrame DB Health Check**: Validates JDBC connections (`conn.isValid`) to catch DB locks and connection pool exhaustion in real-time.
  * **Deep JVM Diagnostics**: Preemptively detects heap exhaustion (92% critical threshold) and thread deadlocks before Garbage Collection locks up.
  * **Disk & I/O Guard**: Monitors disk space depletion and file system Read-Only lockouts (I/O deadlocks).
* **Enterprise Observability & Override**
  * Spring Boot Actuator custom health indicator integration (`/actuator/health`).
  * Emergency manual control REST API endpoint (`/api/jesus/eject-and-resurrect`) for administrative overrides. It requires the `X-Watchdog-Token` header matching `JESUS_WATCHDOG_MANUAL_CONTROL_TOKEN`; the endpoint is disabled when no token is configured.
* **Omni-Platform Nix Flakes Arena**
  * Guarantees bit-identical isolated builds and runtime environments across Linux, macOS, and other target hosts.

---

## 📂 Directory Structure

```text
Jesus_system/
├── .github/
│   └── workflows/
│       └── ci.yml                     # GitHub Actions CI/CD Pipeline
├── flake.nix                          # Multi-platform Nix shell & build architecture
├── pom.xml                            # Maven standard build config (Spring Boot 3.3.0)
└── src/
    └── main/
        ├── java/com/jesus/system/
        │   ├── JesusApplication.java              # Bootstrap Main
        │   ├── JesusWatchdogAutoConfiguration.java # Spring Boot Starter Auto-Configuration
        │   ├── config/
        │   │   └── WatchdogProperties.java        # Externalized jesus.watchdog.* settings
        │   ├── core/
        │   │   ├── ProcessLifecycleManager.java   # Single Eject & Nix Resurrection Core
        │   │   └── VitalsRegistry.java            # Shared Monitor Vitals Registry
        │   ├── monitor/
        │   │   ├── WatchdogAgent.java             # 0.5s Actuator Ping & Ejection Loop
        │   │   ├── AbyssProbeMonitor.java         # Raw TCP Socket Low-Level Probe
        │   │   ├── EgovWatchdogMonitor.java       # eGovFrame DB & WAS Dual Verification
        │   │   ├── JvmDiagnosticsMonitor.java     # JVM Heap & Deadlock Preemption Guard
        │   │   ├── DiskIoGuardMonitor.java        # Disk I/O & Read-Only Lock Guard
        │   │   └── HttpVitalsProbe.java           # Shared HTTP Vitals Probe
        │   ├── api/
        │   │   └── WatchdogCommandController.java # Emergency Manual Control REST API
        │   └── health/
        │       └── WatchdogHealthIndicator.java   # Health Indicator Backed by Real Vitals
        └── resources/
            ├── application.yml                    # Server port (9090) & watchdog thresholds
            └── META-INF/spring/                   # Auto-configuration registration (starter mode)



🚀 Quick Start & Build


1. Enter Nix Environment (Recommended)


nix develop



2. Local Maven Build & Packaging


mvn clean verify



3. Run Application


mvn spring-boot:run



📦 Adopting into an Existing Spring App (Starter Mode)


Any Spring Boot 3 application (e.g. a modernized eGovFrame service) can embed the full watchdog into its core with a single dependency:


<dependency>
    <groupId>com.jesus.system</groupId>
    <artifactId>jesus-watchdog-agent</artifactId>
    <version>1.0.0</version>
</dependency>


Just having it on the classpath auto-registers every monitor, the health indicator, and the REST API. Disable anytime with jesus.watchdog.enabled=false. Configure `JESUS_WATCHDOG_MANUAL_CONTROL_TOKEN` to enable the emergency endpoint; leave it unset to keep manual ejection disabled.


⚠️ javax-era eGovFrame (Spring Boot 2.x) hosts cannot embed it directly due to the Jakarta namespace split — keep running the watchdog in standalone (sidecar) mode for those systems.



🧪 Test & Formal Verification (Nix)


1. Run the full test suite inside the reproducible Nix environment


nix develop -c mvn clean verify



2. Build the deployment artifact as a pure Nix derivation (offline, hash-pinned dependencies)


nix build .#jesus-server-17



ℹ️ The first nix build fails with an mvnHash mismatch error — paste the printed hash into flake.nix (mvnHash = "...") and re-run. From then on the build is fully reproducible.



📊 CI/CD Status


J.E.S.U.S CI/CD Pipeline


---



