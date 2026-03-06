# Feature Specification: Package Test Suite

**Feature Branch**: `001-package-tests`
**Created**: 2026-03-06
**Status**: Draft
**Input**: User description: "I want to write test for this package"

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Verify SimInfo Model Correctness (Priority: P1)

As a contributor or consumer of the `sim_card_info` package, I want confidence that the `SimInfo` data model correctly constructs, serializes, deserializes, and compares instances so that data flowing through the plugin is reliable.

**Why this priority**: The `SimInfo` model is the core data contract of the package. All other functionality depends on it being correct. If the model is wrong, everything built on top of it is unreliable.

**Independent Test**: Can be fully tested by running unit tests against the `SimInfo` class in isolation with no native platform or Flutter binding required. Delivers confidence in data integrity.

**Acceptance Scenarios**:

1. **Given** a valid JSON map with all required fields, **When** `SimInfo.fromJson()` is called, **Then** a `SimInfo` instance is returned with all fields populated correctly.
2. **Given** a `SimInfo` instance, **When** `toJson()` is called, **Then** the result is a map that round-trips back to an equal `SimInfo` via `fromJson`.
3. **Given** two `SimInfo` instances with identical field values, **When** the `==` operator is used, **Then** they are considered equal and share the same `hashCode`.
4. **Given** two `SimInfo` instances with any differing field, **When** the `==` operator is used, **Then** they are not equal.
5. **Given** a `SimInfo` instance, **When** `copyWith()` is called with some overridden fields, **Then** a new instance is returned with the overridden values while unchanged fields retain their original values.
6. **Given** a `SimInfo` instance, **When** `toString()` is called, **Then** the result contains all field names and their values.
7. **Given** a JSON map where `slotIndex` is an integer, **When** `SimInfo.fromJson()` is called, **Then** `slotIndex` is correctly cast to a `String`.

---

### User Story 2 - Verify Method Channel Communication (Priority: P2)

As a contributor, I want to verify that `MethodChannelSimCardInfo` correctly communicates with the native platform via the method channel, parses the JSON response, and returns a properly typed list of `SimInfo` objects.

**Why this priority**: This is the integration layer between Dart and native code. Correct parsing ensures the plugin's primary function works as expected on all platforms.

**Independent Test**: Can be fully tested by mocking the `sim_card_info` method channel and asserting that `getSimInfo()` returns the expected `List<SimInfo>` without any native platform involved.

**Acceptance Scenarios**:

1. **Given** a mocked method channel that returns a valid JSON array string, **When** `getSimInfo()` is called, **Then** it returns a non-null `List<SimInfo>` with the correct number of entries and correctly mapped fields.
2. **Given** a mocked method channel that returns `null`, **When** `getSimInfo()` is called, **Then** it returns `null` without throwing an error.
3. **Given** a mocked method channel that returns an empty JSON array `[]`, **When** `getSimInfo()` is called, **Then** it returns an empty list (not null).
4. **Given** a mocked method channel returning data for multiple SIM cards, **When** `getSimInfo()` is called, **Then** all SIM entries are parsed into the returned list.

---

### User Story 3 - Verify Platform Interface and Plugin Delegation (Priority: P3)

As a contributor, I want to confirm that the platform interface is wired correctly — that `MethodChannelSimCardInfo` is the default instance and that `SimCardInfo` (the public API class) correctly delegates to whatever platform implementation is registered.

**Why this priority**: Validates the plugin architecture contract. Without this, a future platform implementation could silently break without detection.

**Independent Test**: Can be fully tested by verifying the default instance type and substituting a mock platform to assert delegation behavior.

**Acceptance Scenarios**:

1. **Given** no custom platform is registered, **When** `SimCardInfoPlatform.instance` is accessed, **Then** it is an instance of `MethodChannelSimCardInfo`.
2. **Given** a mock platform registered as `SimCardInfoPlatform.instance`, **When** `SimCardInfo().getSimInfo()` is called, **Then** it delegates to the mock platform's `getSimInfo()` and returns its result.
3. **Given** a mock platform that returns a predefined list of `SimInfo` objects, **When** `SimCardInfo().getSimInfo()` is called, **Then** the exact same list is returned.

---

### Edge Cases

