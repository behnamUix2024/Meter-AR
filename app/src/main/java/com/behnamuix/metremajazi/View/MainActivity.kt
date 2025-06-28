package com.behnamuix.metremajazi.View

import android.Manifest
import android.app.ActivityManager
import android.app.Dialog
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.View.OnLongClickListener
import android.view.Window
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.behnamuix.metremajazi.Ads.TapsellApi
import com.behnamuix.metremajazi.Contract.MainContract
import com.behnamuix.metremajazi.Model.MainModel
import com.behnamuix.metremajazi.Presenter.MainPresenter
import com.behnamuix.metremajazi.R
import com.google.ar.sceneform.ux.ArFragment
import kotlin.math.roundToInt


class MainActivity : AppCompatActivity(), MainContract.View {

    private var tutorialDialog: Dialog? = null

    private lateinit var presenter: MainPresenter
    private var arFragment: ArFragment? = null

    private lateinit var distanceTextView: TextView

    private val CAMERA_PERMISSION_CODE = 100

    private var isTutorialDialogShown = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        distanceTextView = findViewById(R.id.distanceText)

        val model = MainModel()
        presenter = MainPresenter(this, model, this)

        arFragment = supportFragmentManager.findFragmentById(R.id.arFragment) as? ArFragment
        arFragment?.let {
            presenter.setArFragment(it)
        }
    }

    private fun showTutorialDialog() {
        if (tutorialDialog == null && !isTutorialDialogShown) {
            tutorialDialog = Dialog(this).apply {
                requestWindowFeature(Window.FEATURE_NO_TITLE)
                setCancelable(false)
                setContentView(R.layout.toturial_dialog)

                val okButton = findViewById<Button>(R.id.btn_ok)
                okButton.setOnClickListener {
                    dismiss()
                    tutorialDialog = null
                    isTutorialDialogShown = true
                    Handler(Looper.getMainLooper()).postDelayed({
                        TapsellApi(this@MainActivity).TapsellConfig()
                    }, 100)
                }
                show()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (checkCameraPermission()) {
            if (checkARSupport()) {
                presenter.setupAR()
                Handler(Looper.getMainLooper()).postDelayed({
                    showTutorialDialog()
                }, 1000)
            }
        } else {
            //requestCameraPermission()
        }
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.onDestroy()

        tutorialDialog?.apply {
            if (isShowing) {
                dismiss()
            }
            tutorialDialog = null
        }
    }

    override fun showDistance(distance: Float) {
        runOnUiThread {
            try {
                val distanceMeters = (distance * 100).roundToInt() / 100f
                val distanceCm = (distance * 100).roundToInt()
                val distanceInch = (distance * 39.37f).roundToInt()

                distanceTextView.setOnLongClickListener(OnLongClickListener {
                    distanceTextView.text = "فاصله: ${distanceInch} اینچ"
                    false
                })

                distanceTextView.setOnClickListener {
                    distanceTextView.text = if (distanceMeters >= 1) {
                        "فاصله: ${distanceMeters} متر (${distanceCm} سانتی‌متر)"
                    } else {
                        "فاصله: ${distanceCm} سانتی‌متر"
                    }
                }

                distanceTextView.text = if (distanceMeters >= 1) {
                    "فاصله: ${distanceMeters} متر (${distanceCm} سانتی‌متر)"
                } else {
                    "فاصله: ${distanceCm} سانتی‌متر"
                }
            } catch (e: Exception) {
                Log.e("DistanceError", "خطا در نمایش فاصله", e)
                distanceTextView.text = "فاصله: --"
            }
        }
    }

    override fun showError(message: String) {
        runOnUiThread {
            try {
                Toast.makeText(this, message, Toast.LENGTH_LONG).show()
            } catch (e: Exception) {
                Log.e("ErrorDisplay", "خطا در نمایش پیام خطا", e)
            }
        }
    }

    private fun checkARSupport(): Boolean {
        val activityManager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val openGlVersion = activityManager.deviceConfigurationInfo.glEsVersion

        return if (openGlVersion.toDouble() < 3.0) {
            Toast.makeText(this, "دستگاه شما از AR پشتیبانی نمی‌کند.", Toast.LENGTH_LONG).show()
            tutorialDialog?.findViewById<TextView>(R.id.tv_support_not)?.visibility = View.VISIBLE
            false
        } else {
            runOnUiThread {
                tutorialDialog?.findViewById<TextView>(R.id.tv_support)?.visibility = View.VISIBLE
            }
            true
        }
    }

    private fun checkCameraPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestCameraPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.CAMERA),
            CAMERA_PERMISSION_CODE
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (checkARSupport()) {
                    presenter.setupAR()
                    Handler(Looper.getMainLooper()).postDelayed({
                        showTutorialDialog()
                    }, 300)
                }
            } else {
                Toast.makeText(this, "مجوز دوربین برای قابلیت AR ضروری است.", Toast.LENGTH_LONG)
                    .show()
                finish()
            }
        }
    }
}