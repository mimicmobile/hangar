package ca.mimic.hangar

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import ca.mimic.hangar.Constants.Companion.EXTRA_PACKAGE_NAME
import ca.mimic.hangar.Constants.Companion.INITIAL_JOB_ID
import ca.mimic.hangar.Constants.Companion.PREF_CURRENT_PAGE
import ca.mimic.hangar.Constants.Companion.RECEIVER_APP_LAUNCHED
import ca.mimic.hangar.Constants.Companion.RECEIVER_BOOT_COMPLETED
import ca.mimic.hangar.MainActivity.Companion.startJob

class HangarReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            RECEIVER_APP_LAUNCHED -> {
                val packageName = intent.getStringExtra(EXTRA_PACKAGE_NAME)

                if (packageName == Constants.SWITCH_APP_PACKAGE_NAME) {
                    switchPage(context)
                } else {

                    val appStorage = AppStorage(context)
                    appStorage.launchApp(packageName)

                    context.sendBroadcast(Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS))
                }
            }
            RECEIVER_BOOT_COMPLETED -> {
                startInitialJob(context)
            }
        }
    }

    private fun startInitialJob(context: Context) {
        startJob(context, INITIAL_JOB_ID, 0, HangarJobService::class.java)
    }

    private fun switchPage(context: Context) {
        val page = context.getSharedPreferences(Constants.PREFS_FILE, 0)
            .getLong(PREF_CURRENT_PAGE, 1).toInt()
        val numOfPages = context.getSharedPreferences(Constants.PREFS_FILE, 0)
            .getLong(Constants.PREF_NUM_PAGES, Constants.DEFAULT_NUM_PAGES).toInt()

        val editor = context.getSharedPreferences(Constants.PREFS_FILE, 0).edit()
        editor.putLong(
            PREF_CURRENT_PAGE, if (page + 1 > numOfPages) {
                1
            } else {
                (page + 1).toLong()
            }
        ).apply()

        startInitialJob(context)
    }
}
