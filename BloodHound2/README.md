# Bloodhound (SDS v4.1 Implementation)

This repository contains a standalone Java 17 implementation of the Bloodhound offline health tracking system based on the provided SDS.

## Implemented architecture

- `model` – record and analytics data models
- `service` – validation, classification, analytics, export, and workflow orchestration
- `storage` – JSON file persistence with backup-based recovery
- `ui` – Swing desktop interfaces (classic + modern) and console fallback
- `util` – shared result and parsing helpers

## Core SDS-aligned behaviors

- Add health records with:
  - blood pressure (systolic/diastolic)
  - heart rate
  - lipid panel (total cholesterol, LDL, HDL, triglycerides)
  - contextual tags (`timeOfDay`, `medTiming`, `activityTiming`)
- Input validation (range checks, required metric check, systolic/diastolic consistency)
- Blood pressure classification (Normal, Elevated, Stage 1, Stage 2, Crisis)
- Lipid classification and risk summary
- Local JSON persistence in:
  - `health_records.json` (primary)
  - `health_records.backup.json` (backup)
- Startup recovery from backup when primary storage is corrupted
- Record viewing and date-range filtering
- Local analytics (averages, category counts, alert/trend summaries)
- CSV export of all records

## Build and run

Using JDK 17 directly (no `rg` required):

```bash
mkdir -p out
javac -d out $(git ls-files '*.java')
```

Run the current/recommended UI (modern-themed desktop):

```bash
java -cp out com.txstate.bloodhound.BloodhoundModernDesktopApplication
```

Run the classic desktop UI:

```bash
java -cp out com.txstate.bloodhound.BloodhoundDesktopApplication
```

Optional (if Maven is installed in your environment):

```bash
mvn clean compile
mvn exec:java -Dexec.mainClass=com.txstate.bloodhound.BloodhoundModernDesktopApplication
```

### Console fallback

The original console-driven flow is still available:

```bash
java -cp out com.txstate.bloodhound.BloodhoundApplication
```

## Notes

- The app is offline and local-only.
- No database, cloud sync, networking, authentication, or multi-user support is included.
