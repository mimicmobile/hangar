package ca.mimic.hangar

import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.app.job.JobService
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.Settings
import ca.mimic.hangar.Constants.Companion.ACTION_ADW_PICK_ICON
import ca.mimic.hangar.Constants.Companion.CHANGE_ICON_MESSAGE
import ca.mimic.hangar.Constants.Companion.FLUTTER_CHANNEL
import ca.mimic.hangar.Constants.Companion.ICON_PACK_LIST_MESSAGE
import ca.mimic.hangar.Constants.Companion.ICON_PACK_REBUILD_MESSAGE
import ca.mimic.hangar.Constants.Companion.INITIAL_JOB_ID
import ca.mimic.hangar.Constants.Companion.REFRESH_NOTIFICATION_MESSAGE
import io.flutter.app.FlutterActivity
import io.flutter.plugin.common.BasicMessageChannel
import io.flutter.plugins.GeneratedPluginRegistrant
import io.flutter.view.FlutterMain
import io.flutter.plugin.common.StringCodec
import kotlinx.coroutines.*

class MainActivity : FlutterActivity() {
    private val job = Job()
    private val bgScope = CoroutineScope(Dispatchers.Default + job)
    private val appStorage by lazy { AppStorage(this) }
    private val channel by lazy { BasicMessageChannel<String>(flutterView, FLUTTER_CHANNEL, StringCodec.INSTANCE) }
    private lateinit var selectedAppPackageName: String

    override fun onResume() {
        super.onResume()

        if (!startedInstantJob(this)) {
            startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        FlutterMain.startInitialization(this)
        super.onCreate(savedInstanceState)

        GeneratedPluginRegistrant.registerWith(this)

        channel.setMessageHandler { s, a ->
            val message = s.split(':')
            when (message[0]) {
                REFRESH_NOTIFICATION_MESSAGE -> {
                    bgScope.launch {
                        val sharedPreferences = SharedPrefsHelper(applicationContext)
                        if (sharedPreferences.shouldRefresh()) {
                            sharedPreferences.resetForceRefresh()
                            Utils.getUsageStats(applicationContext, true)
                        }
                        NotificationShortcuts(applicationContext).create()
                    }
                }
                ICON_PACK_REBUILD_MESSAGE -> {
                    appStorage.iconsHandler.cacheClear()
                    bgScope.launch {
                        appStorage.iconsHandler.loadIconsPack()

                        Utils.getUsageStats(applicationContext, true)
                        NotificationShortcuts(applicationContext).create()

                        channel.send(ICON_PACK_REBUILD_MESSAGE)
                    }
                }
                ICON_PACK_LIST_MESSAGE -> {
                    val b = appStorage.iconPacksJson(message[1])
                    a.reply(b)
                }
                CHANGE_ICON_MESSAGE -> {
                    selectedAppPackageName = message[1]

                    when (message[2]) {
                        "default" -> {
                            bgScope.launch {
                                appStorage.iconsHandler.generateBitmapFromIconPack(selectedAppPackageName, message[2])
                                    ?.let {
                                        appStorage.updateAppIcon(selectedAppPackageName, it)

                                        NotificationShortcuts(applicationContext).create()
                                        channel.send(ICON_PACK_REBUILD_MESSAGE)
                                    }
                            }
                        }
                        else -> {
                            val intent = Intent().apply {
                                setPackage(message[2])
                                action = ACTION_ADW_PICK_ICON
                            }
                            startActivityForResult(intent, 1)
                        }
                    }

                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                val bitmap = data?.getParcelableExtra<Bitmap>("icon")
                val filename = "${selectedAppPackageName}_${System.currentTimeMillis()}"
                val bitmapStored = appStorage.iconsHandler.cacheStoreBitmap(filename, bitmap)
                if (bitmapStored) {
                    appStorage.updateAppIcon(selectedAppPackageName, filename)

                    Utils.getUsageStats(applicationContext, true)
                    NotificationShortcuts(applicationContext).create()
                    channel.send(ICON_PACK_REBUILD_MESSAGE)
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }

    private fun startedInstantJob(context: Context): Boolean {
        if (Utils.checkForUsagePermission(context)) {
            startJob(context, INITIAL_JOB_ID, 0, HangarJobService::class.java)
            return true
        }
        return false
    }

    companion object {
        fun <T : JobService> startJob(context: Context, id: Int, interval: Long, clazz: Class<T>) {
            val js = context.getSystemService(JOB_SCHEDULER_SERVICE) as JobScheduler
            Utils.cancelJob(context, id)

            val b = JobInfo.Builder(
                id,
                ComponentName(context, clazz)
            ).setMinimumLatency(interval).setPersisted(true)

            js.schedule(b.build())
        }
    }
}
