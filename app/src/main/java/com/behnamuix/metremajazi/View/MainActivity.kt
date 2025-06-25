package com.behnamuix.metremajazi.View

import android.Manifest
import android.app.ActivityManager
import android.app.Dialog
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
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
import ir.tapsell.plus.AdRequestCallback
import ir.tapsell.plus.AdShowListener
import ir.tapsell.plus.TapsellPlus
import ir.tapsell.plus.TapsellPlusInitListener
import ir.tapsell.plus.model.AdNetworkError
import ir.tapsell.plus.model.AdNetworks
import ir.tapsell.plus.model.TapsellPlusAdModel
import ir.tapsell.plus.model.TapsellPlusErrorModel
import kotlin.math.roundToInt


class MainActivity : AppCompatActivity(), MainContract.View {

    var initResponseID=""

    // Presenter برنامه که منطق اصلی را مدیریت می‌کند.
    private lateinit var presenter: MainPresenter

    // ArFragment مسئول نمایش صحنه AR است. nullable است چون ممکن است هنوز مقداردهی نشده باشد.
    private var arFragment: ArFragment? = null
    private lateinit var dialog: Dialog

    // TextView برای نمایش فاصله محاسبه شده در UI.
    private lateinit var distanceTextView: TextView

    // ProgressBar برای نمایش وضعیت بارگذاری.
    private lateinit var progressBar: ProgressBar // متغیر ProgressBar اضافه شد.

    // کد درخواست مجوز دوربین (برای شناسایی در onRequestPermissionsResult).
    private val CAMERA_PERMISSION_CODE = 100

    // متد onCreate: زمانی که اکتیویتی برای اولین بار ایجاد می‌شود، فراخوانی می‌شود.
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // بارگذاری فایل layout XML مربوط به این اکتیویتی.
        setContentView(R.layout.activity_main)
        val instance:TapsellApi by lazy {
            TapsellApi(this)
        }
        //Tapsell config
        instance.TapsellConfig()

        showDialog()
        // متغیرهای UI را از XML پیدا و مقداردهی اولیه کن
        distanceTextView = findViewById(R.id.distanceText)

        // مقداردهی اولیه Presenter: این اکتیویتی (this) به عنوان View، Model و Context به Presenter داده می‌شود.
        val model = MainModel()
        presenter = MainPresenter(this, model, this)

        // پیدا کردن ArFragment: از FragmentManager برای یافتن ArFragment در layout استفاده می‌شود.
        arFragment = supportFragmentManager.findFragmentById(R.id.arFragment) as? ArFragment
        // اگر ArFragment پیدا شد (null نبود)، آن را به Presenter بده.
        arFragment?.let {
            presenter.setArFragment(it)
            // خطوط مربوط به مخفی کردن دستورالعمل‌های پیش‌فرض ArFragment به Presenter منتقل شده‌اند.
            // این کار از خطای NullPointerException در زمان راه‌اندازی جلوگیری می‌کند.
        }

