package com.behnamuix.mvp.Remote.apiRepository
//2
import com.behnamuix.mvp.Remote.dataModel.DefaultModel
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface LoginApiService {

    @FormUrlEncoded
    @POST("email/login")
    suspend fun sendCodeInEmail(
        @Field("email") email:String
    ):Response<DefaultModel>
}