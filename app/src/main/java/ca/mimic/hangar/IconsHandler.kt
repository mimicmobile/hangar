package ca.mimic.hangar

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.content.pm.LauncherApps
import android.content.pm.PackageManager
import android.content.pm.PackageManager.NameNotFoundException
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Bitmap.CompressFormat
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.UserHandle
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.createBitmap
import androidx.core.graphics.drawable.toBitmap
import androidx.core.graphics.scale
import ca.mimic.hangar.Utils.Companion.log
import org.xmlpull.v1.XmlPullParser
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.Random

/**
 * Inspired from http://stackoverflow.com/questions/31490630/how-to-load-icon-from-icon-pack
 */

class IconsHandler(val context: Context) {
    // map with available drawable for an icons pack
    private val packagesDrawables = HashMap<String, String>()

    // instance of a resource object of an icon pack
    private lateinit var iconPackRes: Resources

    // package name of the icons pack
    private lateinit var iconsPackPackageName: String

    // list of back images available on an icons pack
    private val backImages = ArrayList<Bitmap>()

    // bitmap mask of an icons pack
    private var maskImage: Bitmap? = null

    // front image of an icons pack
    private var frontImage: Bitmap? = null

    // scale factor of an icons pack
    private var factor = 1.0f
    private val pm: PackageManager = context.packageManager

    private val iconsCacheDir: File
        get() = File("${context.cacheDir}")

    init {
        loadIconsPack()
    }

    /**
     * Load configured icons pack
     */
    @SuppressLint("DiscouragedApi")
    fun loadIconsPack() {
        //clear icons pack
        iconsPackPackageName = SharedPrefsHelper(context).iconPack()
        log("Load iconPack [$iconsPackPackageName]")
        packagesDrawables.clear()
        backImages.clear()

        // system icons, nothing to do
        if (iconsPackPackageName == "default") {
            return
        }

        iconPackRes = pm.getResourcesForApplication(iconsPackPackageName)

        var xpp: XmlPullParser? = null

        try {
            // search appfilter.xml into icons pack apk resource folder
            val appFilterId = iconPackRes.getIdentifier("appfilter", "xml", iconsPackPackageName)
            if (appFilterId > 0) {
                xpp = iconPackRes.getXml(appFilterId)
            }

            if (xpp != null) {
                var eventType = xpp.eventType
                while (eventType != XmlPullParser.END_DOCUMENT) {
                    if (eventType == XmlPullParser.START_TAG) {
                        // parse <iconback> xml tags used as background of generated icons
                        if (xpp.name == "iconback") {
                            for (i in 0 until xpp.attributeCount) {
                                if (xpp.getAttributeName(i).startsWith("img")) {
                                    val drawableName = xpp.getAttributeValue(i)
                                    val iconback = loadBitmap(drawableName)
                                    if (iconback != null) {
                                        backImages.add(iconback)
                                    }
                                }
                            }
                        } else if (xpp.name == "iconmask") {
                            if (xpp.attributeCount > 0 && xpp.getAttributeName(0) == "img1") {
                                val drawableName = xpp.getAttributeValue(0)
                                maskImage = loadBitmap(drawableName)
                            }
                        } else if (xpp.name == "iconupon") {
                            if (xpp.attributeCount > 0 && xpp.getAttributeName(0) == "img1") {
                                val drawableName = xpp.getAttributeValue(0)
                                frontImage = loadBitmap(drawableName)
                            }
                        } else if (xpp.name == "scale") {
                            if (xpp.attributeCount > 0 && xpp.getAttributeName(0) == "factor") {
                                factor = java.lang.Float.valueOf(xpp.getAttributeValue(0))
                            }
                        }
                        // parse <scale> xml tags used as scale factor of original bitmap icon
                        // parse <iconupon> xml tags used as front image of generated icons
                        // parse <iconmask> xml tags used as mask of generated icons
                        // parse <item> xml tags for custom icons
                        if (xpp.name == "item") {
                            var componentName: String? = null
                            var drawableName: String? = null

                            for (i in 0 until xpp.attributeCount) {
                                if (xpp.getAttributeName(i) == "component") {
                                    componentName = xpp.getAttributeValue(i)
                                } else if (xpp.getAttributeName(i) == "drawable") {
                                    drawableName = xpp.getAttributeValue(i)
                                }
                            }
                            if (!packagesDrawables.containsKey(componentName) && drawableName != null) {
                                packagesDrawables[componentName!!] = drawableName
                            }
                        }
                    }
                    eventType = xpp.next()
                }
            }
        } catch (e: Exception) {
            log("Error parsing appfilter.xml $e")
        }

    }

    @SuppressLint("DiscouragedApi")
    private fun loadBitmap(drawableName: String): Bitmap? {
        val id = iconPackRes.getIdentifier(drawableName, "drawable", iconsPackPackageName)
        if (id > 0) {
            val bitmap = ResourcesCompat.getDrawable(iconPackRes, id, null)
            if (bitmap is BitmapDrawable) {
                return bitmap.bitmap
            }
        }
        return null
    }

    fun generateBitmapFromIconPack(packageName: String, iconPack: String): String? {
        Utils.getLaunchIntent(context, packageName)?.component?.let {
            val filename = getGeneratedIconFilename(packageName)
            val bitmapData =
                getBitmapForPackage(filename, it, android.os.Process.myUserHandle(), iconPack)
            cacheStoreBitmap(filename, bitmapData["bitmap"] as Bitmap?)
            return filename
        }
        return null
    }

    fun getGeneratedIconFilename(packageName: String): String {
        return "${packageName}_generated"
    }

