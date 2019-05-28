/**
 * Author: Felix
 */

package oneoone.security.com.fightme

/**
 * Container class for check results to be
 * displayed in the UI.
 */
data class ResultItem(
    val header: String,
    val body: String,
    val severity: SeverityLevel
)