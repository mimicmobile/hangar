package ca.mimic.hangar

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.*
import android.app.PendingIntent
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Build
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.*
import androidx.core.content.ContextCompat
import ca.mimic.hangar.Constants.Companion.EXTRA_PACKAGE_NAME
import ca.mimic.hangar.Constants.Companion.NOTIFICATION_ID
import ca.mimic.hangar.Constants.Companion.RECEIVER_APP_LAUNCHED
import ca.mimic.hangar.Constants.Companion.SWITCH_APP_PACKAGE_NAME
import kotlin.math.ceil
import kotlin.math.min

class NotificationShortcuts(private val context: Context) {
    private val root: RemoteViews = RemoteViews(context.packageName, R.layout.notification_no_dividers)
    private val expanded: RemoteViews = RemoteViews(context.packageName, R.layout.notification_no_dividers)

    private val rows: MutableList<RemoteViews> = mutableListOf()

    private val appStorage: AppStorage by lazy {
        AppStorage(context)
    }

    private val sharedPreferences = SharedPrefsHelper(context)
    private val maxAppsPerRow = sharedPreferences.maxAppsPerRow()
    private val numOfRows = sharedPreferences.numOfRows()
    private val numOfPages = sharedPreferences.numOfPages()
    private val currentPage = sharedPreferences.currentPage()
    private val iconSize = sharedPreferences.iconSize()
    private val appsPerPage = sharedPreferences.appsPerPage()

    private val startOfRange = appsPerPage * (currentPage - 1)
    private val totalAppsToGet = appsPerPage * numOfPages

    init {
        addRows()
        if (isReady()) {
            val appList = appStorage.apps.filter { !it.blacklisted }.take(totalAppsToGet)

            var positionOfApp = 0
            for (i in startOfRange until endOfRange(appList.size)) {
                addApp(positionOfApp, appList[i])
                positionOfApp += 1
            }

            if (numOfPages > 1) {
                addSwitchPage(positionOfApp)
            }

            createRootContainer()
        }
    }

    fun create() {
        if (isReady())
            createNotification()
    }

    private fun addRows() {
        for (i in 1..numOfRows) {
            rows.add(RemoteViews(context.packageName, R.layout.notification_row_no_dividers))
        }
    }

    private fun isReady(): Boolean {
        return appStorage.apps.isNotEmpty()
    }

    private fun endOfRange(appListSize: Int): Int {
        return min(startOfRange + appsPerPage, appListSize)
    }

    private fun addApp(index: Int, app: App) {
        val row = getRowForApp(index)
        row?.addView(R.id.notifRow, createAppContainer(app))
    }

    private fun getRowForApp(appIndex: Int): RemoteViews? {
        val rowIndex = ceil((appIndex + 1.0) / maxAppsPerRow).toInt() - 1
        return rows.getOrNull(rowIndex)
    }

    private fun addSwitchPage(positionOfApp: Int) {
        addApp(positionOfApp, App("Switch page", packageName = SWITCH_APP_PACKAGE_NAME))
    }

    private fun createRootContainer() {
        root.removeAllViews(R.id.notifContainer)
        expanded.removeAllViews(R.id.notifContainer)

        for (row in rows) {
            expanded.addView(R.id.notifContainer, row)
        }
        root.addView(R.id.notifContainer, rows[0])
        setBackgroundColor(arrayListOf(root, expanded))
    }

    private fun setBackgroundColor(views: List<RemoteViews>) {
        val bgColorPref = sharedPreferences.bgColor()
        for (view in views) {
            val colorInt = when (bgColorPref) {
                Constants.PREF_BACKGROUND_COLOR_DARK -> ContextCompat.getColor(context, R.color.darkBg)
                Constants.PREF_BACKGROUND_COLOR_BLACK -> ContextCompat.getColor(context, R.color.blackBg)
                else -> Color.WHITE
            }
            view.setInt(R.id.notifContainer, "setBackgroundColor", colorInt)
        }
    }

    private fun createNotification() {
        getOrCreateChannel()

        val builder = NotificationCompat.Builder(context, Constants.NOTIFICATION_CHANNEL_ID)
            .setContentTitle("Hangar")
            .setContentText("Hangar")
            .setSmallIcon(R.drawable.notification_small_icon)
            .setCustomContentView(root)
            .setOngoing(true)
            .setWhen(System.currentTimeMillis())
            .setPriority(PRIORITY_MAX)
            .setCustomBigContentView(expanded)
            .setVisibility(VISIBILITY_PUBLIC)

        val notification = builder.build()
        getNotificationManager().notify(NOTIFICATION_ID, notification)
    }

    private fun getOrCreateChannel(): NotificationChannel? {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = getNotificationManager()

            var chan: NotificationChannel? =
                notificationManager.getNotificationChannel(Constants.NOTIFICATION_CHANNEL_ID)

            chan?.apply {
                enableLights(false)
                enableVibration(false)
                setSound(null, null)
            }

            if (chan == null) {
                chan = NotificationChannel(Constants.NOTIFICATION_CHANNEL_ID, "Hangar", IMPORTANCE_LOW)
                notificationManager.createNotificationChannel(chan)
            }
            return chan
        }
        return null
    }


    private fun getNotificationManager(): NotificationManager {
        return context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
    }

    private fun createAppContainer(app: App): RemoteViews {
        val appContainer = RemoteViews(context.packageName, Constants.iconSizeMap[iconSize] ?: error(""))

        appContainer.setContentDescription(R.id.imageButton, app.name)

        setIconForApp(app, appContainer, context)
        setLaunchIntentForApp(app, appContainer, context)

        return appContainer
    }

    private fun setIconForApp(app: App, appContainer: RemoteViews, context: Context) {
        Utils.loadIcon(context, app.safeCachedFile!!)?.let {
            val iconSize = Utils.dpToPx(context, 72)
            appContainer.setImageViewBitmap(
                R.id.imageButton,
                Bitmap.createScaledBitmap(it, iconSize, iconSize, true)
            )
        }
    }

    private fun setLaunchIntentForApp(app: App, appContainer: RemoteViews, context: Context) {
        val intent = Intent(context, HangarReceiver::class.java)
            .putExtra(EXTRA_PACKAGE_NAME, app.packageName)
            .setAction(RECEIVER_APP_LAUNCHED)

        val activity = PendingIntent.getBroadcast(
            context, Utils.getRandomNumber(1000), intent,
            PendingIntent.FLAG_CANCEL_CURRENT
        )

        appContainer.setOnClickPendingIntent(R.id.imageCont, activity)
    }
}