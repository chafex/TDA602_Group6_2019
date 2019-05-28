/**
 * Author: Felix
 */

package oneoone.security.com.fightmecompanion

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.IBinder

/**
 * A service for responding to requests from the FightMe vulnerability
 * scanner application. It will attempt to access the local storage and
 * then report back.
 */
class MaliciousServer: Service() {

    override fun onCreate() {
        super.onCreate()
        registerReceiver(object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                val result = Intent()
                result.action = "AccessStorageOK"
                if(Utils.testFileAccess()) {
                    result.putExtra("Success", true)
                }
                sendBroadcast(result)
            }
        }, IntentFilter("AccessStorage"))
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}
