package com.behnamuix.mvp.ui
//1
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.behnamuix.mvp.Model.ModelMainActivity
import com.behnamuix.mvp.Presenter.PresenterMainActivity
import com.behnamuix.mvp.R
import com.behnamuix.mvp.View.ViewMainActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val view=ViewMainActivity(this)
        setContentView(view.binding.root)
        val presenter=PresenterMainActivity(view, ModelMainActivity())
        presenter.onCreatePresenter()
    }
}