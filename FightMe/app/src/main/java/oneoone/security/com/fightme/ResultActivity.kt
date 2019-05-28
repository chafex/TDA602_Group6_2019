/**
 * Author: Felix
 */

package oneoone.security.com.fightme

import android.content.Intent
import android.graphics.drawable.Drawable
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import oneoone.security.com.fightme.check.CheckMapping
import oneoone.security.com.fightme.check.Checkable
import java.lang.IllegalArgumentException

/**
 * An activity for displaying check results. It launches the Checkable instance in
 * a background thread and subscribes to the partial results through a callback function.
 * The received results are displayed in a list.
 */
class ResultActivity : AppCompatActivity() {

    private lateinit var btnBack: Button
    private lateinit var btnDangerousOnly: Switch

    private lateinit var imgSeparator: ImageView
    private lateinit var statusProgress: ProgressBar
    private lateinit var statusText: TextView
    private lateinit var statusContainer: View

    private lateinit var recyclerViewHeader: TextView
    private lateinit var recyclerView: RecyclerView

    private var resultItems: List<ResultItem> = listOf() // TODO: Place this in a model view
    private var filteredItems: List<ResultItem> = resultItems
    private var showOnlyDangerous = false
    private var runningTask: CheckableTask? = null

    /**
     * Map a severity level to a drawable object.
     *
     * @severity - The severity level to be drawable.
     * @returns - A drawable representation of the severity level.
     */
    private fun severityToDrawable(severity: SeverityLevel): Drawable =
        when(severity) {
            SeverityLevel.ACCEPTABLE -> resources.getDrawable(R.color.color_acceptable, null)
            SeverityLevel.WARNING -> resources.getDrawable(R.color.color_warning, null)
            SeverityLevel.DANGEROUS-> resources.getDrawable(R.color.color_dangerous, null)
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)

        // Setup all listeners and launch the checkable thread.
        btnBack = findViewById(R.id.btn_back)
        btnDangerousOnly = findViewById(R.id.btn_only_dangerous)

        btnBack.setOnClickListener {
            onBackPressed()
        }
        btnDangerousOnly.setOnCheckedChangeListener { _, isChecked ->
            showOnlyDangerous = isChecked
            updateResultsList()
        }

        imgSeparator = findViewById(R.id.img_separator)
        statusProgress = findViewById(R.id.status_progress)
        statusText = findViewById(R.id.status_text)
        statusContainer = findViewById(R.id.status_container)

        recyclerView = findViewById<RecyclerView>(R.id.recyclerview_results).apply {
            layoutManager = LinearLayoutManager(this@ResultActivity)
            adapter = Adapter()
        }
        recyclerViewHeader = findViewById(R.id.recyclerview_header)

        // Map a Checkable tag to its corresponding Checkable instance.
        if(intent.hasExtra(Checkable.NAME)) {
            recyclerViewHeader.text = intent.getStringExtra(Checkable.NAME)

            val checkable = CheckMapping.map(CheckMapping.valueOf(intent.getStringExtra(Checkable.TASK)))
            launchCheckProcess(checkable)
        } else throw IllegalArgumentException("Expected Intent to specify the checkable to be used.")
    }

    /**
     * Will launch a checkable instance in a separate thread to perform
     * device vulnerability checks.
     *
     * @checkable - The checkable instance.
     */
    private fun launchCheckProcess(checkable: Checkable) {
        resultItems = listOf()
        updateResultsList()
        val task = CheckableTask {
            // Bind a callback function to the checkable thread and display partial results.
            resultItems = it.results
            // Test if the check is completely finished yet.
            if(it.progress == CheckableTask.FINISHED) {
                runningTask = null
                // Handle empty results.
                if(resultItems.isEmpty()) {
                    resultItems = listOf(
                        ResultItem("No results were gathered",
                            "This usually means everything is ok as far as this check category can tell.",
                            SeverityLevel.ACCEPTABLE)
                    )
                }
            }
            updateResultsList()
            updateStatusVisibility()
        }
        runningTask = task
        // Launch the checkable thread.
        task.execute(Pair(this, checkable))
        updateStatusVisibility()
    }

    /**
     * Updates the visibility of the animated status image overlay.
     */
    private fun updateStatusVisibility() {
        statusContainer.visibility = if(runningTask == null) View.GONE else View.VISIBLE
    }

    /**
     * After collecting partial results the results list and other
     * UI decorations are updated to summarise all aggregated results.
     */
    private fun updateResultsList() {
        var highestSeverity = SeverityLevel.ACCEPTABLE
        for(i in resultItems) {
            if(i.severity.level >highestSeverity.level ) {
                highestSeverity = i.severity
            }
        }
        imgSeparator.setImageDrawable(severityToDrawable(highestSeverity))
        filteredItems = resultItems.filter {
            ! showOnlyDangerous || it.severity == SeverityLevel.DANGEROUS
        }
        recyclerView.adapter?.notifyDataSetChanged()
    }

    /**
     * An adapter class to the activity recycler view containing all collected results.
     */
    private inner class Adapter: RecyclerView.Adapter<ResultItemViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResultItemViewHolder {
            return ResultItemViewHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.result_list_item,
                    parent,
                    false
                )
            )
        }

        override fun getItemCount(): Int {
            return filteredItems.size
        }

        override fun onBindViewHolder(holder: ResultItemViewHolder, position: Int) {
            val item = filteredItems[position]
            holder.headerText.text = item.header
            holder.bodyText.text = item.body
            holder.statusImage.setImageDrawable(severityToDrawable(item.severity))
        }
    }

    /**
     * A forward function to delegate activity results to any checkable instance.
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        runningTask?.notifyResult(requestCode, resultCode, data!!)
    }
}