    private fun getDefaultAppDrawable(
        componentName: ComponentName,
        userHandle: UserHandle
    ): Drawable? {
        return try {
            val launcher = context.getSystemService(Context.LAUNCHER_APPS_SERVICE) as LauncherApps
            val info = launcher.getActivityList(componentName.packageName, userHandle)[0]
            return info.getBadgedIcon(0)
        } catch (e: NameNotFoundException) {
            log("Unable to found component $componentName$e")
            null
        } catch (e: IndexOutOfBoundsException) {
            log("Unable to found component $componentName$e")
            null
        }

    }


    /**
     * Get or generate icon for an app
     */
    @SuppressLint("DiscouragedApi")
    fun getBitmapForPackage(
        filename: String,
        componentName: ComponentName,
        userHandle: UserHandle,
        iconPack: String? = null
    ): Map<String, Any?> {
        // Search first in cache
        val systemBitmap = cacheGetBitmap(filename)
        if (systemBitmap != null) {
            return mapOf("bitmap" to systemBitmap)
        }

        // system icons, nothing to do
        if ((iconPack ?: iconsPackPackageName) == "default") {
            return mapOf(
                "customIcon" to false,
                "bitmap" to getDefaultAppDrawable(componentName, userHandle)?.toBitmap()
            )
        }

        val drawable = packagesDrawables[componentName.toString()]
        if (drawable != null) { // there is a custom icon
            val id =
                iconPackRes.getIdentifier(drawable, "drawable", (iconPack ?: iconsPackPackageName))
            if (id > 0) {
                try {
                    return mapOf(
                        "customIcon" to true,
                        "bitmap" to ResourcesCompat.getDrawable(iconPackRes, id, null)?.toBitmap()
                    )
                } catch (e: Resources.NotFoundException) {
                    // Unable to load icon, keep going.

                    e.printStackTrace()
                }

            }
        }

        val systemDrawable = getDefaultAppDrawable(componentName, userHandle)
        if (systemDrawable != null) {
            val generated = generateBitmap(systemDrawable)
            return mapOf("customIcon" to false, "bitmap" to generated)
        }
        return mapOf("customIcon" to true, "bitmap" to systemBitmap)
    }

    private fun generateBitmap(defaultDrawable: Drawable): Bitmap {

        if (defaultDrawable is BitmapDrawable) {
            return defaultDrawable.bitmap
        }

        // if no support images in the icon pack return the bitmap itself
        if (backImages.isEmpty()) {
            return defaultDrawable.toBitmap()
        }

        // select a random background image
        val r = Random()
        val backImageInd = r.nextInt(backImages.size)
        val backImage = backImages[backImageInd]
        val w = backImage.width
        val h = backImage.height

        // create a bitmap for the result
        val result = createBitmap(w, h)
        val canvas = Canvas(result)

        // draw the background first
        canvas.drawBitmap(backImage, 0f, 0f, null)

        // scale original icon
        val scaledBitmap =
            (defaultDrawable.toBitmap()).scale((w * factor).toInt(), (h * factor).toInt(), false)

        if (maskImage != null) {
            // draw the scaled bitmap with mask
            val mutableMask = createBitmap(w, h)
            val maskCanvas = Canvas(mutableMask)
            maskCanvas.drawBitmap(maskImage!!, 0f, 0f, Paint())

            // paint the bitmap with mask into the result
            val paint = Paint(Paint.ANTI_ALIAS_FLAG)
            paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_OUT)
            canvas.drawBitmap(
                scaledBitmap,
                ((w - scaledBitmap.width) / 2).toFloat(),
                ((h - scaledBitmap.height) / 2).toFloat(),
                null
            )
            canvas.drawBitmap(mutableMask, 0f, 0f, paint)
            paint.xfermode = null
        } else { // draw the scaled bitmap without mask
            canvas.drawBitmap(
                scaledBitmap,
                ((w - scaledBitmap.width) / 2).toFloat(),
                ((h - scaledBitmap.height) / 2).toFloat(),
                null
            )
        }

        // paint the front
        if (frontImage != null) {
            canvas.drawBitmap(frontImage!!, 0f, 0f, null)
        }

        return result
    }

    private fun isDrawableInCache(key: String): Boolean {
        val drawableFile = cacheGetFileName(key)
        return drawableFile.isFile
    }

    internal fun cacheStoreBitmap(key: String, bitmap: Bitmap?): Boolean {
        if (bitmap == null) {
            return false
        }

        val fos: FileOutputStream
        try {
            fos = FileOutputStream(cacheGetFileName(key))
            bitmap.compress(CompressFormat.PNG, 90, fos)
            fos.flush()
            fos.close()
            return true
        } catch (e: Exception) {
            log("Unable to store bitmap in cache $e")
        }
        return false
    }

    private fun cacheGetBitmap(key: String): Bitmap? {
        if (!isDrawableInCache(key)) {
            return null
        }

        val fis: FileInputStream
        try {
            fis = FileInputStream(cacheGetFileName(key))
            val bitmap = BitmapFactory.decodeStream(fis)
            fis.close()
            return bitmap
        } catch (e: Exception) {
            log("Unable to get bitmap from cache $e")
        }

        return null
    }

    /**
     * create path for icons cache like this
     * {cacheDir}/icons/{icons_pack_package_name}_{key_hash}.png
     */
    private fun cacheGetFileName(key: String): File {
        return File("$iconsCacheDir${File.separator}$key.png")
    }

    /**
     * Clear cache
     */
    fun cacheClear() {
        val cacheDir = this.iconsCacheDir

        if (!cacheDir.isDirectory)
            return

        cacheDir.listFiles()?.forEach {
            if (!it.delete()) {
                log("Failed to delete file: " + it.absolutePath)
            }
        }
    }
}
