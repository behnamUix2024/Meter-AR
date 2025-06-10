package com.behnamuix.mvp.ui
//1
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.behnamuix.mvp.Model.ModelMainActvity
import com.behnamuix.mvp.Presenter.PresenterMainActvity
import com.behnamuix.mvp.View.ViewMainActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val view=ViewMainActivity(this)
        setContentView(view.binding.root)



        val presenter=PresenterMainActvity(view,ModelMainActvity(this))
        presenter.onCreatePresenter()
    }
}