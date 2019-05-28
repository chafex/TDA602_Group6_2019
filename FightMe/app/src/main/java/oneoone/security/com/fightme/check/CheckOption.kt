/**
 * Author: Felix
 */

package oneoone.security.com.fightme.check

/**
 * An interface to map a Checkable to UI elements.
 */
interface CheckOption {
    fun getName(): String
    fun getBody(): String
    fun getBrief(): String
    fun isAvailable(): Boolean
    fun getCheckable(): CheckMapping
}
