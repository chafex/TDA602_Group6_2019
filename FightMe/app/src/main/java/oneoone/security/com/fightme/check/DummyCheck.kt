/**
 * Author: Felix
 */

package oneoone.security.com.fightme.check

import android.content.Context
import android.content.Intent
import android.os.SystemClock
import oneoone.security.com.fightme.CheckResult
import oneoone.security.com.fightme.ResultItem
import oneoone.security.com.fightme.SeverityLevel
import java.util.concurrent.TimeUnit

object DummyCheck : Checkable {

    /**
     * Just some arbitrary test messages and severity levels.
     */
    private val DUMMY_RESULTS = listOf(
        ResultItem("Fish", "Your device is housing a fish. *Splash* *Splash*", SeverityLevel.ACCEPTABLE),
        ResultItem("Empty wallet", "Can you really pay for that?", SeverityLevel.WARNING),
        ResultItem("Penalty Notice for Disorder", "Never have I seen a man less qualified for this task.", SeverityLevel.WARNING),
        ResultItem("Turtle extinction", "No kidding. Poof. No more turtle soup.", SeverityLevel.DANGEROUS),
        ResultItem("Where are you?", "I'm at soup.", SeverityLevel.DANGEROUS))

    override fun notifyResult(requestCode: Int, resultCode: Int, data: Intent) {
        // Ignore
    }

    /**
     * In this check we make sure the UI is able to deliver a new partial result
     * every second.
     */
    override fun performCheck(context: Context, testIndex: Int): CheckResult {
        SystemClock.sleep(TimeUnit.SECONDS.toMillis(1))
        return CheckResult(listOf(DUMMY_RESULTS[testIndex]), SeverityLevel.DANGEROUS)
    }

    override fun numberOfTests(): Int {
        return DUMMY_RESULTS.size
    }
}