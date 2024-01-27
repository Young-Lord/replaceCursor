package moe.lyniko.replacecursor

import android.annotation.SuppressLint
import android.app.AndroidAppHelper
import android.content.ContentResolver
import android.content.ContentUris
import android.content.ContentValues
import android.content.res.XResources
import android.graphics.drawable.Drawable
import android.net.Uri
import android.util.Log
import de.robv.android.xposed.IXposedHookZygoteInit
import de.robv.android.xposed.IXposedHookZygoteInit.StartupParam
import de.robv.android.xposed.SELinuxHelper
import de.robv.android.xposed.XSharedPreferences
import de.robv.android.xposed.XposedBridge
import moe.lyniko.replacecursor.utils.PreferenceUtils
import moe.lyniko.replacecursor.utils.ResourceHookEntry
import java.io.File


class MainHook : IXposedHookZygoteInit {
    private var hooks: List<ResourceHookEntry>
    private var xsp: XSharedPreferences
    @SuppressLint("SdCardPath")
    private fun getFilePath(filename: String): String {
        // sadly i don't know how to get context, so i have to use this
        return "/data/data/${BuildConfig.APPLICATION_ID}/files/$filename"
    }

    override fun initZygote(param: StartupParam) {
        hooks.forEach { hook ->
            if(!hook.enabled) return@forEach
            try {
                XposedBridge.log("ReplaceCursor - Setting ${hook.imageFile} for ${hook.resourceId}")
                val imageBinary = PreferenceUtils.getImageBinaryFrom(xsp, hook.imageFile)
                val drawable = Drawable.createFromStream(imageBinary.inputStream(), null)!!
                XResources.setSystemWideReplacement(
                    "android",
                    "drawable",
                    hook.resourceId,
                    // get drawable from filePath
                    object : XResources.DrawableLoader() {
                        override fun newDrawable(xModuleResources: XResources, s: Int): Drawable {
                            /*val fileNameLastPart = File(hook.imageFile).name
                            val resolver: ContentResolver =
                                AndroidAppHelper.currentApplication().contentResolver
                            val uri = Uri.parse("content://moe.lyniko.replacecursor.ImageProvider/${fileNameLastPart}")
                            val cursor = resolver.query(uri, null, null, null, null)!!
                            cursor.moveToFirst()
                            val imageBinary = cursor.getBlob(0)
                            cursor.close()
                            val drawable = Drawable.createFromStream(imageBinary.inputStream(), null)*/
                            // XposedBridge.log("ReplaceCursor - drawable: $drawable")
                            return drawable
                        }
                    }
                )
            }
            catch (e: Exception) {
                e.printStackTrace()
            }
        }
        /* Static Way:
        val MODULE_PATH = param.modulePath;
        val modRes = XModuleResources.createInstance(MODULE_PATH, null)

        XResources.setSystemWideReplacement("android", "drawable", "pointer_arrow",
            modRes.fwd(R.drawable.arrow)
        )
         */
    }

    init {
        xsp =
            XSharedPreferences(BuildConfig.APPLICATION_ID, PreferenceUtils.functionalConfigName)
        xsp.makeWorldReadable()
        hooks = PreferenceUtils.getResourceHooksFrom(xsp)
    }
}
