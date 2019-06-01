package ca.mimic.hangar

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import java.util.ArrayList
import android.content.Intent
import android.graphics.Bitmap
import ca.mimic.hangar.Utils.Companion.log
import kotlin.math.min

class AppStorage(private val context: Context, private var appListModified: Boolean = false) {
    private var moshi: Moshi = Moshi.Builder().build()
    private val appListType = Types.newParameterizedType(
        MutableList::class.java, App::class.java
    )!!
    private val adapter: JsonAdapter<MutableList<App>> = moshi.adapter(appListType)

    private val packages: List<ApplicationInfo> by lazy {
        val pm = context.packageManager
        pm.getInstalledApplications(PackageManager.GET_META_DATA)
    }

    val iconsHandler by lazy {
        IconsHandler(context)
    }

    private val sharedPreferences = SharedPrefsHelper(context)

    private val userWeight: Array<Float> =
        Constants.weightMap[sharedPreferences.orderPriority()] ?: Constants.defaultWeight

    internal val apps: MutableList<App> by lazy {
        adapter.fromJson(sharedPreferences.appList()).orEmpty().toMutableList()
    }

    internal val launchers: ArrayList<String> by lazy {
        val intent = Intent(Intent.ACTION_MAIN)
        intent.addCategory("android.intent.category.LAUNCHER_APP")

        filteredPackages(intent)
    }

    private val themes: ArrayList<String> by lazy {
        val intent = Intent("org.adw.launcher.THEMES")

        filteredPackages(intent)
    }

    fun iconPacksJson(packageName: String): String {
        val packsList: MutableList<App> = mutableListOf()
        val iconExists = Utils.iconExists(context, "${packageName}_generated")
        val defaultFilename = if (iconExists) "${packageName}_generated" else iconsHandler.generateBitmapFromIconPack(
            packageName,
            "default"
        )

        packsList.add(
            App(
                "Default",
                "default",
                cachedFile = Utils.iconFromCache(context, defaultFilename).absolutePath
            )
        )

        for (theme in themes) {
            val app = newApp(theme, skipSave = true)
            app.cachedFile = Utils.iconFromCache(context, app.safeCachedFile).absolutePath
            packsList.add(app)
        }
        return adapter.toJson(packsList)
    }

    private fun filteredPackages(intent: Intent): ArrayList<String> {
        val pm = context.packageManager
        val filteredPackageNames = ArrayList<String>()
        val activityInfoList = pm.queryIntentActivities(intent, 0)
        for (resolveInfo in activityInfoList) {
            resolveInfo.activityInfo?.let {
                filteredPackageNames.add(it.applicationInfo.packageName)
            }
        }
        return filteredPackageNames
    }

    fun savePrefs(): Boolean {
        if (!appListModified) return false

        calculateSortScore()

        val sortedApps = getSortedApps()
        val appJson = adapter.toJson(sortedApps)
        sharedPreferences.setAppList(appJson)

        return true
    }

    fun updateAppIcon(packageName: String, filename: String) {
        getApp(packageName)?.let {
            it.cachedFile = filename
            appListModified = true
            savePrefs()
        }
    }

    private fun getApp(packageName: String): App? {
        return apps.find { it.packageName == packageName }
    }

    private fun newApp(packageName: String, skipSave: Boolean = false): App {
        val ai = packages.find { it.packageName == packageName }

        val installedApp = App(
            name = ai?.loadLabel(context.packageManager).toString(),
            packageName = packageName,
            isSystemApp = isSystemApp(ai)
        )

        if (!installedApp.emptyName()) {
            log("new app: $installedApp")
            generateIcon(context, packageName, installedApp)

            if (!skipSave) {
                apps.add(installedApp)
            }
        }

        return installedApp
    }

    private fun generateIcon(context: Context, packageName: String, app: App) {
        Utils.getLaunchIntent(context, packageName)?.component?.let {
            val filename = "${packageName}_${System.currentTimeMillis()}"
            val bitmapData = iconsHandler.getBitmapForPackage(filename, it, android.os.Process.myUserHandle())
            val cachedIcon = iconsHandler.cacheStoreBitmap(filename, bitmapData["bitmap"] as Bitmap?)
            val customIcon = bitmapData["customIcon"] as Boolean?

            if (customIcon != null) {
                app.customIcon = customIcon
            }
            app.cachedIcon = cachedIcon
            app.cachedFile = filename
        }
    }

