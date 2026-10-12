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
  * Emergency manual control REST API endpoint (`/api/jesus/eject-and-resurrect`) for administrative overrides.
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
        └── java/
            └── com/
                └── jesus/
                    └── system/
                        ├── JesusApplication.java             # Bootstrap Main
                        ├── JesusWatchdogAgent.java           # Process Vitals & Excision Core
                        ├── JesusHealthIndicator.java         # Actuator Health Integration
                        ├── JesusCommandController.java       # Emergency Manual Control REST API
                        ├── JesusAbyssProbeComponent.java     # Raw TCP Socket Low-Level Probe
                        ├── JesusEgovWatchdogService.java     # eGovFrame DB & WAS Dual Verification
                        ├── JesusJvmDiagnosticsService.java   # JVM Heap & Deadlock Preemption Guard
                        └── JesusFileIoAndDiskGuardService.java # Disk I/O & Read-Only Lock Guard



🚀 Quick Start & Build


1. Enter Nix Environment (Recommended)


nix develop



2. Local Maven Build & Packaging


mvn clean package -DskipTests



3. Run Application


mvn spring-boot:run



📊 CI/CD Status


J.E.S.U.S CI/CD Pipeline


---




