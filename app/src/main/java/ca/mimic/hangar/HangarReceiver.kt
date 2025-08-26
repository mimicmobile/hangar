package ca.mimic.hangar

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import ca.mimic.hangar.Constants.Companion.EXTRA_PACKAGE_NAME
import ca.mimic.hangar.Constants.Companion.RECEIVER_APP_LAUNCHED
import ca.mimic.hangar.Constants.Companion.RECEIVER_BOOT_COMPLETED

class HangarReceiver : BroadcastReceiver() {
    private lateinit var receiverContext: Context
    private val sharedPreferences by lazy { SharedPrefsHelper(receiverContext) }

    @SuppressLint("MissingPermission")
    override fun onReceive(context: Context, intent: Intent) {
        receiverContext = context

        when (intent.action) {
            RECEIVER_APP_LAUNCHED -> {
                val packageName: String = intent.getStringExtra(EXTRA_PACKAGE_NAME)!!

                if (packageName == Constants.SWITCH_APP_PACKAGE_NAME) {
                    val page = TrampolineActivity.incrementPageNumber(sharedPreferences)
                    sharedPreferences.setPage(page)
                } else {
                    AppStorage(context).launchApp(packageName)
                    sharedPreferences.setPage(1)

                    @Suppress("DEPRECATION")
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
                        context.sendBroadcast(Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS))
                    }
                }

            }

            RECEIVER_BOOT_COMPLETED -> {
                Utils.getUsageStats(context)
            }
        }

        NotificationShortcuts(context).create()
    }
}
