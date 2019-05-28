/**
 * Author: Felix
 */

package oneoone.security.com.fightme

import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import oneoone.security.com.fightme.check.Checkable

/**
 * Container class for storing check progress
 * feedback when performing checks asynchronously.
 */
data class ProgressData (
    // A list of partial results items to be displayed to
    // the user in the UI.
    val results: List<ResultItem>,
    // A progress value from 1 to 100.
    val progress: Int
)

/**
 * An implementation of an asynchronous execution of check tests. This
 * will be run on a separate thread and will terminate once all partial
 * check results have been returned.
 *
 * @callback - A callback function which will be triggered and run on the UI thread
 *             once a partial result is available.
 */
class CheckableTask(
    private val callback: (ProgressData) -> Unit
): AsyncTask<Pair<Context, Checkable>, ProgressData, List<ResultItem>>() {

    // List of all collected results.
    private val results: MutableList<ResultItem> = mutableListOf()
    // The current progress status, ranging from START to FINISHED.
    private var progress = START
    // Holding variable for the currently executed check.
    private var currentCheck: Checkable? = null

    companion object {
        // Progress starting value.
        const val START = 0
        // Progress ending value.
        const val FINISHED = 100
    }

    /**
     * Called by an external activity to send activity results back
     * to the currently running check instance. This functionality
     * is only used if the check requires communication with external
     * applications.
     *
     * @requestCode - The result request code.
     * @resultCode - The result status code.
     * @data - The result intent.
     */
    fun notifyResult(requestCode: Int, resultCode: Int, data: Intent) {
        currentCheck?.notifyResult(requestCode, resultCode, data)
    }

    /**
     * Interface implementation of an asynchronous task executing the checks in a
     * separate thread. It will take a set of <context, checkable> pairs and execute
     * that checkable in the bundled context.
     *
     * @params - Variable list of context, checkable pairs.
     *
     * @return - A list of all accumulated results.
     */
    override fun doInBackground(vararg params: Pair<Context, Checkable>?): List<ResultItem>? {
        var testsDone = 0
        var maxNumTests = 0
        // Calculate the number of sub steps needed.
        for(pair in params) {
            if (pair!= null) {
                val check = pair.second
                maxNumTests += check.numberOfTests()
            }
        }
        // Execute the checks sequentially.
        for(pair in params) {
            if(pair != null) {
                val context = pair.first
                val check = pair.second
                val numTests = check.numberOfTests()
                for(i in 0 until numTests) {
                    currentCheck = check
                    try {
                        publishPartialCheckResult(check.performCheck(context, i), testsDone + i, maxNumTests)
                    } catch (ignore: Exception) {
                        publishError(ignore)
                    } finally {
                        currentCheck = null
                    }
                }
                testsDone += numTests
            } else {
                publishError(null)
            }
        }
        return results
    }

    /**
     * Trigger an update callback to the provided callback function on the UI thread.
     */
    override fun onProgressUpdate(vararg values: ProgressData?) {
        super.onProgressUpdate(*values)
        for (i in values) {
            if(i != null) {
                callback(i)
            }
        }
    }

    /**
     * Return the resulting list.
     */
    override fun onPostExecute(result: List<ResultItem>?) {
        progress = FINISHED
        sendProgress()
    }

    /**
     * Send progress updates through the callback function. The results
     * are not necessarily in order, the index values can come arbitrarily.
     *
     * @part - The partial result.
     * @at - The current part index.
     * @of - The total number of expected parts.
     */
    private fun publishPartialCheckResult(part: CheckResult, at: Int, of: Int) {
        results.addAll(0, part.item)
        progress = at / of
        sendProgress()
    }

    /**
     * Trigger an error in the callback function and send as a partial result.
     */
    private fun publishError(exception: Exception?) {
        exception?.printStackTrace()
        results.add(ResultItem("An error occurred!",
            "Woops!? Seems as if we have some security problems of our own. Something caused the security check to crash mid-way through the tests. If you want to support the application's development and help improving as well, please contact the developers about this error.",
            SeverityLevel.WARNING))
    }

    /**
     * Send the current state to the supervising instance using the bound callback function.
     */
    private fun sendProgress() {
        publishProgress(ProgressData(results, progress))
    }
}
