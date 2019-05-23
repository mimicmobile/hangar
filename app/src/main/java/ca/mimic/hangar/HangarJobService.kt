package ca.mimic.hangar

import android.app.job.JobParameters
import android.app.job.JobService
import android.content.Context
import android.widget.Toast
import ca.mimic.hangar.Constants.Companion.DEFAULT_JOB_INTERVAL
import ca.mimic.hangar.Constants.Companion.INITIAL_JOB_ID
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
            var refreshNotifications = true

            if (isInstantJob(jp)) {
                if (Utils.needsRefresh(this)) {
                    Utils.getUsageStats(this, true)
                }
            } else {
                refreshNotifications = Utils.getUsageStats(this)
            }

            if (refreshNotifications) {
                NotificationShortcuts(this).create()
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

    private fun isInstantJob(jp: JobParameters?): Boolean {
        return jp?.jobId == INITIAL_JOB_ID
    }

    override fun onStopJob(jp: JobParameters?): Boolean {
        return false
    }

}