- What happens when the JSON response contains an unexpected or missing field? (e.g., null `carrierName`)
- **Given** the native side throws a `PlatformException`, **When** `getSimInfo()` is called, **Then** the exception propagates to the caller without being swallowed silently.
- **Given** the method channel returns a malformed (non-JSON) string, **When** `getSimInfo()` is called, **Then** a `FormatException` is thrown (not a silent null return).

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: The test suite MUST cover `SimInfo.fromJson()` with valid, complete input data.
- **FR-002**: The test suite MUST cover `SimInfo.toJson()` and verify round-trip fidelity with `fromJson`.
- **FR-003**: The test suite MUST verify `SimInfo` equality (`==`) and `hashCode` consistency.
- **FR-004**: The test suite MUST verify `SimInfo.copyWith()` correctly overrides specified fields and preserves unspecified ones.
- **FR-005**: The test suite MUST verify `SimInfo.toString()` includes all field names and values.
- **FR-006**: The test suite MUST verify `MethodChannelSimCardInfo.getSimInfo()` correctly parses a mocked JSON response into a `List<SimInfo>`.
- **FR-007**: The test suite MUST verify `MethodChannelSimCardInfo.getSimInfo()` returns `null` when the channel returns `null`.
- **FR-008**: The test suite MUST verify `MethodChannelSimCardInfo.getSimInfo()` returns an empty list when the channel returns an empty JSON array.
- **FR-009**: The test suite MUST verify that `MethodChannelSimCardInfo` is the default `SimCardInfoPlatform` instance.
- **FR-010**: The test suite MUST verify that `SimCardInfo.getSimInfo()` delegates to the registered platform instance.
- **FR-011**: All existing placeholder or broken test cases in `test/sim_card_info_test.dart` and `test/sim_card_info_method_channel_test.dart` MUST be replaced with correct, passing tests.
- **FR-011a**: `SimInfo` model tests MUST reside in a new dedicated file `test/sim_info_test.dart`.
- **FR-011b**: Method channel tests MUST reside in `test/sim_card_info_method_channel_test.dart`.
- **FR-011c**: Platform interface and delegation tests MUST reside in `test/sim_card_info_test.dart`.
- **FR-012**: Tests MUST use mock/fake implementations for the method channel and platform interface — no real native calls.
- **FR-013**: The test suite MUST verify that a `PlatformException` thrown by the native side propagates out of `getSimInfo()` to the caller.
- **FR-014**: The test suite MUST verify that a malformed JSON string returned by the method channel causes a `FormatException` to be thrown.

### Key Entities

- **SimInfo**: The core data model representing a single SIM card. Fields: `carrierName`, `displayName`, `slotIndex` (String), `number`, `countryIso`, `countryPhonePrefix`.
- **MethodChannelSimCardInfo**: The method-channel-based implementation of the platform interface. Invokes `'getSimInfo'` on the `'sim_card_info'` channel.
- **SimCardInfoPlatform**: Abstract platform interface using `plugin_platform_interface`. Default instance is `MethodChannelSimCardInfo`.
- **SimCardInfo**: Public API class that delegates `getSimInfo()` to `SimCardInfoPlatform.instance`.

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: All test cases pass with zero failures when the test suite is run.
- **SC-002**: The `SimInfo` model is covered by at least 7 distinct test cases covering construction, serialization, equality, copyWith, and toString.
- **SC-003**: The method channel layer is covered by at least 6 test cases covering success, null, empty, multi-SIM, PlatformException, and malformed JSON scenarios.
- **SC-004**: The platform interface and public API delegation are covered by at least 3 test cases.
- **SC-005**: No test relies on real native platform calls — all tests run in isolation using mocks or fakes.
- **SC-006**: The previously broken/placeholder tests in `test/sim_card_info_test.dart` and `test/sim_card_info_method_channel_test.dart` are replaced with correct, meaningful assertions.

## Clarifications

### Session 2026-03-06

- Q: Should the test suite include negative/error path tests (PlatformException, malformed JSON)? → A: Yes — include error path tests for both PlatformException and malformed JSON.
- Q: Should SimInfo model tests live in a new dedicated file or be consolidated into existing files? → A: New dedicated file `test/sim_info_test.dart`; existing two files handle channel and platform/delegation tests respectively.

## Assumptions

- The test suite targets Dart unit tests using `flutter_test` — not integration tests or end-to-end device tests.
- `slotIndex` is serialized as an integer from native platforms but stored as a `String` in Dart; tests will validate this conversion.
- No new public API changes are made to the package as part of this feature — tests are written against the existing public interface.
- Platform-specific native code (Android/iOS) is out of scope for this test suite; only the Dart layer is tested.
