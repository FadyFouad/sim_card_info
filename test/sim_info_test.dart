import 'package:flutter_test/flutter_test.dart';
import 'package:sim_card_info/sim_info.dart';

void main() {
  group('SimInfo', () {
    const Map<String, dynamic> fixture = {
      'carrierName': 'Vodafone',
      'displayName': 'Vodafone SIM 1',
      'slotIndex': 0,
      'number': '+201234567890',
      'countryIso': 'eg',
      'countryPhonePrefix': '20',
    };

    test('fromJson creates correct instance', () {
      final sim = SimInfo.fromJson(fixture);
      expect(sim.carrierName, 'Vodafone');
      expect(sim.displayName, 'Vodafone SIM 1');
      expect(sim.slotIndex, '0');
      expect(sim.number, '+201234567890');
      expect(sim.countryIso, 'eg');
      expect(sim.countryPhonePrefix, '20');
    });

    test('slotIndex is cast from int to String', () {
      final sim = SimInfo.fromJson(fixture);
      expect(sim.slotIndex, isA<String>());
      expect(sim.slotIndex, '0');
    });

    test('toJson round-trip fidelity', () {
      final sim = SimInfo.fromJson(fixture);
      final json = sim.toJson();
      final sim2 = SimInfo.fromJson(json);
      expect(sim2, sim);
    });

    test('equal instances are ==', () {
      final sim1 = SimInfo.fromJson(fixture);
      final sim2 = SimInfo.fromJson(fixture);
      expect(sim1, sim2);
    });

    test('differing instances are !=', () {
      final sim1 = SimInfo.fromJson(fixture);
      final sim3 = sim1.copyWith(carrierName: 'Other');
      expect(sim1, isNot(sim3));
    });

    test('equal instances share hashCode', () {
      final sim1 = SimInfo.fromJson(fixture);
      final sim2 = SimInfo.fromJson(fixture);
      expect(sim1.hashCode, sim2.hashCode);
    });

    test('copyWith overrides specified fields', () {
      final sim = SimInfo.fromJson(fixture);
      final sim2 = sim.copyWith(carrierName: 'Orange', slotIndex: '1');
      
      expect(sim2.carrierName, 'Orange');
      expect(sim2.slotIndex, '1');
      expect(sim2.displayName, sim.displayName);
      expect(sim2.number, sim.number);
      expect(sim2.countryIso, sim.countryIso);
      expect(sim2.countryPhonePrefix, sim.countryPhonePrefix);
    });

    test('toString contains all fields', () {
      final sim = SimInfo.fromJson(fixture);
      final str = sim.toString();
      expect(str, contains('carrierName: Vodafone'));
      expect(str, contains('displayName: Vodafone SIM 1'));
      expect(str, contains('slotIndex: 0'));
      expect(str, contains('number: +201234567890'));
      expect(str, contains('countryIso: eg'));
      expect(str, contains('countryPhonePrefix: 20'));
    });
  });
}
