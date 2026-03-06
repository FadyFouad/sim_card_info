# Data Model: Package Test Suite

**Branch**: `001-package-tests` | **Date**: 2026-03-06

---

## Entities Under Test

### SimInfo

Represents a single physical SIM card slot and its associated network/carrier metadata.

| Field | Type | Source | Notes |
|-------|------|--------|-------|
| `carrierName` | `String` | Native JSON | Name of the carrier (e.g., "Vodafone") |
| `displayName` | `String` | Native JSON | Display name of the subscription |
| `slotIndex` | `String` | Native JSON (int) | Slot index, cast from `int` to `String` via `.toString()` |
| `number` | `String` | Native JSON | Phone number; may be empty string on devices that don't expose it |
| `countryIso` | `String` | Native JSON | ISO 3166-1 alpha-2 country code (e.g., "us", "eg") |
| `countryPhonePrefix` | `String` | Native JSON | Country dialing prefix (e.g., "1", "20") |

**Identity**: Two `SimInfo` instances are equal when all 6 fields are equal (via overridden `==` and `hashCode`).

**Validation rules**:
- All fields are required and non-nullable at the Dart level
- `slotIndex` from native is always an `int`; `fromJson` casts it to `String`
- No field length constraints are enforced at the model level

**State transitions**: None — `SimInfo` is an immutable value object (all fields `final`). Mutations go through `copyWith()` which returns a new instance.

---

## Test Fixture Data

### Fixture: Single SIM (happy path)

```json
{
  "carrierName": "Vodafone",
  "displayName": "Vodafone SIM 1",
  "slotIndex": 0,
  "number": "+201234567890",
  "countryIso": "eg",
  "countryPhonePrefix": "20"
}
```

Expected `SimInfo` after `fromJson`:
- `carrierName`: `"Vodafone"`
- `displayName`: `"Vodafone SIM 1"`
- `slotIndex`: `"0"` ← cast from int
- `number`: `"+201234567890"`
- `countryIso`: `"eg"`
- `countryPhonePrefix`: `"20"`

### Fixture: Multi-SIM (dual SIM device)

```json
[
  { "carrierName": "Vodafone", "displayName": "SIM 1", "slotIndex": 0, "number": "+201111111111", "countryIso": "eg", "countryPhonePrefix": "20" },
  { "carrierName": "Orange",   "displayName": "SIM 2", "slotIndex": 1, "number": "+201222222222", "countryIso": "eg", "countryPhonePrefix": "20" }
]
```

### Fixture: Error cases

| Scenario | Mock channel return value |
|----------|--------------------------|
| Null response | `null` |
| Empty array | `"[]"` |
| Malformed JSON | `"not-valid-json"` |
| PlatformException | throw `PlatformException(code: 'UNAVAILABLE')` |

---

## Key Relationships (Test Layer)

```
SimCardInfo (public API)
  └── delegates to → SimCardInfoPlatform.instance
                        ├── default: MethodChannelSimCardInfo
                        │     └── invokes → MethodChannel('sim_card_info')
                        │                       └── returns JSON → List<SimInfo>
                        └── test mock: MockSimCardInfoPlatform
                              └── returns → predefined List<SimInfo>
```

---

## No New Entities

This feature introduces no new data entities. The `SimInfo` model, platform interface, and method channel implementation already exist. The test suite exercises them without modifying their structure.