        // تنظیم شنونده کلیک برای دکمه Reset (با فرض وجود در XML).
        // علامت ?. قبل از setOnClickListener به این معنی است که اگر resetButton null بود، این کد اجرا نشود.

    }







    private fun showDialog() {
        dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.toturial_dialog)
        val yesBtn = dialog.findViewById(R.id.btn_ok) as Button
        yesBtn.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    // متد onResume: زمانی که اکتیویتی به پیش‌زمینه بازمی‌گردد، فراخوانی می‌شود.
    override fun onResume() {
        super.onResume()
        // بررسی مجوز دوربین و پشتیبانی AR هنگام بازگشت برنامه به پیش‌زمینه.
        if (checkCameraPermission()) { // ابتدا مجوز دوربین را بررسی کن
            if (checkARSupport()) { // سپس پشتیبانی AR را بررسی کن
                presenter.setupAR() // Presenter را برای راه‌اندازی AR فراخوانی کن.
            }
        } else {
            // اگر مجوزها نبود، درخواست مجوز را بده.
            requestCameraPermission()
        }
        // ArFragment خودش ArSceneView را در متد onResume() خود resume می‌کند، نیازی به فراخوانی دستی نیست.
    }

    // متد onPause: زمانی که اکتیویتی به پس‌زمینه می‌رود، فراخوانی می‌شود.
    override fun onPause() {
        super.onPause()
        // ArFragment خودش ArSceneView را در متد onPause() خود pause می‌کند، نیازی به فراخوانی دستی نیست.
    }

    // متد onDestroy: زمانی که اکتیویتی از بین می‌رود، فراخوانی می‌شود.
    override fun onDestroy() {
        super.onDestroy()
        // فراخوانی onDestroy در Presenter برای پاکسازی منابع آن.
        presenter.onDestroy()
        // نکته مهم: فراخوانی دستی arFragment?.arSceneView?.destroy() حذف شده است.
        // ArFragment خودش مسئول مدیریت چرخه حیات ArSceneView خود است و فراخوانی دستی می‌تواند منجر به NullPointerException شود.
    }

    // --- پیاده‌سازی متدهای View Contract (این متدها توسط Presenter فراخوانی می‌شوند) ---

    // نمایش فاصله محاسبه شده در UI.
    override fun showDistance(distance: Float) {
        // اجرای کد در نخ اصلی UI.
        runOnUiThread {
            try {
                // تبدیل فاصله از متر به سانتی‌متر و نمایش در فرمت‌های مختلف.
                val distanceMeters = (distance * 100).roundToInt() / 100f
                val distanceCm = (distance * 100).roundToInt()
                val distanceInch = (distance * 39.37f).roundToInt()
                distanceTextView.setOnLongClickListener(OnLongClickListener {
                    distanceTextView.text = "فاصله: ${distanceInch} اینچ"
                    false
                })
                distanceTextView.setOnClickListener({
                    distanceTextView.text = if (distanceMeters >= 1) {
                        "فاصله: ${distanceMeters} متر (${distanceCm} سانتی‌متر)"
                    } else {
                        "فاصله: ${distanceCm} سانتی‌متر"
                    }
                })
                distanceTextView.text = if (distanceMeters >= 1) {
                    "فاصله: ${distanceMeters} متر (${distanceCm} سانتی‌متر)"
                } else {
                    "فاصله: ${distanceCm} سانتی‌متر"
                }
            } catch (e: Exception) {
                // ثبت خطا در Logcat و نمایش متن پیش‌فرض در صورت بروز مشکل در نمایش فاصله.
                Log.e("DistanceError", "خطا در نمایش فاصله", e)
                distanceTextView.text = "فاصله: --"
            }
        }
    }

    // نمایش پیام خطا به کاربر.
    override fun showError(message: String) {
        // اجرای کد در نخ اصلی UI.
        runOnUiThread {
            try {
                // ساخت و نمایش یک Toast message.
                val toast = Toast.makeText(this, message, Toast.LENGTH_LONG)
                toast.show()
            } catch (e: Exception) {
                // ثبت خطا در Logcat در صورت بروز مشکل در نمایش Toast.
                Log.e("ErrorDisplay", "خطا در نمایش پیام خطا", e)
            }
        }
    }


    // --- متدهای کمکی برای بررسی پشتیبانی AR و مجوزها ---

    // بررسی می‌کند که آیا دستگاه از ARCore پشتیبانی می‌کند (OpenGL ES 3.0 یا بالاتر).
    private fun checkARSupport(): Boolean {
        // دسترسی به اطلاعات پیکربندی دستگاه برای بررسی نسخه OpenGL ES.
        val openGlVersion = (getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager)
            .deviceConfigurationInfo
            .glEsVersion

        // اگر نسخه OpenGL کمتر از 3.0 باشد، AR پشتیبانی نمی‌شود.
        return if (openGlVersion.toDouble() < 3.0) {
            Toast.makeText(this, "دستگاه شما از AR پشتیبانی نمی‌کند.", Toast.LENGTH_LONG).show()
            dialog.findViewById<TextView>(R.id.tv_support_not).visibility = View.VISIBLE

            false
        } else {
            runOnUiThread {
                dialog.findViewById<TextView>(R.id.tv_support).visibility = View.VISIBLE

            }
            true
        }
    }

    // بررسی می‌کند که آیا مجوز دوربین به برنامه داده شده است یا خیر.
    private fun checkCameraPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }

    // درخواست مجوز دوربین از کاربر.
    private fun requestCameraPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.CAMERA),
            CAMERA_PERMISSION_CODE
        )
    }

    // دریافت نتیجه درخواست مجوزها از کاربر.
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        // اگر نتیجه مربوط به درخواست مجوز دوربین ما باشد.
        if (requestCode == CAMERA_PERMISSION_CODE) {
            // اگر مجوز داده شد.
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // اگر AR پشتیبانی می‌شود، راه‌اندازی Presenter را انجام بده.
                if (checkARSupport()) {
                    presenter.setupAR()
                }
            } else {
                // اگر مجوز رد شد، پیامی نمایش داده و برنامه را ببند.
                Toast.makeText(this, "مجوز دوربین برای قابلیت AR ضروری است.", Toast.LENGTH_LONG)
                    .show()
                finish() // بستن اکتیویتی.
            }
        }
    }
}
