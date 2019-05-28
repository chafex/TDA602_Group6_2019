/**
 * Author: Felix
 */

package oneoone.security.com.fightme.check

import android.content.Context
import android.content.Intent
import oneoone.security.com.fightme.CheckResult
import oneoone.security.com.fightme.ResultItem
import oneoone.security.com.fightme.SeverityLevel

object EmptyDummyCheck : Checkable {

    private val DUMMY_RESULTS: List<ResultItem> = listOf()

    override fun notifyResult(requestCode: Int, resultCode: Int, data: Intent) {
        // Ignore
    }

    override fun performCheck(context: Context, testIndex: Int): CheckResult {
        return CheckResult(DUMMY_RESULTS, SeverityLevel.ACCEPTABLE)
    }

    override fun numberOfTests(): Int {
        return 1
    }
}