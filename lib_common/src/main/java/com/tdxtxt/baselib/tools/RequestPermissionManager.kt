package com.tdxtxt.baselib.tools

import android.Manifest
import android.content.Context
import androidx.fragment.app.FragmentActivity
import com.permissionx.guolindev.PermissionX
import com.permissionx.guolindev.callback.ForwardToSettingsCallback
import com.permissionx.guolindev.callback.RequestCallback
import com.tdxtxt.baselib.callback.MenuCallBack
import com.tdxtxt.baselib.dialog.impl.CommDialog

/**
 * 功能描述:权限申请工具类
 * @author tangdexiang
 * @since 2020.5.27
 */
object RequestPermissionManager{
    fun directreQuestPermission(activity: FragmentActivity?, permissions: List<String>, isToSettings: Boolean, listener: (PermissionListener.() -> Unit)?){
        if(activity == null) return
        var callback: PermissionListener? = null
        if(listener != null){
            callback = object : PermissionListener() {}
            callback.listener()
        }
        PermissionX.init(activity)
                .permissions(permissions)
//                .onExplainRequestReason { scope, deniedList ->
//                    scope.showRequestReasonDialog(deniedList, "xxx", "明白", "取消")
//                }
            .onForwardToSettings(ForwardToSettingsCallback { scope, deniedList -> if(isToSettings) scope.showForwardToSettingsDialog(deniedList, "您需要去应用程序设置当中手动开启权限", "去设置", "取消") })
            .request(RequestCallback { allGranted, grantedList, deniedList ->
                if (allGranted) {//允许的权限 grantedList
                    callback?.onGranted?.invoke(grantedList)
                } else {//被拒绝的权限 deniedList
                    callback?.onDenied?.invoke(deniedList)
                }
            })
    }
    fun requestPermission(activity: FragmentActivity?, permissions: List<String>, requestReason: String, isToSettings: Boolean, listener: (PermissionListener.() -> Unit)?){
        if(activity == null) return
        if(activity.isDestroyed()) return
        if(activity.isFinishing()) return

        var callback: PermissionListener? = null
        if(listener != null){
            callback = object : PermissionListener() {}
            callback.listener()
        }
        var isGranted = true
        permissions.forEach {
            if(!PermissionX.isGranted(activity, it)){
                isGranted = false
                return@forEach
            }
        }
        if(isGranted){
            callback?.onGranted?.invoke(permissions)
        }else{
            CommDialog.showCommDialog(activity, requestReason, MenuCallBack("取消"){
                callback?.onDenied?.invoke(permissions)
            }, MenuCallBack("确认"){
                directreQuestPermission(activity, permissions, isToSettings, listener)
            })?.setCancelable(false)
        }
    }
    /**
     * 请求相机权限
     */
    fun requestCameraPermission(activity: FragmentActivity?, requestReason: String, listener: (PermissionListener.() -> Unit)?){
        requestPermission(activity, listOf(Manifest.permission.CAMERA), requestReason, true, listener)
    }

    /**
     * 请求存储权限
     */
    fun requestStoragePermission(activity: FragmentActivity?, requestReason: String, listener: (PermissionListener.() -> Unit)?){
        requestPermission(activity, listOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE), requestReason, true, listener)
    }

    /**
     * 请求定位权限
     */
    fun requestLocalPermission(activity: FragmentActivity?, requestReason: String, listener: (PermissionListener.() -> Unit)?){
        requestPermission(activity, listOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION), requestReason, true, listener)
    }

    /**
     * 请求读取联系人权限
     */
    fun requestContactsPermission(activity: FragmentActivity?, requestReason: String, listener: (PermissionListener.() -> Unit)?){
        requestPermission(activity, listOf(Manifest.permission.READ_CONTACTS), requestReason, true, listener)
    }

    /**
     * 权限是否打开
     */
    fun isGranted(context: Context?, permission: String): Boolean{
        if(context == null) return false
        return PermissionX.isGranted(context, permission)
    }
}

abstract class PermissionListener{
    var onGranted: ((grantedList: List<String>?) -> Unit)? = null
    var onDenied: ((deniedList: List<String>?) -> Unit)? = null

    fun grantedPermission(granted: (grantedList: List<String>?) -> Unit){
        onGranted = granted
    }
    fun deniedPermission(denied: (deniedList: List<String>?) -> Unit){
        onDenied = denied
    }
}

/**
1.危险权限，需要用户授权
group:android.permission-group.CONTACTS
    permission:android.permission.WRITE_CONTACTS
    permission:android.permission.GET_ACCOUNTS
    permission:android.permission.READ_CONTACTS

group:android.permission-group.PHONE
    permission:android.permission.READ_CALL_LOG
    permission:android.permission.READ_PHONE_STATE
    permission:android.permission.CALL_PHONE
    permission:android.permission.WRITE_CALL_LOG
    permission:android.permission.USE_SIP
    permission:android.permission.PROCESS_OUTGOING_CALLS
    permission:com.android.voicemail.permission.ADD_VOICEMAIL

group:android.permission-group.CALENDAR
    permission:android.permission.READ_CALENDAR
    permission:android.permission.WRITE_CALENDAR

group:android.permission-group.CAMERA
    permission:android.permission.CAMERA

group:android.permissiongroup.SENSORS
    permission:android.permission.BODY_SENSORS

group:android.permission-group.LOCATION
    permission:android.permission.ACCESS_FINE_LOCATION
    permission:android.permission.ACCESS_COARSE_LOCATION

group:android.permission-group.STORAGE
    permission:android.permission.READ_EXTERNAL_STORAGE
    permission:android.permission.WRITE_EXTERNAL_STORAGE

group:android.permission-group.MICROPHONE
    permission:android.permission.RECORD_AUDIO

group:android.permission-group.SMS
    permission:android.permission.READ_SMS
    permission:android.permission.RECEIVE_WAP_PUSH
    permission:android.permission.RECEIVE_MMS
    permission:android.permission.RECEIVE_SMS
    permission:android.permission.SEND_SMS
    permission:android.permission.READ_CELL_BROADCASTS


2.正常权限，不需要用户授权的
    ACCESS_LOCATION_EXTRA_COMMANDS
    ACCESS_NETWORK_STATE
    ACCESS_NOTIFICATION_POLICY
    ACCESS_WIFI_STATE
    BLUETOOTH
    BLUETOOTH_ADMIN
    BROADCAST_STICKY
    CHANGE_NETWORK_STATE
    CHANGE_WIFI_MULTICAST_STATE
    CHANGE_WIFI_STATE
    DISABLE_KEYGUARD
    EXPAND_STATUS_BAR
    GET_PACKAGE_SIZE
    INSTALL_SHORTCUT
    INTERNET
    KILL_BACKGROUND_PROCESSES
    MODIFY_AUDIO_SETTINGS
    NFC
    READ_SYNC_SETTINGS
    READ_SYNC_STATS
    RECEIVE_BOOT_COMPLETED
    REORDER_TASKS
    REQUEST_INSTALL_PACKAGES
    SET_ALARM
    SET_TIME_ZONE
    SET_WALLPAPER
    SET_WALLPAPER_HINTS
    TRANSMIT_IR
    UNINSTALL_SHORTCUT
    USE_FINGERPRINT
    VIBRATE
    WAKE_LOCK
    WRITE_SYNC_SETTINGS
**/