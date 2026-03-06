import 'package:flutter_test/flutter_test.dart';
import 'package:plugin_platform_interface/plugin_platform_interface.dart';
import 'package:sim_card_info/sim_card_info.dart';
import 'package:sim_card_info/sim_card_info_method_channel.dart';
import 'package:sim_card_info/sim_card_info_platform_interface.dart';
import 'package:sim_card_info/sim_info.dart';

class MockSimCardInfoPlatform
    with MockPlatformInterfaceMixin
    implements SimCardInfoPlatform {
  @override
  Future<List<SimInfo>?> getSimInfo() => Future.value([
        SimInfo(
          carrierName: 'MockCarrier',
          displayName: 'MockSIM',
          slotIndex: '0',
          number: '123',
          countryIso: 'us',
          countryPhonePrefix: '1',
        )
      ]);
}

void main() {
  group('SimCardInfoPlatform', () {
    test('default instance is MethodChannelSimCardInfo', () {
      expect(SimCardInfoPlatform.instance, isA<MethodChannelSimCardInfo>());
    });
  });

  group('SimCardInfo delegation', () {
    test('getSimInfo delegates to platform instance', () async {
      final simCardInfo = SimCardInfo();
      final mockPlatform = MockSimCardInfoPlatform();
      SimCardInfoPlatform.instance = mockPlatform;

      final result = await simCardInfo.getSimInfo();
      expect(result, isNotNull);
      expect(result!.length, 1);
      expect(result[0].carrierName, 'MockCarrier');
    });
  });
}
