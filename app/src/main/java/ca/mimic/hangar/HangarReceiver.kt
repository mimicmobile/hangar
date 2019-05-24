package ca.mimic.hangar

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import ca.mimic.hangar.Constants.Companion.EXTRA_PACKAGE_NAME
import ca.mimic.hangar.Constants.Companion.RECEIVER_APP_LAUNCHED
import ca.mimic.hangar.Constants.Companion.RECEIVER_BOOT_COMPLETED

class HangarReceiver : BroadcastReceiver() {
    private lateinit var sharedPrefs: SharedPreferences

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            RECEIVER_APP_LAUNCHED -> {
                val packageName = intent.getStringExtra(EXTRA_PACKAGE_NAME)
                sharedPrefs = SharedPrefsHelper.getPrefs(context)

                if (packageName == Constants.SWITCH_APP_PACKAGE_NAME) {
                    SharedPrefsHelper.setPage(sharedPrefs, addPage())
                } else {
                    val appStorage = AppStorage(context)
                    appStorage.launchApp(packageName)
                    SharedPrefsHelper.setPage(sharedPrefs, 1)

                    context.sendBroadcast(Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS))
                }

                NotificationShortcuts(context).create()
            }
            RECEIVER_BOOT_COMPLETED -> {
                Utils.getUsageStats(context)
                NotificationShortcuts(context).create()
            }
        }
    }

    private fun addPage(): Long {
        val page = SharedPrefsHelper.currentPage(sharedPrefs)
        val numOfPages = SharedPrefsHelper.numOfPages(sharedPrefs)

        return if (page + 1 > numOfPages) {
            1
        } else {
            (page + 1).toLong()
        }
    }
}
