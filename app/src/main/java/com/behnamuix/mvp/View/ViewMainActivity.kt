package com.behnamuix.mvp.View

import android.content.Context
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.Toast
import com.behnamuix.mvp.databinding.ActivityMainBinding

class ViewMainActivity(
    ctx:Context
) :FrameLayout(ctx){
    val binding=ActivityMainBinding.inflate(LayoutInflater.from(ctx),this,true)
    fun onClickHandler(){
        binding.btnSendEmail.setOnClickListener(){
            if(binding.etEmail.text.toString().isEmpty()){
                Toast.makeText(context,"ایمیل وارد نشده!",Toast.LENGTH_SHORT).show()
                return@setOnClickListener

            }
        }

    }

}