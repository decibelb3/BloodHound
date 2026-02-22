# Software Requirements Specification (SRS)
## Bloodhound Standalone Health Tracker

**Document Version:** 2.0  
**Date:** February 15, 2026  
**Product:** Bloodhound (Standalone Java Desktop)  
**Author:** [Your Name]

---

## 1. Introduction

### 1.1 Purpose
This SRS defines the requirements for Bloodhound, a standalone Java desktop health-tracking application. It is intended for developers, testers, instructors, and project stakeholders. The system supports tracking blood pressure and cholesterol-related metrics, local analytics, and export, without using a database backend.

### 1.2 Scope
- **Product name:** Bloodhound Standalone Health Tracker
- **Summary:** A desktop application that allows users to record blood pressure and lipid panel values (Total Cholesterol, LDL, HDL), review history, view trends/alerts, and export records.
- **Benefits:** Improves consistency of personal health tracking, enables pattern detection, and supports report sharing.
- **Out of scope:** Medical diagnosis, treatment recommendations, cloud synchronization, and external device integration.

### 1.3 Definitions and Acronyms
| Term | Definition |
|------|------------|
| BP | Blood Pressure |
| Systolic | Pressure during heart contraction (mmHg) |
| Diastolic | Pressure between heartbeats (mmHg) |
| AHA | American Heart Association |
| LDL | Low-Density Lipoprotein cholesterol |
| HDL | High-Density Lipoprotein cholesterol |
| Total Cholesterol | Total blood cholesterol value |
| CSV | Comma-Separated Values export format |

### 1.4 References
- AHA Blood Pressure Categories (informational UI logic).
- Instructor-provided course project requirements.

---

## 2. Overall Description

### 2.1 Product Perspective
The product is a standalone desktop application built with Java and Swing. Data is stored locally in files (no database). The app supports multi-screen navigation and structured local analytics.

### 2.2 User Classes and Characteristics
| User Class | Description | Technical Level |
|------------|-------------|-----------------|
| Primary User | Individual manually entering daily readings | Low to Medium |
| Reviewer/Professor | Evaluates features, behavior, and complexity | Medium |

### 2.3 Operating Environment
- Windows 10/11 desktop environment.
- Java Runtime Environment (JRE/JDK 17+ recommended).
- Local file system access for persistent storage and exports.

### 2.4 Design and Implementation Constraints
- Must not use a relational or NoSQL database.
- Must run as an offline local desktop app.
- Must preserve readability and maintainability of Java code.
- UI must provide navigation between major feature areas.

### 2.5 Assumptions and Dependencies
- Users enter health data manually from external measurements/lab reports.
- Users have file-write permissions in the application working directory.
- Health ranges are informational and may vary by provider guidance.

---

## 3. System Features and Requirements

### 3.1 Functional Requirements

| ID | Requirement | Priority | Source |
|----|-------------|----------|--------|
| FR-001 | The system shall allow users to create a new health record with systolic, diastolic, heart rate, time of day, medication timing, and activity fields. | High | User request |
| FR-002 | The system shall allow users to enter lipid values: Total Cholesterol, LDL, and HDL for each record. | High | User request |
| FR-003 | The system shall classify BP records into AHA-style categories (Normal, Elevated, Stage 1, Stage 2, Crisis). | High | Existing behavior |
| FR-004 | The system shall validate numeric inputs and reject invalid/non-numeric values with user-friendly messages. | High | Quality requirement |
| FR-005 | The system shall persist all records to local file storage without a database. | High | User constraint |
| FR-006 | The system shall load all previously saved records at startup. | High | Core behavior |
| FR-007 | The system shall provide navigation buttons for key screens (Add Reading, View Readings, Export, Analytics). | High | User request |
| FR-008 | The system shall display reading history sorted by newest first. | High | Existing behavior |
| FR-009 | The system shall support CSV export of all records including BP and lipid values. | High | Existing + requested data |
| FR-010 | The system shall provide summary analytics (e.g., averages and category counts over configurable windows such as 7/30 days). | Medium | Complexity enhancement |
| FR-011 | The system shall provide alert logic for high-risk patterns (e.g., repeated Stage 2/Crisis readings). | Medium | Complexity enhancement |
| FR-012 | The system shall support filtering history by date range and/or category. | Medium | Complexity enhancement |
| FR-013 | The system shall maintain a local changelog/version note file for project documentation updates. | Medium | Documentation need |

