/**
 * Author: Felix
 */
package oneoone.security.com.fightme

/**
 * The severity levels available for partial results.
 */
enum class SeverityLevel(val level: Int) {
    ACCEPTABLE(0),
    WARNING(1),
    DANGEROUS(2)
}

/**
 * A container class for check results.
 */
data class CheckResult(
    // A list of result items which should be show in the UI.
    val item: List<ResultItem>,
    // The accumulated severity of all listed items.
    val severity: SeverityLevel
)
