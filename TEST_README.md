# Bloodhound Tests

Unit tests for `BPRecord`, `DatabaseController`, and `ExportService`.

## Test Summary

| Class | Tests | Purpose |
|-------|-------|---------|
| **BPRecordTest** | 16 tests | AHA category logic (Crisis, Stage 2, Stage 1, Elevated, Normal) and getters |
| **DatabaseControllerTest** | 2 tests | Table creation and insert (Robolectric) |
| **ExportServiceTest** | 3 tests | CSV export with header, record data, empty list (Robolectric) |

## How to Run Tests

### Option 1: Android Studio
1. Open the project in Android Studio.
2. Right-click `app/src/test` → **Run Tests**.
3. Or use the Gradle tool window: **app** → **Tasks** → **verification** → **test**.

### Option 2: Command Line (with Gradle)
If you have Gradle installed:
```bash
gradle wrapper   # generates gradlew and gradle-wrapper.jar
./gradlew test   # or gradlew.bat test on Windows
```

### Option 3: JUnit only (BPRecord)
`BPRecord` has no Android dependencies. You can run `BPRecordTest` in any JUnit 5 environment with the classpath including `BPRecord.class`.

## Fix in Your Code

`ExportService` was missing `import android.content.Context;`. That import has been added to your root `ExportService.java` file.
