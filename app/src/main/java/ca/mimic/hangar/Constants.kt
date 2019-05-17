package ca.mimic.hangar

class Constants {
    companion object {
        const val INITIAL_JOB_ID = 665
        const val JOB_ID = 666
        const val JOB_INTERVAL: Long = 20000 // TODO: Settings
        const val NOTIFICATION_ID = 1337
        const val NOTIFICATION_CHANNEL_ID = "hangar"
        const val EXTRA_PACKAGE_NAME = "package_name"
        const val SWITCH_APP_PACKAGE_NAME = "ca.mimic.hangar.switch"
        const val RECEIVER_APP_LAUNCHED = "ca.mimic.hangar.APP_LAUNCHED"
        const val RECEIVER_BOOT_COMPLETED = "ACTION_SUGGEST_NOTIFICATION_MARK_READ"
        // Prefs
        const val PREFS_FILE: String = "FlutterSharedPreferences"
        const val PREF_APP_LIST: String = "flutter.apps"
        const val PREF_NUM_ROWS: String = "flutter.numRows"
        const val PREF_APPS_PER_ROW: String = "flutter.appsPerRow"
        const val PREF_NUM_PAGES: String = "flutter.numPages"
        const val PREF_FORCE_REFRESH: String = "flutter.forceRefresh"
        const val PREF_CURRENT_PAGE: String = "flutter.currentPage"
        // Defaults
        const val DEFAULT_NUM_ROWS: Long = 2
        const val DEFAULT_APPS_PER_ROW: Long = 7
        const val DEFAULT_NUM_PAGES: Long = 1

        val IGNORED_PACKAGES: Array<String> = arrayOf(
            "android",
            "com.google.android.packageinstaller",
            "com.android.systemui",
            "com.google.android.gms",
            "ca.mimic.apphangar"
        )
    }
}