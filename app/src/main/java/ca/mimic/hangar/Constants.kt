package ca.mimic.hangar

class Constants {
    companion object {
        const val INITIAL_JOB_ID = 665
        const val JOB_ID = 666
        const val JOB_INTERVAL: Long = 20000 // TODO: Settings
        const val NOTIFICATION_ID = 1337
        const val NOTIFICATION_CHANNEL_ID = "hangar"
        const val EXTRA_PACKAGE_NAME = "package_name"
        const val RECEIVER_APP_LAUNCHED = "ca.mimic.hangar.APP_LAUNCHED"
        const val RECEIVER_BOOT_COMPLETED = "ACTION_SUGGEST_NOTIFICATION_MARK_READ"
        const val PREFS_NAME: String = "FlutterSharedPreferences"
        const val APP_PREFS: String = "flutter.apps"
        const val FORCE_REFRESH_PREFS: String = "flutter.forceRefresh"

        val IGNORED_PACKAGES: Array<String> = arrayOf(
            "android",
            "com.google.android.packageinstaller",
            "com.android.systemui",
            "com.google.android.gms",
            "ca.mimic.apphangar"
        )
    }
}