**Detailed Requirement Notes**
- **FR-002 (Lipid Values):** Fields shall use mg/dL units by default; values must be positive integers.
- **FR-005 (No DB Storage):** Storage format may be structured text/JSON with schema version metadata.
- **FR-009 (CSV):** Export headers shall include at minimum: `SessionID, Timestamp, Systolic, Diastolic, HeartRate, TotalCholesterol, LDL, HDL, Category`.

### 3.2 Non-Functional Requirements

#### 3.2.1 Performance
- App startup shall complete within 3 seconds under normal local conditions with up to 5,000 records.
- Loading history view shall complete within 2 seconds for 5,000 records.
- CSV export of 5,000 records shall complete within 5 seconds.

#### 3.2.2 Security
- Data remains local by default; no remote transmission is required.
- Input shall be sanitized to prevent malformed file content.
- Optional: local file obfuscation/encryption may be added in future versions.

#### 3.2.3 Availability
- Application shall function fully offline.
- Failure of export shall not corrupt stored record files.

#### 3.2.4 Usability
- Primary workflows (add, navigate, export) should be reachable within one screen transition.
- Buttons and labels shall be clearly visible with consistent styling.
- Error messages shall be actionable and human-readable.

#### 3.2.5 Reliability
- Record writes shall be atomic or protected by temporary-file replacement strategy to reduce corruption risk.
- App shall handle missing/corrupt storage files gracefully (warn user, continue running).

---

## 4. External Interface Requirements

### 4.1 User Interfaces
- **Dashboard/Navigation Bar:** Buttons for Add Reading, View Readings, Export, Analytics.
- **Add Reading Screen:** BP inputs + lipid inputs (Total Cholesterol, LDL, HDL) + context fields.
- **History Screen:** List/table view with sorting/filtering.
- **Analytics Screen:** Aggregated statistics and risk indicators.
- **Export Screen:** Export action, path feedback, and success/failure status.

### 4.2 Hardware Interfaces
- Standard keyboard/mouse input only.
- No required external medical device integration.

### 4.3 Software Interfaces
- Java SE runtime.
- Local filesystem API for persistence and exports.
- Optional charting library if advanced analytics visualization is implemented.

### 4.4 Communication Interfaces
- None required for v2 offline mode.

---

## 5. Other Requirements

### 5.1 Legal / Compliance
- The application must display a disclaimer that it is for tracking/educational use and not medical diagnosis.
- Health guideline ranges shown are informational only.

### 5.2 Documentation
- Must include:
  - User run instructions (`compile.bat`, `run.bat`).
  - Feature/version summary.
  - Change log per phase/version.

### 5.3 Training
- No formal training required.
- A short first-run guide/help section should explain basic workflows.

---

## 6. Appendix

### 6.1 Use Case List
- UC-001: User adds a BP + lipid reading.
- UC-002: User reviews reading history and categories.
- UC-003: User reviews analytics and alert summaries.
- UC-004: User exports records to CSV.
- UC-005: User filters records by date/category.

### 6.2 Informational Category Reference
| Category | BP Criteria (mmHg) |
|----------|---------------------|
| Normal | Systolic &lt; 120 and Diastolic &lt; 80 |
| Elevated | Systolic 120-129 and Diastolic &lt; 80 |
| Stage 1 | Systolic 130-139 or Diastolic 80-89 |
| Stage 2 | Systolic >= 140 or Diastolic >= 90 |
| Crisis | Systolic &gt; 180 or Diastolic &gt; 120 |

### 6.3 Suggested Future Extensions (Optional)
- JSON schema version migration support.
- Profile-based multi-user local storage.
- Trend charts and rolling-window risk scoring.

---

*End of Software Requirements Specification — Bloodhound Standalone Health Tracker*
