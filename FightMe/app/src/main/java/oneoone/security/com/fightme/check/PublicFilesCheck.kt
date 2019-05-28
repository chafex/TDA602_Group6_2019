/**
 * Author: Felix
 */

package oneoone.security.com.fightme.check

import android.content.Context
import android.content.Intent
import oneoone.security.com.fightme.CheckResult
import oneoone.security.com.fightme.ResultItem
import oneoone.security.com.fightme.SeverityLevel
import java.io.File
import java.io.IOException

object PublicFilesCheck : Checkable {

    override fun notifyResult(requestCode: Int, resultCode: Int, data: Intent) {
        // Ignore
    }

    override fun performCheck(context: Context, testIndex: Int): CheckResult {
        var foundProcesses = 0
        try {
            // Attempt to access the "proc" folder, where system logs can be found.
            val root = File("/proc")
            if(root.isDirectory) {
                // Attempt to find all visible UID folders (they are simply just folders with process numbers as names).
                for(f in root.listFiles().filter { it.isDirectory }) {
                    try {
                        Integer.valueOf(f.name ?: "")
                        foundProcesses ++
                    } catch(ignore: RuntimeException) { }
                }
            } else {
                throw Exception("Could not find 'proc' directory.")
            }
        } catch(ignore: IOException) {
            ignore.printStackTrace()
            throw Exception("Failed to search public files.")
        }
        // Make sure that we only found our own process folder, otherwise the attacker would have access to other
        // processes log data and an attack could be carried out.
        return when(foundProcesses) {
            0 -> {
                CheckResult(listOf(ResultItem("Unsupported device", "This check did not produce any reliable result, which is often the case when the file structure on a device is not supported by the checker application. Your device could be affected by this vulnerability; however, it is not possible for this check to determine this.", SeverityLevel.WARNING)), SeverityLevel.WARNING)
            }
            1 ->
                CheckResult(listOf(
                    ResultItem("Process was isolated", "This device controls access to some statistical files on the device, which makes it harder for malicious applications to extract private information.", SeverityLevel.ACCEPTABLE)
                ), SeverityLevel.ACCEPTABLE)
            else ->
                CheckResult(listOf(
                    ResultItem("Could access other processes", "It was possible to this application to see and extract statistics from other applications running on this device. This can allow applications to infer private information (app usage such as medical data and geographic location) without any user input. This issue is not present in updated versions of Android.", SeverityLevel.DANGEROUS)
                ), SeverityLevel.DANGEROUS)
        }
    }

    override fun numberOfTests(): Int {
        return 1
    }
}