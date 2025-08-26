package ca.mimic.hangar

import android.app.Activity
import android.content.Context
import android.os.Bundle
import ca.mimic.hangar.Constants.Companion.EXTRA_PACKAGE_NAME

class TrampolineActivity : Activity() {
    private lateinit var context: Context
    private val sharedPreferences by lazy { SharedPrefsHelper(context) }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        context = this

        val packageName: String =
            intent.getStringExtra(EXTRA_PACKAGE_NAME) ?: run { finish(); return }

        if (packageName == Constants.SWITCH_APP_PACKAGE_NAME) {
            val page = incrementPageNumber(sharedPreferences)
            sharedPreferences.setPage(page)
        } else {
            AppStorage(context).launchApp(packageName)
            sharedPreferences.setPage(1)
        }
        finishAndRemoveTask()
    }

    companion object {
        fun incrementPageNumber(sharedPreferences: SharedPrefsHelper): Long {
            val page = sharedPreferences.currentPage()
            val numOfPages = sharedPreferences.numOfPages()

            return if (page + 1 > numOfPages) {
                1
            } else {
                (page + 1).toLong()
            }
        }
    }
}