package ca.mimic.hangar

import com.squareup.moshi.JsonClass
import java.text.SimpleDateFormat
import java.util.*

@JsonClass(generateAdapter = true)
data class App(
    val name: String?,
    val packageName: String,
    var cachedIcon: Boolean = false,
    val isSystemApp: Boolean = false,
    // UsageStats
    var totalTimeInForeground: Long = 0L,
    var lastTimeUsed: Long = 0L,
    // Variable
    var lastUpdated: Long = System.currentTimeMillis(),
    var timesLaunched: Long = 0L,
    var timesUpdated: Long = 0L,
    var sortScore: Float = 0f,
    var pinned: Boolean = false,
    var blacklisted: Boolean = false
) {
    fun lastTimeDate(): String {
        return getSDF().format(Date(this.lastTimeUsed))
    }

    fun lastUpdatedDate(): String {
        return getSDF().format(Date(this.lastUpdated))
    }

    private fun getSDF(): SimpleDateFormat {
        return SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)
    }

    fun emptyName(): Boolean {
        return this.name.isNullOrEmpty() || this.name == "null"
    }
}
