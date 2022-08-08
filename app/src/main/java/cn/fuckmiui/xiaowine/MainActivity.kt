package cn.fuckmiui.xiaowine

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Menu
import android.view.View
import android.view.WindowInsetsController
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import cn.fuckmiui.xiaowine.databinding.ActivityMainBinding
import cn.fuckmiui.xiaowine.utils.Utils.TAG
import cn.fuckmiui.xiaowine.utils.Utils.isPresent
import cn.fuckmiui.xiaowine.utils.Utils.isSystemApplication
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.jaredrummler.ktsh.Shell
import java.text.SimpleDateFormat
import java.util.*
import kotlin.system.exitProcess


class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.setSystemBarsAppearance(WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS, WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS)
        } else {
            window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN) or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
        window.statusBarColor = Color.TRANSPARENT


        setSupportActionBar(binding.toolbar)

        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)

//        binding.fab.setOnClickListener { view ->
//            Snackbar.make(view, "MIUI很多功能都是通过覆盖层实现的\n可以通过屏蔽还原原生的东西", Snackbar.LENGTH_LONG).setAnchorView(R.id.fab).setAction("Action", null).show()
//        }
        Thread {
            if (!isSystemApplication(this)) {
                val result = Shell("sh").run("su")
                Log.i("FuckMIUI", result.output())
                if (!result.isSuccess) {
                    val dialog = MaterialAlertDialogBuilder(this).setTitle("权限不足").setMessage("无ROOT权限，无法使用本工具箱功能").setNegativeButton("退出") { _, _ -> exitProcess(0) }.setCancelable(false)
                    Handler(Looper.getMainLooper()).post { dialog.show() }
                }
            }


            if (!isPresent("android.provider.MiuiSettings")) {
                val dialog = MaterialAlertDialogBuilder(this).setTitle("本设备非MIUI").setMessage("本工具箱功能的所有功能都针对于MIUI，其他系统无法使用").setNegativeButton("退出") { _, _ -> exitProcess(0) }.setCancelable(false)
                Handler(Looper.getMainLooper()).post { dialog.show() }
            }


        }.start()
    }



    //
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        val buildTime = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(BuildConfig.BUILD_TIME.toLong())
        menu.add(buildTime)
        return true
    }


    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}