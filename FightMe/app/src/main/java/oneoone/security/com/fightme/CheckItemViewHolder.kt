/**
 * Author: Felix
 */

package oneoone.security.com.fightme

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.Button
import android.widget.TextView

/**
 * A view holder used in the ResultActivity to bind check results to
 * view items.
 */
class CheckItemViewHolder(view: View): RecyclerView.ViewHolder(view) {
    var headerText: TextView = view.findViewById(R.id.check_header)
    var bodyText: TextView = view.findViewById(R.id.check_body)
    var showButton: Button = view.findViewById(R.id.check_show_btn)
}
