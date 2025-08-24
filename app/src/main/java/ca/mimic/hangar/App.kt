package ca.mimic.hangar

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
    var blacklisted: Boolean = false,
    var customIcon: Boolean = false,
    var cachedFile: String? = null
) {
    val safeCachedFile: String?
        get() = cachedFile ?: packageName

    fun emptyName(): Boolean {
        return this.name.isNullOrEmpty() || this.name == "null"
    }
}
