package ca.mimic.hangar

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_LOW
import android.app.PendingIntent
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.os.Build
import android.util.TypedValue
import android.widget.RemoteViews
import androidx.annotation.ColorInt
import androidx.core.app.NotificationCompat.Builder
import androidx.core.app.NotificationCompat.PRIORITY_LOW
import androidx.core.app.NotificationCompat.VISIBILITY_PUBLIC
import androidx.core.content.ContextCompat
import androidx.core.graphics.scale
import ca.mimic.hangar.Constants.Companion.EXTRA_PACKAGE_NAME
import ca.mimic.hangar.Constants.Companion.NOTIFICATION_ID
import ca.mimic.hangar.Constants.Companion.RECEIVER_APP_LAUNCHED
import ca.mimic.hangar.Constants.Companion.SWITCH_APP_PACKAGE_NAME
import java.lang.Float.max
import kotlin.math.ceil
import kotlin.math.min
import kotlin.math.roundToInt

class NotificationShortcuts(private val context: Context) {
    private val root: RemoteViews =
        RemoteViews(context.packageName, R.layout.notification_no_dividers)
    private val expanded: RemoteViews =
        RemoteViews(context.packageName, R.layout.notification_no_dividers)

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
        (1..numOfRows).forEach { _ ->
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
                Constants.PREF_BACKGROUND_COLOR_DARK -> notificationDrawerBgColor(
                    context,
                    preferDark = true
                )

                Constants.PREF_BACKGROUND_COLOR_BLACK -> Color.BLACK
                else -> notificationDrawerBgColor(context, preferDark = false)
            }
            view.setInt(R.id.notifContainer, "setBackgroundColor", colorInt)
        }
    }

    private fun createNotification() {
        getOrCreateChannel()

        val builder = Builder(context, Constants.NOTIFICATION_CHANNEL_ID)
            // .setContentTitle("Hangar")
            // .setContentText("Hangar")
            .setSmallIcon(R.drawable.notification_small_icon)
            .setOngoing(true)
            .setWhen(System.currentTimeMillis())
            .setShowWhen(false)
            .setSilent(true)
            .setPriority(PRIORITY_LOW)
            .setCustomContentView(root)
            .setCustomBigContentView(expanded)
            // .setStyle(DecoratedCustomViewStyle())
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
                chan =
                    NotificationChannel(Constants.NOTIFICATION_CHANNEL_ID, "Hangar", IMPORTANCE_LOW)
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
        val appContainer =
            RemoteViews(context.packageName, Constants.iconSizeMap[iconSize] ?: error(""))

        appContainer.setContentDescription(R.id.imageButton, app.name)
        val sizePx = Utils.dpToPx(context, 48)      // e.g., 40–48dp collapsed; 48–56dp expanded
        val bgColor = when (sharedPreferences.bgColor()) {
            Constants.PREF_BACKGROUND_COLOR_DARK -> notificationDrawerBgColor(
                context,
                preferDark = true
            )

            Constants.PREF_BACKGROUND_COLOR_BLACK -> Color.BLACK
            else -> notificationDrawerBgColor(context, preferDark = false)
        }
        setIconForApp(app, appContainer, context, bgColor, sizePx)
        setLaunchIntentForApp(app, appContainer, context)

        return appContainer
    }

    @ColorInt
    fun notificationDrawerBgColor(
        context: Context,
        preferDark: Boolean? = null,
        @ColorInt fallbackLight: Int = 0xFFFFFFFF.toInt(), // white
        @ColorInt fallbackDark: Int = 0xFF121212.toInt()  // material dark surface
    ): Int {
        val isNight =
            (context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) ==
                    Configuration.UI_MODE_NIGHT_YES
        val useDark = preferDark ?: isNight

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val resId = if (useDark) android.R.color.system_neutral1_900
            else android.R.color.system_neutral1_50
            try {
                return ContextCompat.getColor(context, resId)
            } catch (_: Throwable) { /* fall through */
            }
        }

        val tv = TypedValue()
        if (context.theme.resolveAttribute(android.R.attr.colorBackground, tv, true)) {
            if (tv.resourceId != 0) return ContextCompat.getColor(context, tv.resourceId)
            if (tv.type in TypedValue.TYPE_FIRST_COLOR_INT..TypedValue.TYPE_LAST_COLOR_INT) return tv.data
        }
        return if (useDark) fallbackDark else fallbackLight
    }

    /**
     * Fit-centers the raw icon into sizePx x sizePx, NO padding/shape.
     * Fills the canvas with bgColor, then draws the icon, then returns RGB_565.
     */
    fun buildNotifIconRawOnBg565(
        src: Bitmap,
        sizePx: Int,
        @ColorInt bgColor: Int,
        upscale: Boolean = true
    ): Bitmap {
        // Composite on ARGB to avoid halos, then convert to 565
        val outArgb = Bitmap.createBitmap(sizePx, sizePx, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(outArgb)

        // Solid background (square)
        val bgPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = bgColor }
        canvas.drawRect(0f, 0f, sizePx.toFloat(), sizePx.toFloat(), bgPaint)

        // Fit-center scale
        var scale = minOf(sizePx.toFloat() / src.width, sizePx.toFloat() / src.height)
        if (!upscale) scale = minOf(1f, scale)

        val dstW = max(1f, src.width * scale).roundToInt()
        val dstH = max(1f, src.height * scale).roundToInt()
        val left = (sizePx - dstW) / 2
        val top = (sizePx - dstH) / 2

        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            isFilterBitmap = true
            isDither = true
        }
        canvas.drawBitmap(src, null, Rect(left, top, left + dstW, top + dstH), paint)

        return outArgb.copy(Bitmap.Config.RGB_565, false)
    }

    private fun setIconForApp(
        app: App,
        appContainer: RemoteViews,
        context: Context,
        bgColor: Int,
        sizePx: Int
    ) {
        Utils.loadIcon(context, app.safeCachedFile!!)?.let { src ->
            val bmp = buildNotifIconRawOnBg565(src, sizePx, bgColor, upscale = true)
            appContainer.setImageViewBitmap(R.id.imageButton, bmp)
        }
    }

    private fun safeScaledNotificationBitmap(src: Bitmap, sizePx: Int): Bitmap {
        val scaled = if (src.width != sizePx || src.height != sizePx) {
            src.scale(sizePx, sizePx)
        } else src
        // Use RGB_565 to halve memory; immutable copy
        return if (scaled.config != Bitmap.Config.RGB_565) {
            scaled.copy(Bitmap.Config.RGB_565, false)
        } else scaled
    }

    private fun setLaunchIntentForApp(app: App, appContainer: RemoteViews, context: Context) {
        var pi: PendingIntent?
        if (app.packageName == SWITCH_APP_PACKAGE_NAME) {
            val intent = Intent(context, HangarReceiver::class.java)
                .putExtra(EXTRA_PACKAGE_NAME, app.packageName)
                .setAction(RECEIVER_APP_LAUNCHED)

            pi = PendingIntent.getBroadcast(
                context, Utils.getRandomNumber(1000), intent,
                PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        } else {
            val tramp = Intent(context, TrampolineActivity::class.java).apply {
                putExtra(EXTRA_PACKAGE_NAME, app.packageName)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }

            pi = PendingIntent.getActivity(
                context,
                app.packageName.hashCode(),
                tramp,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        }

        appContainer.setOnClickPendingIntent(R.id.imageCont, pi)
    }
}