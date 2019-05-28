/**
 * Author: Felix
 */

package oneoone.security.com.fightme.check

import android.content.Context
import android.content.Intent
import oneoone.security.com.fightme.CheckResult

/**
 * An interface for performing asynchronous vulnerability checks. Any instance of
 * this interface will be able to execute partial steps in any implemented attack
 * and terminate at any point, as well as giving real time results and feedback.
 */
interface Checkable {

    companion object {
        const val RUN_CHECKABLE = "RUN_CHECKABLE"
        const val NAME = "NAME"
        const val DESCRIPTION = "DESCRIPTION"
        const val TASK = "TASK"
    }

    /**
     * Performs a step in a vulnerability check.
     *
     * @context - The context to perform the check in.
     * @textIndex - The test step to execute, it is expected that at least all
     *              previous step indexes have been run prior to the current value.
     * @returns - The step result.
     */
    fun performCheck(context: Context, testIndex: Int): CheckResult

    /**
     * @returns - The total number of steps in the check. Must be constant across all calls.
     */
    fun numberOfTests(): Int

    /**
     * A forward method to notify the check that the supervising activity has
     * received an activity result.
     *
     * @requestCode - The result request code.
     * @resultCode - The result status code.
     * @data - The result intent.
     */
    fun notifyResult(requestCode: Int, resultCode: Int, data: Intent)
}