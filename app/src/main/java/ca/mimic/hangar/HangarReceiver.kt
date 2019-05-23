package ca.mimic.hangar

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import ca.mimic.hangar.Constants.Companion.EXTRA_PACKAGE_NAME
import ca.mimic.hangar.Constants.Companion.PREF_CURRENT_PAGE
import ca.mimic.hangar.Constants.Companion.RECEIVER_APP_LAUNCHED
import ca.mimic.hangar.Constants.Companion.RECEIVER_BOOT_COMPLETED

class HangarReceiver : BroadcastReceiver() {
    private lateinit var sharedPrefs: SharedPreferences

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            RECEIVER_APP_LAUNCHED -> {
                val packageName = intent.getStringExtra(EXTRA_PACKAGE_NAME)
                sharedPrefs = context.getSharedPreferences(Constants.PREFS_FILE, 0)

                if (packageName == Constants.SWITCH_APP_PACKAGE_NAME) {
                    setPage(addPage())
                } else {
                    val appStorage = AppStorage(context)
                    appStorage.launchApp(packageName)
                    setPage(1)

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

    private fun numOfPages(): Int {
        return sharedPrefs.getLong(Constants.PREF_NUM_PAGES, Constants.DEFAULT_NUM_PAGES).toInt()
    }

    private fun addPage(): Long {
        val page = sharedPrefs.getLong(PREF_CURRENT_PAGE, 1).toInt()
        val numOfPages = numOfPages()

        return if (page + 1 > numOfPages) {
            1
        } else {
            (page + 1).toLong()
        }
    }

    private fun setPage(page: Long) {
        val editor = sharedPrefs.edit()
        editor.putLong(PREF_CURRENT_PAGE, page).apply()
    }
}
