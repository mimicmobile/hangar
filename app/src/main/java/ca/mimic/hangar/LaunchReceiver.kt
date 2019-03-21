package ca.mimic.hangar

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import ca.mimic.hangar.Constants.Companion.EXTRA_PACKAGE_NAME
import ca.mimic.hangar.Constants.Companion.INITIAL_JOB_ID
import ca.mimic.hangar.Constants.Companion.RECEIVER_APP_LAUNCHED
import ca.mimic.hangar.Constants.Companion.RECEIVER_BOOT_COMPLETED
import ca.mimic.hangar.MainActivity.Companion.startJob

class LaunchReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            RECEIVER_APP_LAUNCHED -> {
                val packageName = intent.getStringExtra(EXTRA_PACKAGE_NAME)

                val appStorage = AppStorage(context)
                appStorage.launchApp(packageName)

                context.sendBroadcast(Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS))
            }
            RECEIVER_BOOT_COMPLETED -> {
                startJob(context, INITIAL_JOB_ID, 0, HangarJobService::class.java)
            }
        }
    }
}
