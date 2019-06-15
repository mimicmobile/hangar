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
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugins.GeneratedPluginRegistrant
import io.flutter.view.FlutterMain
import kotlinx.coroutines.*

class MainActivity : FlutterActivity() {
    private val job = Job()
    private val bgScope = CoroutineScope(Dispatchers.Default + job)
    private val uiScope = CoroutineScope(Dispatchers.Main + job)
    private val appStorage by lazy { AppStorage(this) }
    private val channel by lazy { MethodChannel(flutterView, FLUTTER_CHANNEL) }
    private lateinit var selectedAppPackageName: String

    override fun onResume() {
        super.onResume()

        if (!Utils.checkForUsagePermission(this)) {
            startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))
        } else {
            startJob(this, INITIAL_JOB_ID, 0, HangarJobService::class.java)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        FlutterMain.startInitialization(this)
        super.onCreate(savedInstanceState)

        GeneratedPluginRegistrant.registerWith(this)

        channel.setMethodCallHandler { call, result ->
            val packageName = call.argument<String>("packageName")
            val launchPackageName = call.argument<String>("launchPackageName")

            when (call.method) {
                REFRESH_NOTIFICATION_MESSAGE -> {
                    refreshNotifications()
                }
                ICON_PACK_REBUILD_MESSAGE -> {
                    rebuildIconPacks()
                }
                ICON_PACK_LIST_MESSAGE -> {
                    val iconPackJson = getIconPacksJson(packageName)
                    result.success(iconPackJson)
                }
                CHANGE_ICON_MESSAGE -> {
                    selectedAppPackageName = packageName!!

                    when (launchPackageName) {
                        "default" -> {
                            generateDefaultIcon(launchPackageName)
                        }
                        else -> {
                            launchIconChooser(launchPackageName)
                        }
                    }

                }
            }
        }
    }

    private fun refreshNotifications() {
        bgScope.launch {
            val sharedPreferences = SharedPrefsHelper(applicationContext)
            if (sharedPreferences.shouldRefresh()) {
                sharedPreferences.resetForceRefresh()
                Utils.getUsageStats(applicationContext, true)
            }
            NotificationShortcuts(applicationContext).create()
        }
    }

    private fun rebuildIconPacks() {
        appStorage.iconsHandler.cacheClear()
        bgScope.launch {
            appStorage.iconsHandler.loadIconsPack()

            Utils.getUsageStats(applicationContext, true)
            NotificationShortcuts(applicationContext).create()

            uiScope.launch {
                channel.invokeMethod(ICON_PACK_REBUILD_MESSAGE, null)
            }
        }
    }

    private fun getIconPacksJson(defaultPackageName: String?): String {
        return appStorage.iconPacksJson(defaultPackageName!!)
    }

    private fun generateDefaultIcon(packageName: String) {
        bgScope.launch {
            appStorage.iconsHandler.generateBitmapFromIconPack(
                selectedAppPackageName,
                packageName
            )
                ?.let {
                    appStorage.updateAppIcon(selectedAppPackageName, it)

                    NotificationShortcuts(applicationContext).create()
                    uiScope.launch {
                        channel.invokeMethod(ICON_PACK_REBUILD_MESSAGE, null)
                    }
                }
        }
    }

    private fun launchIconChooser(launchPackageName: String?) {
        val intent = Intent().apply {
            setPackage(launchPackageName!!)
            action = ACTION_ADW_PICK_ICON
        }
        startActivityForResult(intent, 1)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                val bitmap = data?.getParcelableExtra<Bitmap>("icon")
                val filename = "${selectedAppPackageName}_${System.currentTimeMillis()}"
                val hasBitmapStored = appStorage.iconsHandler.cacheStoreBitmap(filename, bitmap)
                if (hasBitmapStored) {
                    appStorage.updateAppIcon(selectedAppPackageName, filename)

                    Utils.getUsageStats(applicationContext, true)
                    NotificationShortcuts(applicationContext).create()
                    channel.invokeMethod(ICON_PACK_REBUILD_MESSAGE, null)
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
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
