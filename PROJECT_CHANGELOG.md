# Bloodhound Project Changelog

## v0.1 - Initial baseline
- Created core BP model (`BPRecord`) with AHA category logic.
- Added local storage and CSV export foundations.
- Added first console workflow for add/list/export.

## v0.2 - Standalone desktop GUI
- Replaced console-first flow with Swing GUI navigation.
- Added cards for Add Reading, View Readings, and Export.
- Improved styling and usability of buttons/forms.

## v0.3 - SRS alignment and analytics
- Added `Total Cholesterol`, `LDL`, and `HDL` fields to record model.
- Extended persistence format and CSV export fields.
- Added analytics screen with averages, category distribution, and risk alerts.

## v0.4 - History management and filtering
- Added history filters:
  - Category filter
  - Date range filter (`yyyy-mm-dd`)
- Added record maintenance actions:
  - Edit by Session ID (supports unique prefix lookup)
  - Delete by Session ID (supports unique prefix lookup)
- Added file-storage update/delete APIs in `DatabaseControllerDesktop`.

## Notes
- Storage remains file-based by design (no database), matching assignment constraints.
- Older saved records remain readable; new fields default to `0` when missing.
