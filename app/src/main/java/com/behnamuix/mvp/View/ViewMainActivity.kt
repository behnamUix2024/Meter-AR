package com.behnamuix.mvp.View

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.Toast
import com.behnamuix.mvp.AndroidWrapper.DeviceInfo
import com.behnamuix.mvp.Remote.RetrofitService
import com.behnamuix.mvp.Remote.dataModel.DefaultModel
import com.behnamuix.mvp.Remote.ext.ErrorUtils
import com.behnamuix.mvp.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ViewMainActivity(ctx: Context) : FrameLayout(ctx) {
    val binding = ActivityMainBinding.inflate(
        LayoutInflater.from(ctx)
    )

    fun onclickHandler(androidId:String) {
        binding.btnSendEmail.setOnClickListener() {
            val email = binding.etEmail.text.toString()

            if (email.isEmpty()) {
                binding.etEmail.error = "ایمیل نمیتواند خالی باشد"
            } else {
                binding.etEmail.error = null
                sendCodeInEmail(email)
                binding.cardView2.visibility = INVISIBLE
                binding.cardView.visibility = INVISIBLE
                binding.tvEmail.text = "ارسال کد 6 رقمی به ایمیل:$email"

            }
        }
        binding.tvWrong.setOnClickListener() {
            binding.cardView2.visibility = VISIBLE
            binding.cardView.visibility = INVISIBLE
        }
        binding.btnVerifyCode.setOnClickListener() {
            val email = binding.etEmail.text.toString()
            val code = binding.etCode.text.toString()
            if (code.isEmpty()) {
                binding.etCode.error = "این فیلد نمیتواند خالی باشد"
            } else {
                binding.etCode.error = null

                verifyCode(email, code, androidId)
            }
        }

    }

    private fun verifyCode(email: String, code: String, androiId: String) {
        val service = RetrofitService.apiService
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val resp = service.verifyCode(androiId, code, email)
                if (resp.isSuccessful) {
                    launch(Dispatchers.Main) {
                        //context.startActivity(context,)
                    }
                } else {
                    launch(Dispatchers.Main) {
                        Toast.makeText(context, ErrorUtils.getError(resp), Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            } catch (e: Exception) {
                Log.i("Server Error", e.message.toString())

            }
        }

    }

    private fun sendCodeInEmail(email: String) {
        val service = RetrofitService.apiService
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val resp = service.sendCode(email)
                if (resp.isSuccessful) {
                    launch(Dispatchers.Main) {
                        val data = resp.body() as DefaultModel
                        Toast.makeText(context, data.message, Toast.LENGTH_SHORT).show()
                    }
                } else {
                    launch(Dispatchers.Main) {
                        Toast.makeText(context, ErrorUtils.getError(resp), Toast.LENGTH_SHORT)
                            .show()

                    }
                }
            } catch (e: Exception) {
                Log.i("Server Error", e.message.toString())
            }

        }

    }

}