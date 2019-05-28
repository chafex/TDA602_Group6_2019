/**
 * Author: Felix
 */

package oneoone.security.com.fightme.check

import android.Manifest
// import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
// import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import oneoone.security.com.fightme.CheckResult
import oneoone.security.com.fightme.ResultItem
import oneoone.security.com.fightme.SeverityLevel

object PermissionsCheck : Checkable {

    data class TestBlock(
        val permission: String,
        val name: String,
        val version: Int = Build.VERSION_CODES.KITKAT
    )

    // @SuppressLint("InlinedApi")
    private val NAMES = listOf(
        TestBlock(Manifest.permission.RECORD_AUDIO, "microphone"),
        TestBlock(Manifest.permission.READ_EXTERNAL_STORAGE, "read storage"),
        TestBlock(Manifest.permission.WRITE_EXTERNAL_STORAGE, "write storage"),
        TestBlock(Manifest.permission.BLUETOOTH, "bluetooth"),
        TestBlock(Manifest.permission.BLUETOOTH_PRIVILEGED, "privileged bluetooth"),
        TestBlock(Manifest.permission.BLUETOOTH_ADMIN, "admin bluetooth"),
        TestBlock(Manifest.permission.INTERNET, "internet"),
        TestBlock(Manifest.permission.ACCESS_FINE_LOCATION, "fine gps location"),
        TestBlock(Manifest.permission.ACCESS_COARSE_LOCATION, "coarse gps location"),
        TestBlock(Manifest.permission.ACCESS_NETWORK_STATE, "network state"),
        TestBlock(Manifest.permission.ACCOUNT_MANAGER, "account manager"),
        TestBlock(Manifest.permission.ANSWER_PHONE_CALLS, "answer phone calls", Build.VERSION_CODES.O),
        TestBlock(Manifest.permission.WRITE_VOICEMAIL, "write voice mail"),
        TestBlock(Manifest.permission.CALL_PHONE, "call phone"),
        TestBlock(Manifest.permission.CALL_PRIVILEGED, "privileged call phone"),
        TestBlock(Manifest.permission.CAPTURE_AUDIO_OUTPUT, "capture audio"),
        TestBlock(Manifest.permission.CAPTURE_SECURE_VIDEO_OUTPUT, "capture secure video"),
        TestBlock(Manifest.permission.CAPTURE_VIDEO_OUTPUT, "capture video"),
        TestBlock(Manifest.permission.SET_TIME, "set time"),
        TestBlock(Manifest.permission.SET_TIME_ZONE, "set time zone"),
        TestBlock(Manifest.permission.CAMERA, "camera"),
        TestBlock(Manifest.permission.GLOBAL_SEARCH, "global search")
    )

    override fun notifyResult(requestCode: Int, resultCode: Int, data: Intent) {
        // Ignore
    }

    /**
     * In this check we are basically brute forcing many of the possible permissions, checking if we have
     * access to them (without actually requesting runtime access).
     */
    override fun performCheck(context: Context, testIndex: Int): CheckResult {
        val block = NAMES[testIndex]
        val permissionName = block.name

        val accept = CheckResult(
            listOf(
                ResultItem(
                    "Unable to access $permissionName",
                    "The application was not able to exploit its install time permissions to get access to $permissionName, without the need of runtime consent.",
                    SeverityLevel.ACCEPTABLE
                )
            ), SeverityLevel.ACCEPTABLE
        )

        return if(Build.VERSION.SDK_INT >= block.version) {
            if (checkPermission(context, NAMES[testIndex].permission)) {
                CheckResult(
                    listOf(
                        ResultItem(
                            "Got access to $permissionName",
                            "The application was able to exploit its install time permissions to get access to $permissionName, without the need of runtime consent.",
                            SeverityLevel.DANGEROUS
                        )
                    ), SeverityLevel.DANGEROUS
                )
            } else {
                accept
            }
        } else {
            accept
        }
    }

    override fun numberOfTests(): Int {
        return NAMES.size
    }

    private fun checkPermission(context: Context, permissionString: String): Boolean {
        return ContextCompat.checkSelfPermission(context, permissionString) == PackageManager.PERMISSION_GRANTED
    }

    /* private fun requestPermission(context: Context, permissionStrings: Array<String>) {
     *    ActivityCompat.requestPermissions(context as Activity, permissionStrings, 0)
     * }
     */
}