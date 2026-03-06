# Quickstart: Running the Package Test Suite

**Branch**: `001-package-tests` | **Date**: 2026-03-06

---

## Prerequisites

- Flutter SDK ≥ 3.3.0 installed and on your PATH
- Run from the package root: `/Users/fadyfouad/AndroidStudioProjects/sim_card_info/`

---

## Run All Unit Tests

```bash
flutter test
```

Expected output: all tests pass, zero failures.

---

## Run a Specific Test File

```bash
# SimInfo model tests
flutter test test/sim_info_test.dart

# Method channel layer tests
flutter test test/sim_card_info_method_channel_test.dart

# Platform interface + delegation tests
flutter test test/sim_card_info_test.dart
```

---

## Test File Map

| File | What it tests | New/Replace |
|------|--------------|-------------|
| `test/sim_info_test.dart` | `SimInfo` model: fromJson, toJson, equality, hashCode, copyWith, toString | New |
| `test/sim_card_info_method_channel_test.dart` | `MethodChannelSimCardInfo.getSimInfo()` via mocked channel | Replace |
| `test/sim_card_info_test.dart` | `SimCardInfoPlatform` default instance + `SimCardInfo` delegation | Replace |

---

## No Additional Setup Required

All test dependencies (`flutter_test`, `plugin_platform_interface`) are already declared in `pubspec.yaml`. No `pub get` or configuration changes are needed beyond what is already in the repository.
