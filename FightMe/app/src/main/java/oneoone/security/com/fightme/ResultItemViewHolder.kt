/**
 * Author: Felix
 */

package oneoone.security.com.fightme

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.TextView

/**
 * A view holder class for connecting result view items to their
 * container counterpart, ResultItem.
 */
class ResultItemViewHolder(view: View): RecyclerView.ViewHolder(view) {

    var headerText: TextView = view.findViewById(R.id.result_header)
    var bodyText: TextView = view.findViewById(R.id.result_body)
    var statusImage: ImageView = view.findViewById(R.id.result_image)

}