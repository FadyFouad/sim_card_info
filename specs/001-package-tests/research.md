# Research: Package Test Suite

**Branch**: `001-package-tests` | **Date**: 2026-03-06

---

## Decision 1: Method Channel Mocking Approach

**Decision**: Use `TestDefaultBinaryMessengerBinding.instance.defaultBinaryMessenger.setMockMethodCallHandler()` to intercept method channel calls in tests.

**Rationale**: This is the standard Flutter plugin testing API, already imported via `flutter_test`. It does not require any additional dependencies. The channel name `'sim_card_info'` is already annotated `@visibleForTesting` in `MethodChannelSimCardInfo`, confirming this approach is intended.

**Alternatives considered**:
- `MethodChannel.setMockMethodCallHandler()` (deprecated in Flutter 3.x — replaced by the binary messenger approach above)
- Full mock of `MethodChannelSimCardInfo` class — unnecessary since the real class is thin and testing it directly with a mocked channel provides higher confidence

---

## Decision 2: Platform Interface Mocking Approach

**Decision**: Use `MockPlatformInterfaceMixin` from the `plugin_platform_interface` package (already a production dependency) to create a mock `SimCardInfoPlatform`.

**Rationale**: `MockPlatformInterfaceMixin` bypasses the `PlatformInterface.verifyToken` check, which would otherwise prevent non-subclass instances from being set as `SimCardInfoPlatform.instance`. This is the official, documented pattern for testing federated Flutter plugins.

**Alternatives considered**:
- Extending `SimCardInfoPlatform` directly — valid but creates a tighter coupling; `MockPlatformInterfaceMixin` is more idiomatic for the `plugin_platform_interface` pattern

---

## Decision 3: PlatformException Test Strategy

**Decision**: Mock the method channel handler to throw `PlatformException` and assert that `getSimInfo()` propagates it without catching or swallowing it.

**Rationale**: The existing `SimCardInfoIos` implementation throws `PlatformException` directly (code `'404'`). The `MethodChannelSimCardInfo.getSimInfo()` implementation does not have a try/catch, so `PlatformException` naturally propagates. The test verifies this contract is maintained.

**Pattern**:
```dart
TestDefaultBinaryMessengerBinding.instance.defaultBinaryMessenger
    .setMockMethodCallHandler(channel, (MethodCall methodCall) async {
  throw PlatformException(code: 'UNAVAILABLE', message: 'SIM not accessible');
});
expect(() => platform.getSimInfo(), throwsA(isA<PlatformException>()));
```

---

## Decision 4: Malformed JSON Test Strategy

**Decision**: Mock the channel to return a non-JSON string (e.g., `'not-valid-json'`) and assert that `getSimInfo()` throws a `FormatException`.

**Rationale**: `MethodChannelSimCardInfo.getSimInfo()` calls `json.decode(info)` without a try/catch. `dart:convert`'s `json.decode` throws `FormatException` on invalid input. The test verifies this behaviour is observable (not silently swallowed by a future framework layer).

**Pattern**:
```dart
TestDefaultBinaryMessengerBinding.instance.defaultBinaryMessenger
    .setMockMethodCallHandler(channel, (MethodCall methodCall) async {
  return 'not-valid-json';
});
expect(() => platform.getSimInfo(), throwsA(isA<FormatException>()));
```

---

## Decision 5: Test File Organization

**Decision**: Three test files, each with a single responsibility:
| File | Responsibility |
|------|---------------|
| `test/sim_info_test.dart` | `SimInfo` model (new file) |
| `test/sim_card_info_method_channel_test.dart` | `MethodChannelSimCardInfo` method channel layer (replace existing) |
| `test/sim_card_info_test.dart` | `SimCardInfoPlatform` default instance + `SimCardInfo` delegation (replace existing) |

**Rationale**: Matches Flutter plugin conventions. Mirrors the existing source file structure (`sim_info.dart`, `sim_card_info_method_channel.dart`, `sim_card_info.dart`). Each file can be run independently.

**Alternatives considered**:
- Single monolithic test file — harder to navigate, masks which layer a failure belongs to
- Mirror each source file with a `_test.dart` suffix — same result, just applying the standard Flutter naming convention

---

## Decision 6: No Additional Test Dependencies Required

**Decision**: All required test infrastructure is available through `flutter_test` (already in `dev_dependencies`) and `plugin_platform_interface` (already in `dependencies`).

**Rationale**:
- `flutter_test` provides: `test`, `expect`, `group`, `setUp`, `tearDown`, `throwsA`, `isA`, `TestWidgetsFlutterBinding`, `TestDefaultBinaryMessengerBinding`
- `plugin_platform_interface` provides: `MockPlatformInterfaceMixin`
- No mocking library (e.g., `mockito`) is needed — the mock platform is hand-written (2 lines), which is the standard plugin pattern

---

## Resolved Unknowns

| Unknown | Resolution |
|---------|-----------|
| How to mock method channels in modern Flutter | `TestDefaultBinaryMessengerBinding` API (not deprecated `setMockMethodCallHandler` on the channel) |
| How to test PlatformException propagation | Set handler to throw, use `throwsA(isA<PlatformException>())` |
| How to test malformed JSON | Set handler to return invalid string, use `throwsA(isA<FormatException>())` |
| How to mock platform interface | `MockPlatformInterfaceMixin` from existing dependency |
| Additional dependencies needed | None — all test infrastructure already present |
