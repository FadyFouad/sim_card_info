# Implementation Plan: Package Test Suite

**Branch**: `001-package-tests` | **Date**: 2026-03-06 | **Spec**: [spec.md](spec.md)
**Input**: Feature specification from `/specs/001-package-tests/spec.md`

## Summary

Write a complete Dart unit test suite for the `sim_card_info` Flutter plugin. Replace two broken placeholder test files and add one new dedicated model test file. Cover the `SimInfo` data model, the `MethodChannelSimCardInfo` channel layer, and the `SimCardInfoPlatform` interface/delegation contract. All tests run in isolation using mocked method channels and fake platform interfaces — no native platform required.

## Technical Context

**Language/Version**: Dart (SDK >= 3.1.5)
**Primary Dependencies**: `flutter_test` (dev), `plugin_platform_interface` (production — provides `MockPlatformInterfaceMixin`)
**Storage**: N/A
**Testing**: `flutter_test` via `flutter test` CLI
**Target Platform**: Host machine (Dart VM) — no device required
**Project Type**: Flutter plugin package (library)
**Performance Goals**: N/A — test suite correctness is the only goal
**Constraints**: No real native calls; all tests must pass on host without a connected device
**Scale/Scope**: 3 test files, ~16 test cases total

## Constitution Check

The project constitution (`constitution.md`) contains only template placeholders — no ratified principles are defined. No gate violations to enforce.

Post-design re-check: N/A — no constitution rules to verify against.

## Project Structure

### Documentation (this feature)

```text
specs/001-package-tests/
├── plan.md          (this file)
├── research.md      (Phase 0 output)
├── data-model.md    (Phase 1 output)
├── quickstart.md    (Phase 1 output)
└── tasks.md         (Phase 2 output - /speckit.tasks)
```

### Source Code (repository root)

```text
test/
├── sim_info_test.dart                     # NEW  - SimInfo model tests
├── sim_card_info_method_channel_test.dart # REPLACE - MethodChannelSimCardInfo tests
└── sim_card_info_test.dart                # REPLACE - platform interface + delegation tests
```

**Structure Decision**: Single-level `test/` directory. Three files map 1:1 to the three source files under test. Matches Flutter plugin conventions.

## Phase 0: Research (Complete)

See [research.md](research.md) for full findings. Key resolutions:

| Unknown | Resolution |
|---------|-----------|
| Method channel mocking API | `TestDefaultBinaryMessengerBinding` (current Flutter 3.x API) |
| PlatformException testing | Set mock handler to throw; use `throwsA(isA<PlatformException>())` |
| Malformed JSON testing | Return invalid string from handler; expect `FormatException` |
| Platform interface mocking | `MockPlatformInterfaceMixin` from existing `plugin_platform_interface` dep |
| Additional dependencies | None required |

## Phase 1: Design (Complete)

See [data-model.md](data-model.md) for entity details and test fixtures.

**No contracts directory**: This feature adds tests to an existing library. No new public API surface is introduced.

### Test Case Inventory

#### `test/sim_info_test.dart` (new)

| ID | Test name | Assertion |
|----|-----------|-----------|
| T-01 | fromJson creates correct instance | All 6 fields match expected values |
| T-02 | slotIndex is cast from int to String | `slotIndex == "0"` when JSON has `slotIndex: 0` |
| T-03 | toJson round-trips via fromJson | `SimInfo.fromJson(sim.toJson()) == sim` |
| T-04 | equal instances are == | Two identical instances are equal |
| T-05 | differing instances are != | Instances differing in any field are not equal |
| T-06 | equal instances share hashCode | `a.hashCode == b.hashCode` when `a == b` |
| T-07 | copyWith overrides specified fields | Changed field updated; others unchanged |
| T-08 | toString contains all fields | Output contains all 6 field names and values |

#### `test/sim_card_info_method_channel_test.dart` (replace)

| ID | Test name | Assertion |
|----|-----------|-----------|
| T-09 | valid JSON returns populated list | Returns `List<SimInfo>` with correct count and field values |
| T-10 | null channel return yields null | `getSimInfo()` returns `null` |
| T-11 | empty array returns empty list | Returns `[]`, not `null` |
| T-12 | multi-SIM JSON returns full list | All entries parsed correctly |
| T-13 | PlatformException propagates | `throwsA(isA<PlatformException>())` |
| T-14 | malformed JSON throws FormatException | `throwsA(isA<FormatException>())` |

#### `test/sim_card_info_test.dart` (replace)

| ID | Test name | Assertion |
|----|-----------|-----------|
| T-15 | default instance is MethodChannelSimCardInfo | `SimCardInfoPlatform.instance` is `isInstanceOf<MethodChannelSimCardInfo>()` |
| T-16 | getSimInfo delegates to platform mock | Mock's return value is returned by `SimCardInfo().getSimInfo()` |

**Total**: 16 test cases across 3 files.
