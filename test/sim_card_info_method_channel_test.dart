import 'dart:convert';
import 'package:flutter/services.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:sim_card_info/sim_card_info_method_channel.dart';

void main() {
  TestWidgetsFlutterBinding.ensureInitialized();

  group('MethodChannelSimCardInfo', () {
    final platform = MethodChannelSimCardInfo();
    const channel = MethodChannel('sim_card_info');

    tearDown(() {
      TestDefaultBinaryMessengerBinding.instance.defaultBinaryMessenger
          .setMockMethodCallHandler(channel, null);
    });

    test('getSimInfo returns valid single-SIM list', () async {
      const fixture = {
        'carrierName': 'Vodafone',
        'displayName': 'SIM 1',
        'slotIndex': 0,
        'number': '+201234567890',
        'countryIso': 'eg',
        'countryPhonePrefix': '20',
      };

      TestDefaultBinaryMessengerBinding.instance.defaultBinaryMessenger
          .setMockMethodCallHandler(channel, (methodCall) async {
        return json.encode([fixture]);
      });

      final result = await platform.getSimInfo();
      expect(result, isNotNull);
      expect(result!.length, 1);
      expect(result[0].carrierName, 'Vodafone');
      expect(result[0].slotIndex, '0');
    });

    test('getSimInfo returns null when channel returns null', () async {
      TestDefaultBinaryMessengerBinding.instance.defaultBinaryMessenger
          .setMockMethodCallHandler(channel, (methodCall) async {
        return null;
      });

      final result = await platform.getSimInfo();
      expect(result, isNull);
    });

    test('getSimInfo returns empty list for empty array JSON', () async {
      TestDefaultBinaryMessengerBinding.instance.defaultBinaryMessenger
          .setMockMethodCallHandler(channel, (methodCall) async {
        return '[]';
      });

      final result = await platform.getSimInfo();
      expect(result, isEmpty);
    });

    test('getSimInfo returns multi-SIM list', () async {
      final fixtures = [
        {
          'carrierName': 'Vodafone',
          'displayName': 'SIM 1',
          'slotIndex': 0,
          'number': '+20111',
          'countryIso': 'eg',
          'countryPhonePrefix': '20',
        },
        {
          'carrierName': 'Orange',
          'displayName': 'SIM 2',
          'slotIndex': 1,
          'number': '+20222',
          'countryIso': 'eg',
          'countryPhonePrefix': '20',
        },
      ];

      TestDefaultBinaryMessengerBinding.instance.defaultBinaryMessenger
          .setMockMethodCallHandler(channel, (methodCall) async {
        return json.encode(fixtures);
      });

      final result = await platform.getSimInfo();
      expect(result!.length, 2);
      expect(result[0].carrierName, 'Vodafone');
      expect(result[1].carrierName, 'Orange');
    });

    test('getSimInfo propagates PlatformException', () async {
      TestDefaultBinaryMessengerBinding.instance.defaultBinaryMessenger
          .setMockMethodCallHandler(channel, (methodCall) async {
        throw PlatformException(code: 'UNAVAILABLE');
      });

      expect(() => platform.getSimInfo(), throwsA(isA<PlatformException>()));
    });

    test('getSimInfo throws FormatException on malformed JSON', () async {
      TestDefaultBinaryMessengerBinding.instance.defaultBinaryMessenger
          .setMockMethodCallHandler(channel, (methodCall) async {
        return 'not-json';
      });

      expect(() => platform.getSimInfo(), throwsA(isA<FormatException>()));
    });
  });
}
