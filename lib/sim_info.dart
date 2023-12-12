

/*
╔═══════════════════════════════════════════════════╗
║ Created by Fady Fouad on 12/12/2023 at 3:29 PM.   ║
║═══════════════════════════════════════════════════║
║ fady.fouad.a@gmail.com.                           ║
╚═══════════════════════════════════════════════════╝
*/

class SimInfo {
  final String carrierName;
  final String displayName;
  final String slotIndex;
  final String number;
  final String countryIso;
  final String countryPhonePrefix;

  SimInfo({
    required this.carrierName,
    required this.displayName,
    required this.slotIndex,
    required this.number,
    required this.countryIso,
    required this.countryPhonePrefix,
  });

  factory SimInfo.fromJson(Map<String, dynamic> json) {
    return SimInfo(
      carrierName: json['carrierName'],
      displayName: json['displayName'],
      slotIndex: json['slotIndex'].toString(),
      number: json['number'],
      countryIso: json['countryIso'],
      countryPhonePrefix: json['countryPhonePrefix'],
    );
  }

  @override
  String toString() {
    return 'SimInfo{carrierName: $carrierName, displayName: $displayName, slotIndex: $slotIndex, number: $number, countryIso: $countryIso, countryPhonePrefix: $countryPhonePrefix}\n';
  }

  @override
  bool operator ==(Object other) =>
      identical(this, other) ||
      other is SimInfo &&
          runtimeType == other.runtimeType &&
          carrierName == other.carrierName &&
          displayName == other.displayName &&
          slotIndex == other.slotIndex &&
          number == other.number &&
          countryIso == other.countryIso &&
          countryPhonePrefix == other.countryPhonePrefix;

  @override
  int get hashCode =>
      carrierName.hashCode ^
      displayName.hashCode ^
      slotIndex.hashCode ^
      number.hashCode ^
      countryIso.hashCode ^
      countryPhonePrefix.hashCode;
}
