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

private val isoToDialingCode = mapOf(
    "af" to "93", "al" to "355", "dz" to "213", "as" to "1-684", "ad" to "376",
    "ao" to "244", "ai" to "1-264", "aq" to "672", "ag" to "1-268", "ar" to "54",
    "am" to "374", "aw" to "297", "au" to "61", "at" to "43", "az" to "994",
    "bs" to "1-242", "bh" to "973", "bd" to "880", "bb" to "1-246", "by" to "375",
    "be" to "32", "bz" to "501", "bj" to "229", "bm" to "1-441", "bt" to "975",
    "bo" to "591", "ba" to "387", "bw" to "267", "bv" to "47", "br" to "55",
    "io" to "246", "vg" to "1-284", "bn" to "673", "bg" to "359", "bf" to "226",
    "bi" to "257", "kh" to "855", "cm" to "237", "ca" to "1", "cv" to "238",
    "ky" to "1-345", "cf" to "236", "td" to "235", "cl" to "56", "cn" to "86",
    "cx" to "61", "cc" to "61", "co" to "57", "km" to "269", "ck" to "682",
    "cr" to "506", "hr" to "385", "cu" to "53", "cy" to "357", "cz" to "420",
    "cd" to "243", "dk" to "45", "dj" to "253", "dm" to "1-767", "do" to "1-809, 1-829, 1-849",
    "ec" to "593", "eg" to "20", "sv" to "503", "gq" to "240", "er" to "291",
    "ee" to "372", "et" to "251", "fk" to "500", "fo" to "298", "fj" to "679",
    "fi" to "358", "fr" to "33", "gf" to "594", "pf" to "689", "tf" to "262",
    "ga" to "241", "gm" to "220", "ge" to "995", "de" to "49", "gh" to "233",
    "gi" to "350", "gr" to "30", "gl" to "299", "gd" to "1-473", "gp" to "590",
    "gu" to "1-671", "gt" to "502", "gg" to "44", "gn" to "224", "gw" to "245",
    "gy" to "592", "ht" to "509", "hm" to "672", "va" to "379", "hn" to "504",
    "hk" to "852", "hu" to "36", "is" to "354", "in" to "91", "id" to "62",
    "ir" to "98", "iq" to "964", "ie" to "353", "im" to "44", "il" to "972",
    "it" to "39", "ci" to "225", "jm" to "1-876", "jp" to "81", "je" to "44",
    "jo" to "962", "kz" to "7", "ke" to "254", "ki" to "686", "kp" to "850",
    "kr" to "82", "kw" to "965", "kg" to "996", "la" to "856", "lv" to "371",
    "lb" to "961", "ls" to "266", "lr" to "231", "ly" to "218", "li" to "423",
    "lt" to "370", "lu" to "352", "mo" to "853", "mk" to "389", "mg" to "261",
    "mw" to "265", "my" to "60", "mv" to "960", "ml" to "223", "mt" to "356",
    "mh" to "692", "mq" to "596", "mr" to "222", "mu" to "230", "yt" to "262",
    "mx" to "52", "fm" to "691", "md" to "373", "mc" to "377", "mn" to "976",
    "me" to "382", "ms" to "1-664", "ma" to "212", "mz" to "258", "mm" to "95",
    "na" to "264", "nr" to "674", "np" to "977", "nl" to "31", "an" to "599",
    "nc" to "687", "nz" to "64", "ni" to "505", "ne" to "227", "ng" to "234",
    "nu" to "683", "nf" to "672", "mp" to "1-670", "no" to "47", "om" to "968",
    "pk" to "92", "pw" to "680", "ps" to "970", "pa" to "507", "pg" to "675",
    "py" to "595", "pe" to "51", "ph" to "63", "pn" to "870", "pl" to "48",
    "pt" to "351", "pr" to "1-787, 1-939", "qa" to "974", "cg" to "242", "re" to "262",
    "ro" to "40", "ru" to "7", "rw" to "250", "sh" to "290", "kn" to "1-869",
    "lc" to "1-758", "pm" to "508", "vc" to "1-784", "ws" to "685", "sm" to "378",
    "st" to "239", "sa" to "966", "sn" to "221", "rs" to "381", "sc" to "248",
    "sl" to "232", "sg" to "65", "sk" to "421", "si" to "386", "sb" to "677",
    "so" to "252", "za" to "27", "gs" to "500", "es" to "34", "lk" to "94",
    "sd" to "249", "sr" to "597", "sj" to "47", "sz" to "268", "se" to "46",
    "ch" to "41", "sy" to "963", "tw" to "886", "tj" to "992", "tz" to "255",
    "th" to "66", "tg" to "228", "tk" to "690", "to" to "676", "tt" to "1-868",
    "tn" to "216", "tr" to "90", "tm" to "993", "tc" to "1-649", "tv" to "688",
    "ug" to "256", "ua" to "380", "ae" to "971", "gb" to "44", "us" to "1",
    "vi" to "1-340", "uy" to "598", "uz" to "998", "vu" to "678", "ve" to "58",
    "vn" to "84", "wf" to "681", "eh" to "212", "ye" to "967", "zm" to "260",
    "zw" to "263"
)

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

        val hasPhoneStatePermission = ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.READ_PHONE_STATE
        ) == PackageManager.PERMISSION_GRANTED

        if (telephonyManager == null || !hasPhoneStatePermission) {
            writer.endArray()
            return simCardInfo.toString()
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val subscriptionManager = context.getSystemService(SubscriptionManager::class.java)
            val subscriptionInfoList = subscriptionManager?.activeSubscriptionInfoList

            if (subscriptionInfoList != null) {
                for (info in subscriptionInfoList) {
                    writer.beginObject()
                    writer.name("carrierName").value(info.carrierName.toString())
                    writer.name("displayName").value(info.displayName.toString())
                    writer.name("slotIndex").value(info.simSlotIndex.toString())

                    var number = ""
                    val hasPhoneNumberPermission =
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                            ActivityCompat.checkSelfPermission(
                                context,
                                Manifest.permission.READ_PHONE_NUMBERS
                            ) == PackageManager.PERMISSION_GRANTED
                    } else {
                            true
                    }

                    if (hasPhoneNumberPermission) {
                        number = info.number?.takeIf { it.isNotEmpty() } ?: ""
                    }

                    writer.name("number").value(number)
                    val countryIso = info.countryIso?.lowercase() ?: ""
                    writer.name("countryIso").value(countryIso)
                    writer.name("countryPhonePrefix").value(isoToDialingCode[countryIso] ?: "")
                    writer.endObject()
                }
            }
        } else {
            writer.beginObject()
            writer.name("carrierName").value(telephonyManager.networkOperatorName.toString())
            writer.name("displayName").value(telephonyManager.simOperatorName.toString())
            writer.name("slotIndex").value(telephonyManager.simSerialNumber.toString())

            val number = telephonyManager.line1Number?.takeIf { it.isNotEmpty() } ?: ""
            writer.name("number").value(number)

            val countryIso = telephonyManager.simCountryIso?.lowercase() ?: ""
            writer.name("countryIso").value(countryIso)
            writer.name("countryPhonePrefix").value(isoToDialingCode[countryIso] ?: "")
            writer.endObject()
        }
        writer.endArray()
        println("simCardInfo mowne: ")
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
        // No-op
    }

    override fun onReattachedToActivityForConfigChanges(binding: ActivityPluginBinding) {
        context = binding.activity
        binding.addRequestPermissionsResultListener(this)
    }

    override fun onDetachedFromActivity() {
        // No-op
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
