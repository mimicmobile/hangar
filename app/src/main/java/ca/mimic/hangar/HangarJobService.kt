package ca.mimic.hangar

import android.app.job.JobParameters
import android.app.job.JobService
import android.widget.Toast
import ca.mimic.hangar.Constants.Companion.INITIAL_JOB_ID
import ca.mimic.hangar.Constants.Companion.JOB_ID
import ca.mimic.hangar.MainActivity.Companion.startJob

class HangarJobService : JobService() {
    private lateinit var sharedPreferences: SharedPrefsHelper

    override fun onStartJob(jp: JobParameters?): Boolean {
        sharedPreferences = SharedPrefsHelper(this)
        if (!Utils.checkForUsagePermission(this)) {
            Toast.makeText(this, R.string.toast_no_permission, Toast.LENGTH_SHORT).show()
            Utils.cancelJob(this, if (jp?.jobId != null) jp.jobId else INITIAL_JOB_ID)

            jobFinished(jp, false)
            return true
        } else {
            var refreshNotifications = true

            if (isInstantJob(jp)) {
                if (sharedPreferences.shouldRefresh()) {
                    sharedPreferences.resetForceRefresh()
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
        startJob(this, JOB_ID,
            sharedPreferences.jobInterval(),
            this::class.java
        )

        return true
    }

    private fun isInstantJob(jp: JobParameters?): Boolean {
        return jp?.jobId == INITIAL_JOB_ID
    }

    override fun onStopJob(jp: JobParameters?): Boolean {
        return false
    }

}