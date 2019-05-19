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
        const val RECEIVER_BOOT_COMPLETED = "android.intent.action.BOOT_COMPLETED"
        // Prefs
        const val PREFS_FILE: String = "FlutterSharedPreferences"
        const val PREF_APP_LIST: String = "flutter.apps"
        const val PREF_NUM_ROWS: String = "flutter.numRows"
        const val PREF_APPS_PER_ROW: String = "flutter.appsPerRow"
        const val PREF_NUM_PAGES: String = "flutter.numPages"
        const val PREF_BACKGROUND_COLOR: String = "flutter.backgroundColor"
        const val PREF_JOB_INTERVAL: String = "flutter.jobInterval"

        const val PREF_FORCE_REFRESH: String = "flutter.forceRefresh"
        const val PREF_CURRENT_PAGE: String = "flutter.currentPage"

        // Background colors
        const val PREF_BACKGROUND_COLOR_DEFAULT: String = "White"
        const val PREF_BACKGROUND_COLOR_DARK: String = "Material Dark"
        const val PREF_BACKGROUND_COLOR_BLACK: String = "Black"

        val PREF_JOB_INTERVAL_MAP: Map<String, Long> =
            mapOf(
                "5 seconds" to 5000L,
                "10 seconds" to 10000L,
                "15 seconds" to 15000L,
                "20 seconds" to 20000L,
                "30 seconds" to 30000L,
                "1 minute" to 60000L,
                "2 minutes" to 120000L
            )

        // Defaults
        const val DEFAULT_NUM_ROWS: Long = 2
        const val DEFAULT_APPS_PER_ROW: Long = 7
        const val DEFAULT_NUM_PAGES: Long = 1
        const val DEFAULT_JOB_INTERVAL: String = "20 seconds"

        val IGNORED_PACKAGES: Array<String> = arrayOf(
            "android",
            "com.google.android.packageinstaller",
            "com.android.systemui",
            "com.google.android.gms",
            "ca.mimic.apphangar"
        )
    }
}