package com.behnamuix.mvp.AndroidWrapper

import android.annotation.SuppressLint
import android.content.Context
import android.provider.Settings

object DeviceInfo {
    var androidId: String? = null
    fun getAndroidID(ctx: Context): String {
        if(androidId==null){
            androidId=Settings.Secure.getString(
                ctx.contentResolver,
                Settings.Secure.ANDROID_ID
            )
        }
        return androidId?:""

    }
}