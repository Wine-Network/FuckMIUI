package cn.fuckmiui.xiaowine

import android.content.ComponentName
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import cn.fuckmiui.xiaowine.databinding.FragmentFirstBinding
import cn.fuckmiui.xiaowine.utils.Utils
import cn.fuckmiui.xiaowine.utils.Utils.TAG
import com.google.android.material.materialswitch.MaterialSwitch
import com.jaredrummler.ktsh.Shell


class FirstFragment : Fragment(), View.OnClickListener {


    private val serviceMap = HashMap<MaterialSwitch, String>()
    private val acMap = HashMap<TextView, String>()
    private var switchViewList = arrayListOf<MaterialSwitch>()
    private var textViewList = arrayListOf<TextView>()

    private val binding get() = _binding!!
    private var _binding: FragmentFirstBinding? = null
    private lateinit var fragmentActivity: FragmentActivity

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fragmentActivity = requireActivity()

        serviceMap[binding.screenshot] = "com.miui.screenshot/com.miui.screenshot.TakeScreenshotService"
        serviceMap[binding.shutdown] = "miui.systemui.plugin/miui.systemui.globalactions.GlobalActionsPlugin"
        serviceMap[binding.control] = "miui.systemui.plugin/miui.systemui.controlcenter.MiuiControlCenter"
        serviceMap[binding.volume] = "miui.systemui.plugin/miui.systemui.volume.VolumeDialogPlugin"
        serviceMap[binding.player] = "miui.systemui.plugin/miui.systemui.miplay.MiPlayPluginImpl"
        serviceMap[binding.gesture] = "com.miui.home/com.miui.home.recents.TouchInteractionService"
        serviceMap.forEach { (key, _) ->
            key.setOnClickListener(this)
            switchViewList.add(key)
        }


        acMap[binding.battery] = "com.miui.powerkeeper/.ui.powertools.module.batterylife.BatteryStatusActivity"
        acMap[binding.test] = "com.miui.powerkeeper/com.miui.powerkeeper.ui.powertools.PowerToolsActivity"
        acMap[binding.temperature] = "com.miui.powerkeeper/com.miui.powerkeeper.ui.powertools.module.thermal.ThermalDetailActivity"
        acMap[binding.dark] = "com.android.settings/com.android.settings.Settings\$ReduceBrightColorsSettingsActivity"
        acMap[binding.dialogue] = "com.android.settings/com.android.settings.Settings\$ConversationListSettingsActivity"
        acMap[binding.running] = "com.android.settings/com.android.settings.RunningServices"
        acMap[binding.notice] = "com.android.settings/com.android.settings.Settings\$NotificationAssistantSettingsActivity"
        acMap[binding.memory] = "com.android.settings/com.android.settings.Settings\$MemorySettingsActivity"
        acMap.forEach { (key, _) ->
            key.setOnClickListener(this)
            textViewList.add(key)
        }

        binding.uninstall.setOnClickListener {
            val sh = Shell("su")
            val result = sh.run("pm ubinstall com.xiaomi.vipaccount")
            Log.i(TAG, result.output())
            if (!result.isSuccess) {
                Utils.showToastOnLooper(fragmentActivity, "您的系统不支持")
            }
        }
        init()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onClick(v: View?) {
        if (v == null) return
        if (textViewList.contains(v)) {
            openAc(acMap[v]!!)
        }
        if (switchViewList.contains(v)) {
            handler(serviceMap[v]!!, v)
        }
    }

    private fun openAc(cls: String) {
        val sh = Shell("su")
        val result = sh.run("am start 'intent:#Intent;launchFlags=0x80000;component=$cls;end'")
        Log.i(TAG, result.output())
        if (!result.isSuccess) {
            Utils.showToastOnLooper(fragmentActivity, "您的系统不支持")
        }
    }

    private fun handler(service: String, v: View) {
        val view = v as MaterialSwitch
        if (view.isChecked) {
            disable(service, v)
        } else {
            enable(service, v)
        }
    }

    private fun disable(service: String, v: View) {
        Thread {
            val sh = Shell("su")
            val result = sh.run("pm disable $service ")
            Log.i(TAG, result.output())
            if (result.isSuccess) {
                Handler(Looper.getMainLooper()).post { (v as MaterialSwitch).isChecked = true }
            } else {
                Handler(Looper.getMainLooper()).post { (v as MaterialSwitch).isChecked = false }
                Utils.showToastOnLooper(fragmentActivity, "您的系统不支持")
            }
        }.start()
    }

    private fun enable(service: String, v: View) {
        Thread {
            val sh = Shell("su")
            val result = sh.run("pm enable $service ")
            Log.i(TAG, result.output())
            if (result.isSuccess) {
                Handler(Looper.getMainLooper()).post {
                    (v as MaterialSwitch).isChecked = false
                }
            } else {
                Handler(Looper.getMainLooper()).post { (v as MaterialSwitch).isChecked = true }
                Utils.showToastOnLooper(fragmentActivity, "您的系统不支持")
            }
        }.start()
    }

    private fun init() {
//        val switchList = arrayListOf(binding.screenshot, binding.shutdown, binding.volume, binding.player)
        for (i in 0 until switchViewList.size) {
//            Log.i("aa", switchList[i].id.toString())
//            Log.i("aa", serviceMap[switchList[i].id.toString()]!!.split("/")[0])
            switchViewList[i].isChecked = !isEnabled(ComponentName(serviceMap[switchViewList[i]]!!.split("/")[0], serviceMap[switchViewList[i]]!!.split("/")[1]))
        }
    }

    private fun isEnabled(componentName: ComponentName): Boolean {
        return try {
            val state: Int = fragmentActivity.packageManager.getComponentEnabledSetting(componentName)
            state == PackageManager.COMPONENT_ENABLED_STATE_ENABLED || state == PackageManager.COMPONENT_ENABLED_STATE_DEFAULT
        } catch (e: Exception) {
            false
        }
    }

}