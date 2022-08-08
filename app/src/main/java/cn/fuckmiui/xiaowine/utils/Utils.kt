package cn.fuckmiui.xiaowine.utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import cn.fuckmiui.xiaowine.BuildConfig
import java.util.*


object Utils {
    const val TAG = "FuckMIUI"
    private val handler by lazy { Handler(Looper.getMainLooper()) }

    fun bash(context: Context): String = if (isSystemApplication(context)) "sh" else "su"
    fun isSystemApplication(context: Context): Boolean {
        val mPackageManager: PackageManager = context.packageManager
        try {
            val packageInfo = mPackageManager.getPackageInfo(BuildConfig.APPLICATION_ID, PackageManager.GET_CONFIGURATIONS)
            if (packageInfo.applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM > 0) {
                return true
            }
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        return false
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    fun showToastOnLooper(context: Context, message: String) {
        try {

            handler.post { //                XToast.makeToast(context, message, toastIcon =context.resources.getDrawable(R.mipmap.ic_launcher_round)).show()
//                XToast.makeText(context, message, toastIcon = context.resources.getDrawable(R.mipmap.ic_launcher, null)).show()
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            }
        } catch (e: RuntimeException) {
            e.printStackTrace()
        }
    }

    fun isPresent(name: String): Boolean {
        return try {
            Objects.requireNonNull(Thread.currentThread().contextClassLoader).loadClass(name)
            true
        } catch (e: ClassNotFoundException) {
            false
        }
    }
}