/**
 * Author: Felix
 */

package oneoone.security.com.fightme

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.SystemClock
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import oneoone.security.com.fightme.check.*
import java.util.concurrent.TimeUnit

/**
 * The main activity class, which acts a hub of all available
 * checks that can be performed on the device. The user is allowed
 * to select one test class and will then be redirected to the
 * ResultActivity.
 */
class MainActivity : AppCompatActivity() {

    private lateinit var btnRun: Button
    private lateinit var textHeader: TextView
    private lateinit var textBody: TextView
    private lateinit var recyclerView
            : RecyclerView

    private var selectedCheckOption: CheckOption? = null

    /**
     * A list of all available test classes and their respective
     * Checkable implementation.
     */
    private val checkOptionsList: List<CheckOption> = listOf(
        object : CheckOption {
            override fun getName(): String {
                return "Dummy Check"
            }

            override fun getBody(): String {
                return "This is a dummy check which will return some pre-configured items. This check does not tell you anything about the security of your device, instead it is used to debug the application itself. You can try to run it if you want to."
            }

            override fun getBrief(): String {
                return "Used for testing. A dummy check with returns some pre-configured results."
            }

            override fun isAvailable(): Boolean {
                return true
            }

            override fun getCheckable(): CheckMapping {
                return CheckMapping.DUMMY_CHECK
            }

        },
        object : CheckOption {
            override fun getName(): String {
                return "Empty Dummy Check"
            }

            override fun getBody(): String {
                return "A dummy check which will not return any results. This check does not tell you anything about the security of your device, instead it is used to debug the application itself. You can try to run it if you want to."
            }

            override fun getBrief(): String {
                return "Used for testing. A dummy check with returns no results."
            }

            override fun isAvailable(): Boolean {
                return true
            }

            override fun getCheckable(): CheckMapping {
                return CheckMapping.EMPTY_DUMMY_CHECK
            }

        },
        object : CheckOption {
            override fun getName(): String {
                return "Public files access"
            }

            override fun getBody(): String {
                return "By running this check you will scan your device for possible leaky public files, which can be accessed by any application and could leak private or sensitive data to an malicious application."
            }

            override fun getBrief(): String {
                return "Will attempt to access sensitive public files."
            }

            override fun isAvailable(): Boolean {
                return true
            }

            override fun getCheckable(): CheckMapping {
                return CheckMapping.PUBLIC_FILES_CHECK
            }

        },
        object : CheckOption {
            override fun getName(): String {
                return "Permission check"
            }

            override fun getBody(): String {
                return "This check tries to aquire permissions without requesting access during runtime, intead using those granted during its installation. Only having a install time check makes the application's use less transparent with its intentions."
            }

            override fun getBrief(): String {
                return "Will try to access various permissions."
            }

            override fun isAvailable(): Boolean {
                return true
            }

            override fun getCheckable(): CheckMapping {
                return CheckMapping.PERMISSIONS_CHECK
            }
        },

        object : CheckOption {
            override fun getName(): String {
                return "Permission delegation"
            }

            override fun getBody(): String {
                return "This check will test for intent delegation attacks, where an application without permission can trick a privileged application with some permissions to leak sensitive data."
            }

            override fun getBrief(): String {
                return "Tries to extract restricted data using other applications."
            }

            override fun isAvailable(): Boolean {
                return true
            }

            override fun getCheckable(): CheckMapping {
                return CheckMapping.DELEGATION_CHECK
            }
        },
        object : CheckOption {
            override fun getName(): String {
                return "Microphone"
            }

            override fun getBody(): String {
                return "Scanning the microphone permission"
            }

            override fun getBrief(): String {
                return "Microphone permission"
            }

            override fun isAvailable(): Boolean {
                return true
            }

            override fun getCheckable(): CheckMapping {
                return CheckMapping.MICROPHONE_CHECK
            }
        },
        object : CheckOption {
            override fun getName(): String {
                return "Network (unimplemented)"
            }

            override fun getBody(): String {
                return "Scanning the network permission (unimplemented)"
            }

            override fun getBrief(): String {
                return "Network permission (unimplemented)"
            }

            override fun isAvailable(): Boolean {
                return true
            }

            override fun getCheckable(): CheckMapping {
                return CheckMapping.NETWORK_CHECK
            }

        }
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Bind
        btnRun = findViewById(R.id.run_btn)
        textHeader = findViewById(R.id.check_header)
        textBody = findViewById(R.id.check_body)
        val myAdapter = Adapter()
        recyclerView = findViewById<RecyclerView>(R.id.recyclerview_checks).apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = myAdapter
        }

        setSelected(null)
    }

    /**
     * A managing function for updating the UI content depending on
     * what test class is selected.
     *
     * @item - The selected check option.
     */
    private fun setSelected(item: CheckOption?) {
        if (item == null) {
            btnRun.visibility = View.GONE
            textHeader.text = "Select check"
            textBody.text =
                "Please select a check from the list below. Press 'Show' to display more information and to enable the option to run the selected check."
            btnRun.setOnClickListener(null)
        } else {
            textHeader.text = item.getName()
            textBody.text = item.getBody()
            if (item.isAvailable()) {
                btnRun.visibility = View.VISIBLE
            } else {
                btnRun.visibility = View.GONE
            }
            // Bind a listener to the run button to launch the result activity.
            btnRun.setOnClickListener {
                val intent = Intent(this@MainActivity, ResultActivity::class.java)
                intent.setAction(Checkable.RUN_CHECKABLE)
                intent.putExtra(Checkable.NAME, item.getName())
                intent.putExtra(Checkable.DESCRIPTION, item.getBody())
                intent.putExtra(Checkable.TASK, item.getCheckable().name)
                startActivity(intent)
            }
        }
    }

    /**
     * An adater class for the test classes recycler view.
     */
    private inner class Adapter : RecyclerView.Adapter<CheckItemViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CheckItemViewHolder {
            return CheckItemViewHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.check_list_item,
                    parent,
                    false
                )
            )
        }

        override fun getItemCount(): Int {
            return checkOptionsList.size
        }

        override fun onBindViewHolder(holder: CheckItemViewHolder, position: Int) {
            val item = checkOptionsList[position]
            holder.headerText.text = item.getName()
            holder.bodyText.text = item.getBrief()
            holder.showButton.setOnClickListener {
                setSelected(item)
            }
        }
    }
}
