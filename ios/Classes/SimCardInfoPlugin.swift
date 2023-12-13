import Flutter
import UIKit

public class SimCardInfoPlugin: NSObject, FlutterPlugin {
  public static func register(with registrar: FlutterPluginRegistrar) {
    let channel = FlutterMethodChannel(name: "sim_card_info", binaryMessenger: registrar.messenger())
    let instance = SimCardInfoPlugin()
    registrar.addMethodCallDelegate(instance, channel: channel)
  }

  public func handle(_ call: FlutterMethodCall, result: @escaping FlutterResult) {
    switch call.method {
    case "getSimInfo":
      result(FlutterMethodNotImplemented)

//       result("[{" +
//             "\"carrierName\": \"carrierName\"," +
//             "\"displayName\": \"displayName\"," +
//             "\"slotIndex\": \"simSlotIndex\"," +
//             "\"number\": \"number\"," +
//             "\"countryIso\": \"countryIso\"," +
//             "\"countryPhonePrefix\": \"countryIso\"" +
//             "}]"
//       )

    default:
      result(FlutterMethodNotImplemented)
    }
  }
}
