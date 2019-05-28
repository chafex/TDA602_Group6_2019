/**
 * Author: Felix
 */

package oneoone.security.com.fightmecompanion

import android.os.Environment
import java.io.File

/**
 * A utilities class for testing local storage file access.
 */
object Utils {
    /**
     * Will attempt to create a dummy file if not already existing, otherwise it will attempt to delete the file.
     *
     * @returns - True if it was possible to create/delete the dummy file, otherwise False.
     */
    @JvmStatic
    fun testFileAccess(): Boolean {
        val file = File("${Environment.getExternalStorageDirectory().absolutePath}/FightMe_StorageDummy.txt")
        return if(file.exists()) {
            file.delete()
            ! file.exists()
        } else {
            file.createNewFile()
            file.writeText("Spooky")
            file.exists()
        }
    }
}