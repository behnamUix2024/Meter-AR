package com.behnamuix.mvp.Presenter

import com.behnamuix.mvp.Model.ModelMainActvity
import com.behnamuix.mvp.View.ViewMainActivity

class PresenterMainActvity(
    private val view:ViewMainActivity,
    private val model:ModelMainActvity
) {
    fun onCreatePresenter(){
        view.onclickHandler(model.getId())

    }
}