package com.behnamuix.mvp.Presenter
//3
import com.behnamuix.mvp.Model.ModelMainActivity
import com.behnamuix.mvp.View.ViewMainActivity

class PresenterMainActivity(
    private val view: ViewMainActivity,
    private val model: ModelMainActivity

) {
    fun onCreatePresenter(){
        view.onClickHandler()
    }
}