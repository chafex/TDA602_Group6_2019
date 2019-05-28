/**
 * Author: Shahnur
 */

package oneoone.security.com.fightme.check

import android.content.Context
import oneoone.security.com.fightme.CheckResult
import oneoone.security.com.fightme.ResultItem
import oneoone.security.com.fightme.SeverityLevel
import android.content.Intent



object NetworkCheck : Checkable {
    override fun notifyResult(requestCode: Int, resultCode: Int, data: Intent) {
        // TODO
    }

    val SUCCESS_RESULTS = listOf(
        ResultItem("Network", "Was able to send file over network.", SeverityLevel.ACCEPTABLE)
    )

    val FAILURE_RESULTS = listOf(
        ResultItem("Network", "Was not able to send file over network.", SeverityLevel.ACCEPTABLE)
    )



    override fun performCheck(context: Context, testIndex: Int): CheckResult {
        // TODO
        return CheckResult(listOf(SUCCESS_RESULTS[0]), SeverityLevel.DANGEROUS)

    }

    override fun numberOfTests(): Int {
        return 1
    }
}