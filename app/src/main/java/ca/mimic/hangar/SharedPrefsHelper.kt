package ca.mimic.hangar

import android.content.Context
import android.content.SharedPreferences

class SharedPrefsHelper {
    companion object {
        fun getPrefs(context: Context): SharedPreferences = context.getSharedPreferences(Constants.PREFS_FILE, 0)

        fun maxAppsPerRow(sp: SharedPreferences) = sp
            .getLong(Constants.PREF_APPS_PER_ROW, Constants.DEFAULT_APPS_PER_ROW).toInt()

        fun numOfRows(sp: SharedPreferences) = sp
            .getLong(Constants.PREF_NUM_ROWS, Constants.DEFAULT_NUM_ROWS).toInt()

        fun numOfPages(sp: SharedPreferences) = sp
            .getLong(Constants.PREF_NUM_PAGES, Constants.DEFAULT_NUM_PAGES).toInt()

        fun currentPage(sp: SharedPreferences) = sp
            .getLong(Constants.PREF_CURRENT_PAGE, 1).toInt()

        fun iconSize(sp: SharedPreferences): String = sp
            .getString(Constants.PREF_ICON_SIZE, Constants.DEFAULT_ICON_SIZE)!!

        fun iconPack(sp: SharedPreferences): String = sp
            .getString(Constants.PREF_ICON_PACK, Constants.DEFAULT_ICON_PACK)!!

        fun jobInterval(sp: SharedPreferences) = sp
            .getLong(Constants.PREF_JOB_INTERVAL, Constants.DEFAULT_JOB_INTERVAL)

        fun orderPriority(sp: SharedPreferences) = sp
            .getString(Constants.PREF_NOTIFICATION_WEIGHT, Constants.DEFAULT_NOTIFICATION_WEIGHT)!!

        fun appList(sp: SharedPreferences) = sp
            .getString(Constants.PREF_APP_LIST, "[]")!!

        fun bgColor(sp: SharedPreferences) = sp
            .getString(Constants.PREF_BACKGROUND_COLOR, Constants.PREF_BACKGROUND_COLOR_DEFAULT)!!

        fun pinnedAppPlacement(sp: SharedPreferences) = sp
            .getString(Constants.PREF_PINNED_APP_PLACEMENT, Constants.DEFAULT_PINNED_APP_PLACEMENT)!!

        private fun shouldRefresh(sp: SharedPreferences) = sp
            .getBoolean(Constants.PREF_FORCE_REFRESH, true)

        // Setters

        fun setIconPack(sp: SharedPreferences, iconPack: String) = sp
            .edit().putString(Constants.PREF_ICON_PACK, iconPack).apply()

        fun setAppList(sp: SharedPreferences, appJson: String) = sp
            .edit().putString(Constants.PREF_APP_LIST, appJson).apply()

        fun setPage(sp: SharedPreferences, page: Long) = sp
            .edit().putLong(Constants.PREF_CURRENT_PAGE, page).apply()

        private fun setForceRefresh(context: Context, b: Boolean): Boolean {
            getPrefs(context).edit().putBoolean(Constants.PREF_FORCE_REFRESH, b).apply()
            return b
        }

        fun needsRefresh(context: Context): Boolean {
            val shouldRefresh = shouldRefresh(getPrefs(context))

            if (shouldRefresh) {
                setForceRefresh(context, false)
            }

            return shouldRefresh
        }

        fun appsPerPage(sharedPrefs: SharedPreferences): Int {
            return (numOfRows(sharedPrefs) * maxAppsPerRow(sharedPrefs)) - Utils.switchPagePlaceholder(
                numOfPages(
                    sharedPrefs
                )
            )
        }
    }
}
