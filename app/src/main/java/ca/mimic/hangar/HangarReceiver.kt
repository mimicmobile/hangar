package ca.mimic.hangar

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import ca.mimic.hangar.Constants.Companion.EXTRA_PACKAGE_NAME
import ca.mimic.hangar.Constants.Companion.RECEIVER_APP_LAUNCHED
import ca.mimic.hangar.Constants.Companion.RECEIVER_BOOT_COMPLETED

class HangarReceiver : BroadcastReceiver() {
    private lateinit var receiverContext: Context
    private val sharedPreferences = SharedPrefsHelper(receiverContext)

    override fun onReceive(context: Context, intent: Intent) {
        receiverContext = context

        when (intent.action) {
            RECEIVER_APP_LAUNCHED -> {
                val packageName = intent.getStringExtra(EXTRA_PACKAGE_NAME)

                if (packageName == Constants.SWITCH_APP_PACKAGE_NAME) {
                    val page = incrementPageNumber()
                    sharedPreferences.setPage(page)
                } else {
                    AppStorage(context).launchApp(packageName)
                    sharedPreferences.setPage(1)

                    context.sendBroadcast(Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS))
                }

            }
            RECEIVER_BOOT_COMPLETED -> {
                Utils.getUsageStats(context)
            }
        }

        NotificationShortcuts(context).create()
    }

    private fun incrementPageNumber(): Long {
        val page = sharedPreferences.currentPage()
        val numOfPages = sharedPreferences.numOfPages()

        return if (page + 1 > numOfPages) {
            1
        } else {
            (page + 1).toLong()
        }
    }
}
