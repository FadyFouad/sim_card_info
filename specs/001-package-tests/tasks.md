# Tasks: Package Test Suite

**Input**: Design documents from `/specs/001-package-tests/`
**Prerequisites**: plan.md, spec.md, research.md, data-model.md, quickstart.md

**Organization**: Tasks grouped by user story — each story is independently implementable and testable.

## Format: `[ID] [P?] [Story?] Description`

- **[P]**: Can run in parallel (different files, no dependencies on incomplete tasks)
- **[Story]**: Which user story this task belongs to (US1, US2, US3)

## Path Conventions

All test files are at repository root: `test/`

---

## Phase 1: Setup

**Purpose**: Create the new test file and clear broken placeholder tests from existing files.

- [ ] T001 [P] Create `test/sim_info_test.dart` with file skeleton (import `flutter_test`, import `package:sim_card_info/sim_info.dart`, empty `main()`)
- [ ] T002 [P] Replace body of `test/sim_card_info_method_channel_test.dart` — remove broken placeholder test (`expect(result, '42')`), keep file with `TestWidgetsFlutterBinding.ensureInitialized()` and empty `main()`
- [ ] T003 [P] Replace body of `test/sim_card_info_test.dart` — remove broken placeholder test (`expect(result, '42')`), keep `MockSimCardInfoPlatform` stub and empty `main()`

---

## Phase 2: Foundational (Blocking Prerequisite)

**Purpose**: Verify the test harness runs cleanly on the current codebase before writing new cases.

**⚠️ CRITICAL**: Confirm this passes before writing any new test cases.

- [ ] T004 Run `flutter test` from the repository root and confirm zero test failures with the cleaned files from Phase 1

**Checkpoint**: Green baseline confirmed — user story test writing can now begin.

---

## Phase 3: User Story 1 — SimInfo Model Tests (Priority: P1) 🎯 MVP

**Goal**: Full test coverage of the `SimInfo` data model — construction, serialization, equality, `hashCode`, `copyWith`, and `toString`.

**Independent Test**: Run `flutter test test/sim_info_test.dart` — passes independently with no native device.

### Implementation

- [ ] T005 [US1] Add shared fixture constant and `group('SimInfo', ...)` wrapper in `test/sim_info_test.dart`
- [ ] T006 [US1] Write T-01: `fromJson` creates correct instance (assert all 6 fields) in `test/sim_info_test.dart`
- [ ] T007 [US1] Write T-02: `slotIndex` is cast from `int` to `String` (`slotIndex: 0` in JSON → `"0"` in model) in `test/sim_info_test.dart`
- [ ] T008 [US1] Write T-03: `toJson` round-trip fidelity (`SimInfo.fromJson(sim.toJson()) == sim`) in `test/sim_info_test.dart`
- [ ] T009 [US1] Write T-04 and T-05: equal instances are `==`; differing instances are `!=` in `test/sim_info_test.dart`
- [ ] T010 [US1] Write T-06: equal instances share `hashCode` in `test/sim_info_test.dart`
- [ ] T011 [US1] Write T-07: `copyWith` overrides specified fields; unchanged fields retain original values in `test/sim_info_test.dart`
- [ ] T012 [US1] Write T-08: `toString` output contains all 6 field names and their values in `test/sim_info_test.dart`

**Checkpoint**: Run `flutter test test/sim_info_test.dart` — all 8 test cases pass. User Story 1 complete.

---

## Phase 4: User Story 2 — Method Channel Communication Tests (Priority: P2)

**Goal**: Full test coverage of `MethodChannelSimCardInfo.getSimInfo()` — happy path, null, empty, multi-SIM, `PlatformException`, and malformed JSON.

**Independent Test**: Run `flutter test test/sim_card_info_method_channel_test.dart` — passes independently with no native device.

### Implementation

- [ ] T013 [US2] Add `TestWidgetsFlutterBinding.ensureInitialized()`, `MethodChannelSimCardInfo` instance, `MethodChannel('sim_card_info')` constant, and `setUp`/`tearDown` hooks for mock handler cleanup in `test/sim_card_info_method_channel_test.dart`
- [ ] T014 [US2] Write T-09: valid single-SIM JSON returns `List<SimInfo>` with correct count and all field values in `test/sim_card_info_method_channel_test.dart`
- [ ] T015 [US2] Write T-10: null channel return yields `null` result from `getSimInfo()` in `test/sim_card_info_method_channel_test.dart`
- [ ] T016 [US2] Write T-11: empty JSON array `"[]"` returns empty list (not `null`) in `test/sim_card_info_method_channel_test.dart`
- [ ] T017 [US2] Write T-12: multi-SIM JSON array returns full list with both entries correctly parsed in `test/sim_card_info_method_channel_test.dart`
- [ ] T018 [US2] Write T-13: mock handler throws `PlatformException`; assert `getSimInfo()` propagates it via `throwsA(isA<PlatformException>())` in `test/sim_card_info_method_channel_test.dart`
- [ ] T019 [US2] Write T-14: mock handler returns `'not-valid-json'`; assert `getSimInfo()` throws `FormatException` via `throwsA(isA<FormatException>())` in `test/sim_card_info_method_channel_test.dart`

