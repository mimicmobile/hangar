package ca.mimic.hangar

import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.app.job.JobService
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import ca.mimic.hangar.Constants.Companion.FLUTTER_CHANNEL
import ca.mimic.hangar.Constants.Companion.INITIAL_JOB_ID
import ca.mimic.hangar.Constants.Companion.REFRESH_NOTIFICATION_MESSAGE
import io.flutter.app.FlutterActivity
import io.flutter.plugin.common.BasicMessageChannel
import io.flutter.plugins.GeneratedPluginRegistrant
import io.flutter.view.FlutterMain
import io.flutter.plugin.common.StringCodec

class MainActivity : FlutterActivity() {
    override fun onResume() {
        super.onResume()

        if (!startedInstantJob(this)) {
            startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        FlutterMain.startInitialization(this)
        super.onCreate(savedInstanceState)

        val channel = BasicMessageChannel<String>(
            flutterView, FLUTTER_CHANNEL, StringCodec.INSTANCE
        )

        GeneratedPluginRegistrant.registerWith(this)

        channel.setMessageHandler { s, _ ->
            when (s) {
                REFRESH_NOTIFICATION_MESSAGE -> {
                    startedInstantJob(this)
                }
            }
        }
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
