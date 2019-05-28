/**
 * Author: Felix & Shahnur
 */

package oneoone.security.com.fightme.check

import android.content.Context
import android.content.Intent
import android.media.MediaRecorder
import android.os.Environment
import android.os.SystemClock
import android.util.Log
import oneoone.security.com.fightme.CheckResult
import oneoone.security.com.fightme.ResultItem
import oneoone.security.com.fightme.SeverityLevel
import java.io.File
import java.util.concurrent.TimeUnit

object MicrophoneCheck : Checkable {

    val LOG_NAME = this::class.java.simpleName

    val SUCCESS_ATTACK_RESULT = CheckResult(listOf(
        ResultItem("Unauthorized access to microphone", "The device's microphone could be accessed without the consent of the user. This allows apps to make unauthorized recordings, which can be used to steal private information.", SeverityLevel.DANGEROUS)
    ), SeverityLevel.DANGEROUS)

    val FAILED_ATTACK_RESULT = CheckResult(listOf(
        ResultItem("No microphone", "It was not possible to access the microphone without proper authorization.", SeverityLevel.ACCEPTABLE)
    ), SeverityLevel.ACCEPTABLE)

    val MICROPHONE_ATTACK_RESULT = CheckResult(listOf(
        ResultItem("Got access to microphone", "It was possible for the application get access to using the microphone as an recording input.", SeverityLevel.WARNING)
    ), SeverityLevel.WARNING)

    val STORAGE_ATTACK_RESULT = CheckResult(listOf(
        ResultItem("Got access to storage", "The application got access to the device's external storage.", SeverityLevel.WARNING)
    ), SeverityLevel.WARNING)

    val RESULT_IGNORE = CheckResult(listOf(), SeverityLevel.ACCEPTABLE)

    private lateinit var mediaRecorder: MediaRecorder
    val outputPath = "${Environment.getExternalStorageDirectory().absolutePath}/FightMe_MicrophoneCheckRecording.mp3"
    private var failedBefore = false

    override fun notifyResult(requestCode: Int, resultCode: Int, data: Intent) {
        // Ignore
    }

    /**
     * In this check we try to access the microphone and do a 2 seconds recording, saving it to
     * the internal storage.
     */
    override fun performCheck(context: Context, testIndex: Int): CheckResult {
        when (testIndex) {
            // Step 1: Attempt to access the mircophone as an input device.
            0 -> {
                mediaRecorder = MediaRecorder()
                try {
                    mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC)
                    failedBefore = false
                } catch (ignore: RuntimeException) {
                    failedBefore = true
                    return FAILED_ATTACK_RESULT
                }
                mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                mediaRecorder.setOutputFile(outputPath)

                return MICROPHONE_ATTACK_RESULT;
            }
            // Step 2: Attempt to do a recording and save it to the internal storage.
            1 -> {
                if(failedBefore) {
                    return RESULT_IGNORE
                }
                mediaRecorder.prepare()
                mediaRecorder.start()
                Log.d(LOG_NAME, "Starting microphone recording.")

                SystemClock.sleep(TimeUnit.SECONDS.toMillis(2))

                mediaRecorder.stop()
                mediaRecorder.release()
                Log.d(LOG_NAME, "Terminating microphone recording.")

                return STORAGE_ATTACK_RESULT
            }
            // Step 3: Check if the saved file is available.
            2 -> {
                if(failedBefore) {
                    return RESULT_IGNORE
                }
                return if(File(outputPath).isFile()) {
                    SUCCESS_ATTACK_RESULT
                } else {
                    FAILED_ATTACK_RESULT
                }
            }
            else -> throw IllegalArgumentException("Tried to perform change with invalid index.")
        }
    }

    override fun numberOfTests(): Int {
        return 3
    }
}