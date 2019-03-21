package ca.mimic.hangar

import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.app.job.JobService
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import ca.mimic.hangar.Constants.Companion.INITIAL_JOB_ID
import io.flutter.app.FlutterActivity
import io.flutter.plugins.GeneratedPluginRegistrant
import io.flutter.view.FlutterMain

class MainActivity : FlutterActivity() {
    override fun onResume() {
        super.onResume()

        if (Utils.checkForUsagePermission(this)) {
            startJob(this, INITIAL_JOB_ID, 0, HangarJobService::class.java)
        } else {
            startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        FlutterMain.startInitialization(this)
        super.onCreate(savedInstanceState)

        GeneratedPluginRegistrant.registerWith(this)
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
