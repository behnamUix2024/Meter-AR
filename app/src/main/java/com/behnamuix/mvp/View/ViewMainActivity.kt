package com.behnamuix.mvp.View
//2
import android.content.Context
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.Toast
import com.behnamuix.mvp.R
import com.behnamuix.mvp.Remote.RetrofitService
import com.behnamuix.mvp.Remote.dataModel.DefaultModel
import com.behnamuix.mvp.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ViewMainActivity(
    ctx: Context
) : FrameLayout(ctx) {
    val binding = ActivityMainBinding.inflate(LayoutInflater.from(ctx), this, false)
    fun onClickHandler() {
        binding.btnSendEmail.setOnClickListener() {
            val email = binding.etEmail.text.toString()
            if (email.isEmpty()) {
                binding.etEmail.setError(
                    "ایمیل وارد نشد!",
                    resources.getDrawable(R.drawable.baseline_warning_24)
                )
                return@setOnClickListener
            }
            sendCodeToEmail(email)
            binding.tvEmail.text = "$email ارسال کد به ایمیل: "
            binding.cardView2.visibility = INVISIBLE
            binding.cardView.visibility = VISIBLE

        }
        binding.tvWrong.setOnClickListener() {
            binding.cardView2.visibility = VISIBLE
            binding.cardView.visibility = INVISIBLE
        }
    }

    private fun sendCodeToEmail(email: String) {
        val service=RetrofitService.apiService
        CoroutineScope(Dispatchers.IO).launch{
            val resp=service.sendRequest(email)
            if(resp.isSuccessful){
                launch (Dispatchers.Main){
                    val data=resp.body() as DefaultModel
                    println(data)
                }
            }
        }


    }

}