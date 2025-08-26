package ca.mimic.hangar

import android.annotation.SuppressLint
import android.app.AppOpsManager
import android.app.AppOpsManager.OPSTR_GET_USAGE_STATS
import android.app.job.JobScheduler
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Context.JOB_SCHEDULER_SERVICE
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.hardware.display.DisplayManager
import android.os.Build
import android.os.Process
import android.util.Log
import android.util.TypedValue
import android.view.Display
import androidx.core.app.AppOpsManagerCompat.MODE_ALLOWED
import androidx.core.graphics.createBitmap
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.util.Calendar
import java.util.Random
import java.util.concurrent.TimeUnit

class Utils {
    companion object {
        fun log(s: String) {
            Log.d("Hangar", s)
        }

        private fun visibleLauncherPackages(context: Context): Set<String> {
            val pm = context.packageManager
            val intent = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER)

            val resolves = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                pm.queryIntentActivities(intent, PackageManager.ResolveInfoFlags.of(0))
            } else {
                @Suppress("DEPRECATION")
                pm.queryIntentActivities(intent, 0)
            }
            return resolves.map { it.activityInfo.packageName }.toSet()
        }

        private fun getApplicationInfoCompat(pm: PackageManager, pkg: String): ApplicationInfo? {
            return try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    pm.getApplicationInfo(pkg, PackageManager.ApplicationInfoFlags.of(0))
                } else {
                    @Suppress("DEPRECATION")
                    pm.getApplicationInfo(pkg, 0)
                }
            } catch (_: PackageManager.NameNotFoundException) {
                null
            }
        }

        fun getUsageStats(context: Context, forceModified: Boolean = false): Boolean {
            if (!isScreenOn(context)) return false

            val end = System.currentTimeMillis()
            val begin = end - TimeUnit.DAYS.toMillis(7)  // tighter window keeps results relevant

            val usage = getUsageStatsManager(context)
                .queryAndAggregateUsageStats(begin, end)
                .toList()

            val appStorage = AppStorage(context, forceModified)
            val visible = visibleLauncherPackages(context)

            log("usage.size=${usage.size} visible.launcher=${visible.size}")

            usage.asSequence()
                .map { it.second } // UsageStats
                .filter { us ->
                    us.lastTimeUsed > 100_000 &&
                            us.totalTimeInForeground > 0 &&
                            !Constants.IGNORED_PACKAGES.contains(us.packageName) &&
                            !appStorage.launchers.contains(us.packageName) &&
                            visible.contains(us.packageName)
                }
                .forEach { us ->
                    appStorage.checkApp(
                        us.packageName,
                        lastTimeUsed = us.lastTimeUsed,
                        totalTimeInForeground = us.totalTimeInForeground
                    )
                }

            return appStorage.savePrefs()
        }

        @SuppressLint("WrongConstant")
        private fun getUsageStatsManager(context: Context): UsageStatsManager {
            return context.getSystemService(if (isApi22()) Context.USAGE_STATS_SERVICE else "usagestats") as UsageStatsManager
        }

        private fun getBeginTimeMillis(): Long {
            val cal = Calendar.getInstance()
            cal.add(Calendar.YEAR, -1)

            return cal.timeInMillis
        }

        fun switchPagePlaceholder(numOfPages: Int): Int {
            return if (numOfPages > 1) 1 else 0
        }

        fun checkForUsagePermission(context: Context): Boolean {
            val appOps = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
            appOps.apply {}
            val mode =
                appOps.checkOpNoThrow(OPSTR_GET_USAGE_STATS, Process.myUid(), context.packageName)
            return mode == MODE_ALLOWED
        }

        fun cancelJob(context: Context, id: Int) {
            val js = context.getSystemService(JOB_SCHEDULER_SERVICE) as JobScheduler
            js.cancel(id)
        }

        fun isApi22(): Boolean {
            return Build.VERSION.SDK_INT >= 22
        }

        fun drawableToBitmap(drawable: Drawable): Bitmap {
            if (drawable is BitmapDrawable) {
                return drawable.bitmap
            }

            val bitmap = createBitmap(drawable.intrinsicWidth, drawable.intrinsicHeight)
            val canvas = Canvas(bitmap)
            drawable.setBounds(0, 0, canvas.width, canvas.height)
            drawable.draw(canvas)

            return bitmap
        }

        fun saveIcon(context: Context, packageName: String): Boolean {
            try {
                val resourceFile: FileOutputStream
                val icon = context.packageManager.getApplicationIcon(packageName)
                val bitmap = drawableToBitmap(icon)

                try {
                    resourceFile =
                        FileOutputStream("${context.cacheDir}${File.separator}$packageName.png")
                    bitmap.compress(Bitmap.CompressFormat.PNG, 90, resourceFile)

                    resourceFile.flush()
                    resourceFile.close()

                    return true
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            } catch (ignored: PackageManager.NameNotFoundException) {
            }
            return false
        }

        fun iconFromCache(context: Context, filename: String?): File {
            return File("${context.cacheDir}${File.separator}$filename.png")
        }

        fun iconExists(context: Context, filename: String): Boolean {
            return iconFromCache(context, filename).exists()
        }

        fun loadIcon(context: Context, packageName: String): Bitmap? {
            var icon: Bitmap? = null
            var resourceFile: FileInputStream? = null
            try {
                if (packageName == Constants.SWITCH_APP_PACKAGE_NAME) {
                    icon =
                        BitmapFactory.decodeResource(context.resources, R.drawable.ic_switch_page)
                } else {
                    resourceFile =
                        FileInputStream("${context.cacheDir}${File.separator}$packageName.png")
                    val buffer = ByteArray(1024)
                    val bytes = ByteArrayOutputStream()
                    var bytesRead = 0
                    while (bytesRead >= 0) {
                        bytes.write(buffer, 0, bytesRead)
                        bytesRead = resourceFile.read(buffer, 0, buffer.size)
                    }
                    icon = BitmapFactory.decodeByteArray(bytes.toByteArray(), 0, bytes.size())
                }
                if (icon == null) {
                    log("failed to decode pre-load icon for $packageName")
                }
            } catch (e: FileNotFoundException) {
                log("there is no restored icon for: $packageName")
            } catch (e: IOException) {
                log("failed to read pre-load icon for: $packageName")
            } finally {
                if (resourceFile != null) {
                    try {
                        resourceFile.close()
                    } catch (e: IOException) {
                        log("failed to read pre-load icon for: $packageName")
                    }

                }
            }

            return icon
        }

        fun getLaunchIntent(context: Context, packageName: String): Intent? {
            val intent = context.packageManager.getLaunchIntentForPackage(packageName)
            intent?.addCategory(Intent.CATEGORY_LAUNCHER)
            intent?.action = Intent.ACTION_MAIN
            return intent
        }

        fun dpToPx(context: Context, dp: Int): Int {
            return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp.toFloat(),
                context.resources.displayMetrics
            ).toInt()
        }

        fun getRandomNumber(limit: Int): Int {
            return Random().nextInt(limit) + 1
        }

        fun isScreenOn(context: Context): Boolean {
            val dm = context.getSystemService(Context.DISPLAY_SERVICE) as DisplayManager
            for (display in dm.displays) {
                if (display.state != Display.STATE_OFF) {
                    return true
                }
            }
            return false
        }
    }
}