/**
 * Author: Felix
 */

package oneoone.security.com.fightme.check

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.SystemClock
import android.util.Log
import oneoone.security.com.fightme.CheckResult
import oneoone.security.com.fightme.ResultItem
import oneoone.security.com.fightme.SeverityLevel
import java.util.concurrent.TimeUnit

object DelegationCheck : Checkable {

    const val REQUEST_CODE = 1337

    private var gotResult = false
    private var resultSuccess = false

    override fun notifyResult(requestCode: Int, resultCode: Int, data: Intent) {
        if(requestCode == REQUEST_CODE) {
            resultSuccess = resultCode == Activity.RESULT_OK
            gotResult = true
        }
    }

    override fun performCheck(context: Context, testIndex: Int): CheckResult {
        val failure = CheckResult(listOf(
            ResultItem(
                "Unable to exploit external application",
                "The application was unable to exploit permissions granted to another application through intents.",
                SeverityLevel.ACCEPTABLE
            )
        ), SeverityLevel.ACCEPTABLE)
        val success = CheckResult(listOf(
            ResultItem(
                "Succeeded in exploiting external application",
                "The application was able to exploit permissions granted to another application through intents. (Though, it could be that the companion application requires permissions to be granted during runtime, which is required in order for this test to yield reliable results)",
                SeverityLevel.DANGEROUS
            )
        ), SeverityLevel.DANGEROUS)
        val timeout = CheckResult(listOf(
            ResultItem(
                "Timeout reached",
                "The test took too long to perform, a timeout was reached. It is not possible to say weather this was due to security restrictions or not.",
                SeverityLevel.WARNING
            )
        ), SeverityLevel.WARNING)
        val missingCompanion = CheckResult(listOf(
            ResultItem(
                "Missing companion application",
                "It is not possible to perform this test as the necessary companion application is not installed.",
                SeverityLevel.WARNING
            )
        ), SeverityLevel.WARNING)
        when(testIndex) {
            0 -> {
                gotResult = false
                resultSuccess = false
                val receiver =  object : BroadcastReceiver() {
                    override fun onReceive(context: Context?, intent: Intent?) {
                        resultSuccess = intent?.hasExtra("Success") ?: false
                        gotResult = true
                        Log.d("FALLOUT", "Got response!")
                    }
                }
                context.registerReceiver(receiver, IntentFilter("AccessStorageOK"))
                val intent = Intent()
                intent.action = "AccessStorage"
                SystemClock.sleep(1000)
                context.sendBroadcast(intent)
                val currentTime = System.currentTimeMillis()
                while (!gotResult) {
                    SystemClock.sleep(100)
                    if (System.currentTimeMillis() > currentTime + TimeUnit.SECONDS.toMillis(5)) {
                        context.unregisterReceiver(receiver)
                        return timeout
                    }
                }
                context.unregisterReceiver(receiver)
                return if (resultSuccess) {
                    success
                } else {
                    failure
                }
            }
        }
        return CheckResult(listOf(), SeverityLevel.ACCEPTABLE)
    }

    override fun numberOfTests(): Int {
        return 1
    }
}