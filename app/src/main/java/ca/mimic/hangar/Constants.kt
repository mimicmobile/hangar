package ca.mimic.hangar

class Constants {
    companion object {
        const val INITIAL_JOB_ID = 665
        const val JOB_ID = 666
        const val NOTIFICATION_ID = 1337
        const val NOTIFICATION_CHANNEL_ID = "hangar"
        const val EXTRA_PACKAGE_NAME = "package_name"
        const val FLUTTER_CHANNEL = "hangar/native_channel"
        const val REFRESH_NOTIFICATION_MESSAGE = "refresh_notification"
        const val ICON_PACK_REBUILD_MESSAGE = "icon_pack_rebuild"
        const val ICON_PACK_LIST_MESSAGE = "icon_pack_list"
        const val CHANGE_ICON_MESSAGE = "change_icon"
        const val SWITCH_APP_PACKAGE_NAME = "ca.mimic.hangar.switch"
        const val RECEIVER_APP_LAUNCHED = "ca.mimic.hangar.APP_LAUNCHED"
        const val RECEIVER_BOOT_COMPLETED = "android.intent.action.BOOT_COMPLETED"
        const val ACTION_ADW_PICK_ICON = "org.adw.launcher.icons.ACTION_PICK_ICON"
        // Prefs
        const val PREFS_FILE: String = "FlutterSharedPreferences"
        const val PREF_APP_LIST: String = "flutter.apps"
        const val PREF_NUM_ROWS: String = "flutter.numRows"
        const val PREF_APPS_PER_ROW: String = "flutter.appsPerRow"
        const val PREF_NUM_PAGES: String = "flutter.numPages"
        const val PREF_BACKGROUND_COLOR: String = "flutter.backgroundColor"
        const val PREF_ICON_SIZE: String = "flutter.iconSize"
        const val PREF_ICON_PACK: String = "flutter.iconPack"
        const val PREF_PINNED_APP_PLACEMENT: String = "flutter.pinnedAppPlacement"
        const val PREF_NOTIFICATION_WEIGHT: String = "flutter.notificationWeight"
        const val PREF_JOB_INTERVAL: String = "flutter.jobInterval"

        const val PREF_FORCE_REFRESH: String = "flutter.forceRefresh"
        const val PREF_CURRENT_PAGE: String = "flutter.currentPage"

        // Background colors
        const val PREF_BACKGROUND_COLOR_DEFAULT: String = "white"
        const val PREF_BACKGROUND_COLOR_DARK: String = "materialDark"
        const val PREF_BACKGROUND_COLOR_BLACK: String = "black"

        // Weight array order: foregroundTime, lastUsed, timesUpdated (unused), timesLaunched
        internal val defaultWeight: Array<Float> = arrayOf(1f, 3f, 1f, 1.5f)

        internal val weightMap: Map<String, Array<Float>> = mapOf(
            "lastUsed" to arrayOf(1f, 3f, 1f, 1.5f),
            "foregroundTime" to arrayOf(4f, 1f, 1f, 1.5f),
            "timesLaunched" to arrayOf(1f, 1f, 1f, 4f)
        )

        internal val iconSizeMap: Map<String, Int> = mapOf(
            "small" to R.layout.notification_item_small,
            "medium" to R.layout.notification_item,
            "large" to R.layout.notification_item_large
        )

        // Defaults
        const val DEFAULT_NUM_ROWS: Long = 2
        const val DEFAULT_APPS_PER_ROW: Long = 7
        const val DEFAULT_NUM_PAGES: Long = 1
        const val DEFAULT_JOB_INTERVAL: Long = 15000
        const val DEFAULT_NOTIFICATION_WEIGHT: String = "lastUsed"
        const val DEFAULT_ICON_SIZE = "medium"
        const val DEFAULT_ICON_PACK = "default"
        const val DEFAULT_PINNED_APP_PLACEMENT = "left"

        val IGNORED_PACKAGES: Array<String> = arrayOf(
            "android",
            "com.google.android.packageinstaller",
            "com.android.systemui",
            "com.google.android.gms",
            "ca.mimic.apphangar"
        )
    }
}