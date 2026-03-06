package com.wsc.sim_card_info
import java.io.StringWriter
import android.util.JsonWriter
import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.telephony.SubscriptionManager
import android.telephony.TelephonyManager
import androidx.core.app.ActivityCompat
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.plugin.common.EventChannel.EventSink
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import io.flutter.plugin.common.PluginRegistry.RequestPermissionsResultListener

//import com.google.gson.Gson


/** SimCardInfoPlugin */
class SimCardInfoPlugin : FlutterPlugin, MethodCallHandler, ActivityAware,
    RequestPermissionsResultListener {
    /// The MethodChannel that will the communication between Flutter and native Android
    ///
    /// This local reference serves to register the plugin with the Flutter Engine and unregister it
    /// when the Flutter Engine is detached from the Activity
    private lateinit var context: Context
    private lateinit var channel: MethodChannel
    private var methodChannelName = "getSimInfo"

    private val result: Result? = null
    private val permissionEvent: EventSink? = null


    override fun onAttachedToEngine(flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
        context = flutterPluginBinding.applicationContext
        channel = MethodChannel(flutterPluginBinding.binaryMessenger, "sim_card_info")
        channel.setMethodCallHandler(this)
    }

    override fun onMethodCall(call: MethodCall, result: Result) {
        if (call.method == methodChannelName) {
            result.success(getSimInfo())
        } else {
            result.notImplemented()
        }
    }


    @SuppressLint("HardwareIds")
    private fun getSimInfo(): String {
        val simCardInfo = StringWriter()
        val writer = JsonWriter(simCardInfo)
        writer.beginArray()
        val telephonyManager =
            context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager?
        if (telephonyManager == null || ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.READ_PHONE_STATE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return ("Permission denied")
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
        // if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val subscriptionManager = context.getSystemService(SubscriptionManager::class.java)
            subscriptionManager?.activeSubscriptionInfoList?.let { subscriptionInfoList ->
                for (info in subscriptionInfoList) {
                    writer.beginObject()
                    writer.name("carrierName").value(info.carrierName.toString())
                    writer.name("displayName").value(info.displayName.toString())
                    writer.name("slotIndex").value(info.simSlotIndex.toString())

                    if (info?.number != null && info?.number != "") {
                        writer.name("number").value(info?.number.toString())
                    } else {
                        writer.name("number").value("")
                    }
                    writer.name("countryIso").value(info.countryIso.toString())
                    writer.name("countryPhonePrefix").value(info.countryIso.toString())
                    writer.endObject()

                }
                writer.endArray()

            }
        } else {
            writer.beginObject()
            writer.name("carrierName").value(telephonyManager.networkOperatorName.toString())
            writer.name("displayName").value(telephonyManager.simOperatorName.toString())
            writer.name("slotIndex").value(telephonyManager.simSerialNumber.toString())

            if (telephonyManager?.line1Number != null && telephonyManager?.line1Number.toString() != "") {
                writer.name("number").value(telephonyManager.line1Number.toString())
            } else {
                writer.name("number").value("")
            }
            writer.name("countryIso").value(telephonyManager.simCountryIso.toString())
            writer.name("countryPhonePrefix").value(telephonyManager.simCountryIso.toString())
            writer.endObject()
            writer.endArray()
        }
        println("simCardInfo mowne: $simCardInfo")
        return simCardInfo.toString()
    }


    override fun onDetachedFromEngine(binding: FlutterPlugin.FlutterPluginBinding) {
        channel.setMethodCallHandler(null)
    }

    override fun onAttachedToActivity(binding: ActivityPluginBinding) {
        context = binding.activity
        binding.addRequestPermissionsResultListener(this)
    }

    override fun onDetachedFromActivityForConfigChanges() {
        TODO("Not yet implemented")
    }

    override fun onReattachedToActivityForConfigChanges(binding: ActivityPluginBinding) {
        context = binding.activity
        binding.addRequestPermissionsResultListener(this)
    }

    override fun onDetachedFromActivity() {
        TODO("Not yet implemented")
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ): Boolean {
        // If request is cancelled, the result arrays are empty.
        if (requestCode == 0) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                permissionEvent?.success(true)
                getSimInfo()
                return true
            } else {
                permissionEvent?.success(false)
            }
        }
        result?.error("PERMISSION", "onRequestPermissionsResult is not granted", null)
        return false
    }

}

data class SimInfo(
    val carrierName: String = "",
    val displayName: String = "",
    val slotIndex: Int = 0,
    val number: String = "",
    val countryIso: String = "",
    val countryPhonePrefix: String = ""
) {}