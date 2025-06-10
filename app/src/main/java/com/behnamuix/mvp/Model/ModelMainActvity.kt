package com.behnamuix.mvp.Model

import android.content.Context
import com.behnamuix.mvp.AndroidWrapper.DeviceInfo

class ModelMainActvity(private val context: Context) {
    fun getId()=DeviceInfo.getAndroidID(context)
}