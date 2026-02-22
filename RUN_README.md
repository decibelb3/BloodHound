# Running Bloodhound

## Option 1: Standalone Java (Windows – no Android)

1. Open a terminal in the `standalone` folder.
2. Run `compile.bat` to compile.
3. Run `run.bat` to start the program.

The program is a console app. You can add readings, list them, and export to CSV. Data is stored in `bloodhound_data.txt` in the same folder.

---

## Option 2: Android App (Android Studio)

1. Open Android Studio.
2. **File → Open** → select the `test` folder (the one containing `build.gradle`).
3. Wait for Gradle sync to finish.
4. Click **Run** (green triangle) or **Run → Run 'app'**.
5. Choose an emulator or connected device.

## What the app does

- **Enter readings**: Systolic, diastolic, heart rate, time of day, med timing, activity.
- **Save**: Saves to local SQLite via `DatabaseController`.
- **Export**: Writes all records to CSV via `ExportService`.
- **View**: Recent readings are shown with their AHA category (Normal, Elevated, Stage 1, Stage 2, Crisis).
