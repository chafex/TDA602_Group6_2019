/**
 * Author: Felix
 */

package oneoone.security.com.fightmecompanion

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat

/**
 * This is a dummy activity that only launches a background service. If
 * you attempt to start it, it will just flash white and then close (meaning
 * the service was correctly started).
 *
 * The secondary purpose of this application was to allow for disassembly, which
 * is why it contains some seemingly unused code and constants.
 */
class MainActivity : AppCompatActivity() {

    private val secretToken = "This is secret!" // You will just have to imagine this is not visible in the assembled code...
    private val secret = "Some other secret" // This too...

    companion object {
        const val TYPE = "SECRET_REQUEST"
        const val TYPE_RESULT = "SECRET_RESPONSE"
        const val TOKEN = "TOKEN"
        const val RESULT = "RESULT"
    }

    /**
     * This method will start the background service.
     */
    private fun run() {
        val serviceIntent = Intent(this, MaliciousServer::class.java)
        startService(serviceIntent)
        val result = Intent()
        if(intent.type == "AccessStorage") {
            if(Utils.testFileAccess()) {
                setResult(Activity.RESULT_OK, result)
            } else {
                setResult(Activity.RESULT_CANCELED, result)
            }
            finish()
        } else if(intent.type == TYPE
            && intent.hasExtra(TOKEN)
            && intent?.component?.packageName == componentName.packageName) {
            val token = intent.getStringExtra(TOKEN)
            if(token == secretToken) {
                result.type = TYPE_RESULT
                result.putExtra(RESULT, secret)
                setResult(Activity.RESULT_OK, result)
                finish()
            }
        } else {
            setResult(Activity.RESULT_CANCELED)
            finish()
        }
    }

    /**
     * The only purpose of the activity is to request storage permissions and then
     * open the background service.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if(checkPermissions()) {
            run()
        } else {
            requestPermissions()
        }
    }

    /**
     * Callback for results after asking for permissions.
     */
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == 101) {
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                run()
            } else {
                requestPermissions()
            }
        }
    }

    /**
     * Attempt to ask for storage permissions.
     */
    private fun requestPermissions() {
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 101)
    }

    /**
     * Check for storage permissions.
     *
     * @returns - True if the application has storage permissions, otherwise False.
     */
    private fun checkPermissions(): Boolean {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
    }
}