**Checkpoint**: Run `flutter test test/sim_card_info_method_channel_test.dart` — all 6 test cases pass. User Story 2 complete.

---

## Phase 5: User Story 3 — Platform Interface and Plugin Delegation Tests (Priority: P3)

**Goal**: Verify `MethodChannelSimCardInfo` is the default platform instance and `SimCardInfo` correctly delegates `getSimInfo()` to whatever platform is registered.

**Independent Test**: Run `flutter test test/sim_card_info_test.dart` — passes independently with no native device.

### Implementation

- [ ] T020 [US3] Add imports (`flutter_test`, `plugin_platform_interface`, `sim_card_info.dart`, `sim_card_info_method_channel.dart`, `sim_card_info_platform_interface.dart`, `sim_info.dart`) and `MockSimCardInfoPlatform` class (using `MockPlatformInterfaceMixin`) in `test/sim_card_info_test.dart`
- [ ] T021 [US3] Write T-15: assert `SimCardInfoPlatform.instance` is `isInstanceOf<MethodChannelSimCardInfo>()` in `test/sim_card_info_test.dart`
- [ ] T022 [US3] Write T-16: register `MockSimCardInfoPlatform` returning a predefined `List<SimInfo>`; call `SimCardInfo().getSimInfo()`; assert returned list equals the mock's value in `test/sim_card_info_test.dart`

**Checkpoint**: Run `flutter test test/sim_card_info_test.dart` — both test cases pass. User Story 3 complete.

---

## Phase 6: Polish & Cross-Cutting Concerns

**Purpose**: Full suite validation, lint compliance, and spec acceptance criteria sign-off.

- [ ] T023 [P] Run `flutter test` from repository root — assert all 16 test cases pass with zero failures (SC-001)
- [ ] T024 [P] Run `flutter analyze` from repository root — assert zero errors or warnings in test files
- [ ] T025 Verify acceptance against spec success criteria: SC-002 (≥7 SimInfo tests), SC-003 (≥6 channel tests), SC-004 (≥3 platform tests), SC-005 (no real native calls), SC-006 (broken placeholders replaced)

---

## Dependencies & Execution Order

### Phase Dependencies

- **Setup (Phase 1)**: No dependencies — all 3 tasks start immediately in parallel
- **Foundational (Phase 2)**: Depends on Phase 1 completion — BLOCKS all user story phases
- **US1 (Phase 3)**: Depends on Phase 2 baseline green — independent of US2/US3
- **US2 (Phase 4)**: Depends on Phase 2 baseline green — independent of US1/US3
- **US3 (Phase 5)**: Depends on Phase 2 baseline green — independent of US1/US2
- **Polish (Phase 6)**: Depends on all story phases being complete

### User Story Dependencies

- **US1 (P1)**: No dependency on US2 or US3 — file `test/sim_info_test.dart` is entirely standalone
- **US2 (P2)**: No dependency on US1 or US3 — file `test/sim_card_info_method_channel_test.dart` is standalone
- **US3 (P3)**: No dependency on US1 or US2 — file `test/sim_card_info_test.dart` is standalone

### Within Each User Story

- Tasks within a user story are sequential (all write to the same test file)
- setUp/tearDown scaffolding (T005, T013, T020) must precede test case tasks within that story

### Parallel Opportunities

- T001, T002, T003 (Phase 1) — all different files, run in parallel
- T023, T024 (Phase 6) — independent checks, run in parallel
- US1, US2, US3 phases — different files, can be worked in parallel by different developers once Phase 2 is green

---

## Parallel Example: After Phase 2 (green baseline)

```
Developer A starts Phase 3 (US1): test/sim_info_test.dart
Developer B starts Phase 4 (US2): test/sim_card_info_method_channel_test.dart
Developer C starts Phase 5 (US3): test/sim_card_info_test.dart
```

All three work concurrently with zero file conflicts.

---

## Implementation Strategy

### MVP First (User Story 1 Only)

1. Complete Phase 1: Setup (T001–T003, parallel)
2. Complete Phase 2: Foundational (T004)
3. Complete Phase 3: User Story 1 (T005–T012)
4. **STOP and VALIDATE**: `flutter test test/sim_info_test.dart` — 8 tests green
5. Delivers: confidence that the core `SimInfo` data model is reliable

### Incremental Delivery

1. Setup + Foundational → baseline green
2. Add US1 → `SimInfo` model fully tested (MVP)
3. Add US2 → method channel layer fully tested
4. Add US3 → platform interface contract verified
5. Polish → full suite signed off

---

## Notes

- [P] = different files, no shared state — safe to run concurrently
- [Story] label maps each task to the user story for traceability
- Mock handler cleanup in `tearDown` (T013) is critical — prevents test pollution between cases in US2
- `TestWidgetsFlutterBinding.ensureInitialized()` is required in US2 but NOT in US1 (no Flutter bindings needed for pure model tests)
- Stop at each story checkpoint to run that story's file independently before proceeding
