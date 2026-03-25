// 文件：app/src/main/java/com/rosan/installer/hook/util/InxLockerConfig.kt
package com.rosan.installer.hook.util
import com.rosan.installer.hook.util.PrefsProvider
import android.content.Context
import com.highcapable.yukihookapi.hook.factory.prefs
import com.highcapable.yukihookapi.hook.log.YLog
import com.highcapable.yukihookapi.YukiHookAPI
import android.app.ActivityThread
import java.lang.reflect.Method

object PrefsSaver {
    private val currentApplicationMethod: Method by lazy {
        try {
            val activityThreadClass = Class.forName("android.app.ActivityThread")
            activityThreadClass.getMethod("currentApplication")
        } catch (e: Exception) {
            YLog.e("PrefsSaver", "加载失败$e")
            throw e
        }
    }
    
    private fun getAppContext(): Context {
        return currentApplicationMethod.invoke(null) as Context
    }
    fun putBoolean(key: String, value: Boolean) {
        try {
            val context: Context = getAppContext()
            context.prefs(PrefsProvider.PREFS_FILE_NAME).edit {
                putBoolean(key, value)
            }
        } catch(_: Exception) {}
    }

}