    fun launchApp(packageName: String) {
        val app = getApp(packageName) ?: newApp(packageName)
        app.timesLaunched += 1
        appListModified = true
        savePrefs()

        val intent = Utils.getLaunchIntent(context, packageName)
        context.startActivity(intent)
    }

    fun checkApp(
        packageName: String,
        totalTimeInForeground: Long,
        lastTimeUsed: Long
    ) {
        val app = getApp(packageName) ?: newApp(packageName)

        if (isAppModified(app, totalTimeInForeground, lastTimeUsed)) {
            app.totalTimeInForeground = totalTimeInForeground
            app.lastTimeUsed = lastTimeUsed
            app.timesUpdated += 1

            appListModified = true

            log("updated app: $app")
        }
        if (app.cachedIcon && !Utils.iconExists(context, app.safeCachedFile!!)) {
            generateIcon(context, packageName, app)

            appListModified = true

            log("generated app icon: $app")
        }
    }

    private fun calculateSortScore() {
        val totalTimeList = apps.sortedByDescending { it.totalTimeInForeground }
        val lastTimeUsed = apps.sortedByDescending { it.lastTimeUsed }
        val timesUpdated = apps.sortedByDescending { it.timesUpdated }
        val timesLaunched = apps.sortedByDescending { it.timesLaunched }

        for (app in apps) {
            val totalTimeListPos = totalTimeList.lastIndexOf(app)
            val lastTimeUsedPos = lastTimeUsed.lastIndexOf(app)
            val timesUpdatedPos = timesUpdated.lastIndexOf(app)
            val timesLaunchedPos = timesLaunched.lastIndexOf(app)

            app.sortScore = getAdjustedScore(
                getPercentile(totalTimeListPos, apps.size),
                getPercentile(lastTimeUsedPos, apps.size),
                getPercentile(timesUpdatedPos, apps.size),
                getPercentile(timesLaunchedPos, apps.size)
            )
        }
    }

    private fun getAdjustedScore(
        totalTimeScore: Float,
        lastTimeUsedScore: Float,
        timesUpdatedScore: Float,
        timesLaunchedScore: Float
    ): Float {
        return ((totalTimeScore * userWeight[0]) + (lastTimeUsedScore * userWeight[1]) + (timesUpdatedScore * userWeight[2]) + (timesLaunchedScore * userWeight[3]))
    }

    private fun getSortedApps(): MutableList<App> {
        val sortedList = apps.filter { !it.pinned }.sortedByDescending { it.sortScore }.toMutableList()
        val pinned = apps.filter { it.pinned }.sortedByDescending { it.sortScore }

        val fullPageCount = sharedPreferences.appsPerPage() - pinned.size

        val index = when (sharedPreferences.pinnedAppPlacement()) {
            Constants.DEFAULT_PINNED_APP_PLACEMENT -> 0  // Pin to front
            else -> getIndex(sortedList, fullPageCount)
        }

        sortedList.addAll(index, pinned)
        return sortedList
    }

    private fun getIndex(sortedList: MutableList<App>, fullPageCount: Int): Int {
        var indexCount = 0
        var pageCount = 0

        while (pageCount < min(sortedList.size - 2, fullPageCount)) {
            val app = sortedList[indexCount]
            indexCount += 1
            if (!app.blacklisted) {
                pageCount += 1
            }
        }
        return indexCount
    }

    private fun getPercentile(rank: Int, size: Int): Float {
        return (size - rank) / size.toFloat() * 100
    }

    private fun isAppModified(app: App, totalTimeInForeground: Long, lastTimeUsed: Long): Boolean {
        return !app.emptyName() && (app.totalTimeInForeground != totalTimeInForeground || app.lastTimeUsed != lastTimeUsed)
    }

    private fun isSystemApp(ai: ApplicationInfo?): Boolean {
        return ai != null && return ai.flags and ApplicationInfo.FLAG_SYSTEM != 0
    }
}