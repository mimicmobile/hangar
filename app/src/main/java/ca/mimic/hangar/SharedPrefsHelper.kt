package ca.mimic.hangar

import android.content.Context
import android.content.SharedPreferences

class SharedPrefsHelper(val context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(Constants.PREFS_FILE, 0)

    fun maxAppsPerRow() = sharedPreferences
        .getLong(Constants.PREF_APPS_PER_ROW, Constants.DEFAULT_APPS_PER_ROW).toInt()

    fun numOfRows() = sharedPreferences
        .getLong(Constants.PREF_NUM_ROWS, Constants.DEFAULT_NUM_ROWS).toInt()

    fun numOfPages() = sharedPreferences
        .getLong(Constants.PREF_NUM_PAGES, Constants.DEFAULT_NUM_PAGES).toInt()

    fun currentPage() = sharedPreferences
        .getLong(Constants.PREF_CURRENT_PAGE, 1).toInt()

    fun iconSize(): String = sharedPreferences
        .getString(Constants.PREF_ICON_SIZE, Constants.DEFAULT_ICON_SIZE)!!

    fun iconPack(): String = sharedPreferences
        .getString(Constants.PREF_ICON_PACK, Constants.DEFAULT_ICON_PACK)!!

    fun jobInterval() = sharedPreferences
        .getLong(Constants.PREF_JOB_INTERVAL, Constants.DEFAULT_JOB_INTERVAL)

    fun orderPriority() = sharedPreferences
        .getString(Constants.PREF_NOTIFICATION_WEIGHT, Constants.DEFAULT_NOTIFICATION_WEIGHT)!!

    fun appList() = sharedPreferences
        .getString(Constants.PREF_APP_LIST, "[]")!!

    fun bgColor() = sharedPreferences
        .getString(Constants.PREF_BACKGROUND_COLOR, Constants.PREF_BACKGROUND_COLOR_DEFAULT)!!

    fun pinnedAppPlacement() = sharedPreferences
        .getString(Constants.PREF_PINNED_APP_PLACEMENT, Constants.DEFAULT_PINNED_APP_PLACEMENT)!!

    fun shouldRefresh() = sharedPreferences
        .getBoolean(Constants.PREF_FORCE_REFRESH, true)

    // Setters

    fun setAppList(appJson: String) = sharedPreferences
        .edit().putString(Constants.PREF_APP_LIST, appJson).apply()

    fun setPage(page: Long) = sharedPreferences
        .edit().putLong(Constants.PREF_CURRENT_PAGE, page).apply()

    fun resetForceRefresh() {
        sharedPreferences.edit().putBoolean(Constants.PREF_FORCE_REFRESH, false).apply()
    }

    fun appsPerPage(): Int {
        return (numOfRows() * maxAppsPerRow()) - Utils.switchPagePlaceholder(
            numOfPages()
        )
    }
}
