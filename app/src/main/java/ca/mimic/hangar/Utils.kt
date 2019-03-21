package ca.mimic.hangar

import android.app.AppOpsManager
import android.app.AppOpsManager.OPSTR_GET_USAGE_STATS
import android.app.job.JobScheduler
import android.content.Context
import android.content.Context.JOB_SCHEDULER_SERVICE
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Process
import android.util.Log
import android.util.TypedValue
import androidx.core.app.AppOpsManagerCompat.MODE_ALLOWED
import java.io.*
import java.util.*

class Utils {
    companion object {
        fun log(s: String) { Log.d("Hangar", s) }

        fun checkForUsagePermission(context: Context): Boolean {
            val appOps = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
            appOps.apply {}
            val mode = appOps.checkOpNoThrow(OPSTR_GET_USAGE_STATS, Process.myUid(), context.packageName)
            return mode == MODE_ALLOWED
        }

        fun cancelJob(context: Context, id: Int) {
            val js = context.getSystemService(JOB_SCHEDULER_SERVICE) as JobScheduler
            js.cancel(id)
        }

        fun isApi22(): Boolean {
            return android.os.Build.VERSION.SDK_INT >= 22
        }

        fun drawableToBitmap(drawable: Drawable): Bitmap {
            if (drawable is BitmapDrawable) {
                return drawable.bitmap
            }

            val bitmap = Bitmap.createBitmap(drawable.intrinsicWidth, drawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
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
                    resourceFile = FileOutputStream("${context.cacheDir}${File.separator}$packageName.png")
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

        fun loadIcon(context: Context, packageName: String): Bitmap? {
            var icon: Bitmap? = null
            var resourceFile: FileInputStream? = null
            try {
                resourceFile = FileInputStream("${context.cacheDir}${File.separator}$packageName.png")
                val buffer = ByteArray(1024)
                val bytes = ByteArrayOutputStream()
                var bytesRead = 0
                while (bytesRead >= 0) {
                    bytes.write(buffer, 0, bytesRead)
                    bytesRead = resourceFile.read(buffer, 0, buffer.size)
                }
                icon = BitmapFactory.decodeByteArray(bytes.toByteArray(), 0, bytes.size())
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
            var intent: Intent? = null
            try {
                intent = context.packageManager.getLaunchIntentForPackage(packageName)
                intent.addCategory(Intent.CATEGORY_LAUNCHER)
                intent.action = Intent.ACTION_MAIN
            } catch (e: PackageManager.NameNotFoundException) {
            }
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

    }


}