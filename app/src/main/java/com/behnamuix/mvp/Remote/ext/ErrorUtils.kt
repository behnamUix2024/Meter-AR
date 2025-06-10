package com.behnamuix.mvp.Remote.ext

import com.behnamuix.mvp.Remote.dataModel.ErrorModel
import com.google.gson.Gson
import retrofit2.Response

object ErrorUtils {
    fun getError(res:Response<*>):String{
        var error:String?=null
        val errorBody=res.errorBody()
        if(errorBody!=null){
            error=Gson().fromJson(errorBody.string(),ErrorModel()::class.java).message
        }
        return error?:"ارتباط با سرور امکان پذیر نیست!"
    }
}