package cn.fuckmiui.xiaowine.utils

import android.annotation.SuppressLint
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import java.util.*


object Utils {
    const val TAG = "FuckMIUI"
    private val handler by lazy { Handler(Looper.getMainLooper()) }

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