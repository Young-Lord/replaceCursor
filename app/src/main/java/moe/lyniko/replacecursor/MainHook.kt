package moe.lyniko.replacecursor

import android.content.res.XResources
import android.graphics.drawable.Drawable
import de.robv.android.xposed.IXposedHookZygoteInit
import de.robv.android.xposed.IXposedHookZygoteInit.StartupParam
import de.robv.android.xposed.XSharedPreferences
import de.robv.android.xposed.XposedBridge
import moe.lyniko.replacecursor.utils.PreferenceUtils
import moe.lyniko.replacecursor.utils.ResourceHookEntry


class MainHook : IXposedHookZygoteInit {
    private var hooks: List<ResourceHookEntry>
    private var xsp: XSharedPreferences =
        XSharedPreferences(BuildConfig.APPLICATION_ID, PreferenceUtils.functionalConfigName)

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
                /*
                Change cursor hotspot, not working.
                val xml = """
                    <pointer-icon xmlns:android="http://schemas.android.com/apk/res/android"
                        android:bitmap="@android:drawable/${hook.resourceId}"
                        android:hotSpotX="0dp"
                        android:hotSpotY="0dp" />
                """.trimIndent()
                val drawableIcon = xmlStringToDrawable(xml)
                XResources.setSystemWideReplacement(
                    "android",
                    "drawable",
                    hook.resourceId+"_icon",
                    object : XResources.DrawableLoader() {
                        override fun newDrawable(xModuleResources: XResources, s: Int): Drawable {
                            XposedBridge.log("ReplaceCursor - drawableIcon for ${hook.resourceId}: $drawableIcon")
                            return drawableIcon
                        }
                    }
                )
                */
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

    /*
    private fun xmlStringToDrawable(yourString: String): Drawable{
        val parser = Xml.newPullParser()
        parser.setInput(StringReader(yourString))
        return Drawable.createFromXml(MyApplication.resourcesPublic, parser)
    }
    */

    init {
        xsp.makeWorldReadable()
        hooks = PreferenceUtils.getResourceHooksFrom(xsp)
    }
}
