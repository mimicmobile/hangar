package ca.mimic.hangar

import android.annotation.SuppressLint
import android.app.job.JobParameters
import android.app.job.JobService
import android.app.usage.UsageStatsManager
import android.content.Context
import android.widget.Toast
import ca.mimic.hangar.Constants.Companion.DEFAULT_JOB_INTERVAL
import ca.mimic.hangar.Constants.Companion.PREF_FORCE_REFRESH
import ca.mimic.hangar.Constants.Companion.INITIAL_JOB_ID
import java.util.*
import ca.mimic.hangar.Constants.Companion.JOB_ID
import ca.mimic.hangar.Constants.Companion.PREFS_FILE
import ca.mimic.hangar.Constants.Companion.PREF_JOB_INTERVAL
import ca.mimic.hangar.MainActivity.Companion.startJob

class HangarJobService : JobService() {
    override fun onStartJob(jp: JobParameters?): Boolean {
        if (!Utils.checkForUsagePermission(this)) {
            Toast.makeText(this, R.string.toast_no_permission, Toast.LENGTH_SHORT).show()
            Utils.cancelJob(this, if (jp?.jobId != null) jp.jobId else INITIAL_JOB_ID)

            jobFinished(jp, false)
            return true
        } else {
            val refreshNotifications =
                isInitialJob(jp) || needsRefresh(this) || (Utils.isScreenOn(this) && getUsageStats())
            if (refreshNotifications) {
                NotificationShortcuts(this).start()
            }
        }

        jobFinished(jp, false)
        startJob(this, JOB_ID, jobIntervalFromPrefs(this), this::class.java)

        return true
    }

    private fun jobIntervalFromPrefs(context: Context): Long {
        return context.getSharedPreferences(PREFS_FILE, 0).getLong(
            PREF_JOB_INTERVAL,
            DEFAULT_JOB_INTERVAL
        )
    }

    private fun needsRefresh(context: Context): Boolean {
        val shouldRefresh = context.getSharedPreferences(PREFS_FILE, 0).getBoolean(
            PREF_FORCE_REFRESH,
            true
        )

        val editor = context.getSharedPreferences(PREFS_FILE, 0).edit()
        editor.putBoolean(PREF_FORCE_REFRESH, false).apply()

        return shouldRefresh
    }

    private fun isInitialJob(jp: JobParameters?): Boolean {
        return jp?.jobId == INITIAL_JOB_ID
    }

    private fun getUsageStats(): Boolean {
        val stats = getUsageStatsManager().queryAndAggregateUsageStats(
            getBeginTimeMillis(),
            System.currentTimeMillis()
        ).toList()

        val appStorage = AppStorage(this)

        stats.filter {
            it.second.lastTimeUsed > 100000 &&
                    it.second.totalTimeInForeground > 0 &&
                    !Constants.IGNORED_PACKAGES.contains(it.second.packageName) &&
                    !appStorage.launchers.contains(it.second.packageName)
        }
            .forEach { usageStats ->
                appStorage.checkApp(
                    usageStats.second.packageName,
                    lastTimeUsed = usageStats.second.lastTimeUsed,
                    totalTimeInForeground = usageStats.second.totalTimeInForeground
                )
            }

        return appStorage.savePrefs()
    }

    @SuppressLint("WrongConstant")
    private fun getUsageStatsManager(): UsageStatsManager {
        return getSystemService(if (Utils.isApi22()) Context.USAGE_STATS_SERVICE else "usagestats") as UsageStatsManager
    }

    private fun getBeginTimeMillis(): Long {
        val cal = Calendar.getInstance()
        cal.add(Calendar.YEAR, -1)

        return cal.timeInMillis
    }

    override fun onStopJob(jp: JobParameters?): Boolean {
        return false
    }

}