package com.behnamuix.metremajazi.View // تعریف پکیج (بسته) که این فایل در آن قرار دارد.

import android.Manifest // ایمپورت کلاس Manifest برای دسترسی به مجوزهای اندروید.
import android.app.ActivityManager // ایمپورت کلاس ActivityManager برای دسترسی به اطلاعات سیستم (مثل OpenGL).
import android.app.Dialog // ایمپورت کلاس Dialog برای ایجاد و نمایش دیالوگ‌ها (پنجره‌های پاپ‌آپ).
import android.content.Context // ایمپورت کلاس Context که اطلاعات مربوط به محیط برنامه را فراهم می‌کند.
import android.content.pm.PackageManager // ایمپورت کلاس PackageManager برای بررسی وضعیت مجوزها.
import android.os.Bundle // ایمپورت کلاس Bundle برای ذخیره و بازیابی وضعیت اکتیویتی.
import android.os.Handler // ایمپورت کلاس Handler برای زمان‌بندی اجرای کد (مثل تاخیر).
import android.os.Looper // ایمپورت کلاس Looper برای کار با پیام‌های رابط کاربری (UI thread).
import android.util.Log // ایمپورت کلاس Log برای ثبت پیام‌ها در Logcat (برای دیباگ).
import android.view.View // ایمپورت کلاس View برای کار با عناصر UI.
import android.view.View.OnLongClickListener // ایمپورت اینترفیس OnLongClickListener برای تشخیص کلیک طولانی.
import android.view.Window // ایمپورت کلاس Window برای کنترل ویژگی‌های پنجره (مثل عدم نمایش عنوان).
import android.widget.Button // ایمپورت کلاس Button برای کار با دکمه‌ها.
import android.widget.ProgressBar // ایمپورت کلاس ProgressBar برای نمایش نوار پیشرفت.
import android.widget.TextView // ایمپورت کلاس TextView برای نمایش متن.
import android.widget.Toast // ایمپورت کلاس Toast برای نمایش پیام‌های کوتاه.
import androidx.appcompat.app.AppCompatActivity // ایمپورت کلاس پایه AppCompatActivity برای سازگاری با نسخه‌های قدیمی‌تر اندروید.
import androidx.core.app.ActivityCompat // ایمپورت کلاس ActivityCompat برای درخواست مجوزها.
import androidx.core.content.ContextCompat // ایمپورت کلاس ContextCompat برای بررسی مجوزها.
import com.behnamuix.metremajazi.Ads.TapsellApi // ایمپورت کلاس TapsellApi از پکیج Ads برای مدیریت تبلیغات تپسّل.
import com.behnamuix.metremajazi.Contract.MainContract // ایمپورت اینترفیس MainContract از پکیج Contract (برای معماری MVP).
import com.behnamuix.metremajazi.Model.MainModel // ایمپورت کلاس MainModel از پکیج Model (برای معماری MVP).
import com.behnamuix.metremajazi.Presenter.MainPresenter // ایمپورت کلاس MainPresenter از پکیج Presenter (برای معماری MVP).
import com.behnamuix.metremajazi.R // ایمپورت کلاس R که دسترسی به منابع برنامه (مثل layout ها و ID ها) را فراهم می‌کند.
import com.google.ar.sceneform.ux.ArFragment // ایمپورت کلاس ArFragment برای نمایش صحنه AR و مدیریت تعاملات کاربر.
import kotlin.math.roundToInt // ایمپورت تابع roundToInt برای گرد کردن اعداد به نزدیک‌ترین عدد صحیح.


class MainActivity : AppCompatActivity(), MainContract.View { // تعریف کلاس MainActivity که از AppCompatActivity ارث می‌برد و اینترفیس MainContract.View را پیاده‌سازی می‌کند.

    private var tutorialDialog: Dialog? = null // تعریف یک متغیر از نوع Dialog برای نمایش دیالوگ آموزشی، این متغیر Nullable است (می‌تواند null باشد).

    private lateinit var presenter: MainPresenter // تعریف یک متغیر از نوع MainPresenter، با lateinit مشخص می‌شود که قبل از استفاده مقداردهی اولیه خواهد شد.
    private var arFragment: ArFragment? = null // تعریف یک متغیر از نوع ArFragment، این متغیر Nullable است.

    private lateinit var distanceTextView: TextView // تعریف یک متغیر از نوع TextView برای نمایش متن فاصله، با lateinit مشخص می‌شود.
    // private lateinit var progressBar: ProgressBar // این خط کامنت شده است. متغیر ProgressBar تعریف شده ولی در کدهای شما استفاده‌ای از آن دیده نمی‌شود.

    private val CAMERA_PERMISSION_CODE = 100 // تعریف یک ثابت برای کد درخواست مجوز دوربین.

    // یک متغیر برای بررسی اینکه آیا دیالوگ آموزشی قبلاً نمایش داده شده است یا خیر
    private var isTutorialDialogShown = false // متغیری از نوع Boolean برای ردیابی وضعیت نمایش دیالوگ آموزشی (true اگر نمایش داده شده باشد، false در غیر این صورت).

    override fun onCreate(savedInstanceState: Bundle?) { // متد onCreate: زمانی که اکتیویتی برای اولین بار ایجاد می‌شود، فراخوانی می‌شود.
        super.onCreate(savedInstanceState) // فراخوانی متد onCreate از کلاس والد.
        setContentView(R.layout.activity_main) // بارگذاری فایل layout XML مربوط به این اکتیویتی (activity_main.xml).

        // متغیرهای UI را از XML پیدا و مقداردهی اولیه کن
        distanceTextView = findViewById(R.id.distanceText) // پیدا کردن TextView با ID 'distanceText' و انتساب آن به distanceTextView.

        // مقداردهی اولیه Presenter: این اکتیویتی (this) به عنوان View، Model و Context به Presenter داده می‌شود.
        val model = MainModel() // ایجاد یک نمونه از MainModel.
        presenter = MainPresenter(this, model, this) // ایجاد یک نمونه از MainPresenter و ارسال this (MainActivity به عنوان View و Context) و model به آن.

        // پیدا کردن ArFragment: از FragmentManager برای یافتن ArFragment در layout استفاده می‌شود.
        arFragment = supportFragmentManager.findFragmentById(R.id.arFragment) as? ArFragment // پیدا کردن Fragment با ID 'arFragment' و تبدیل آن به ArFragment.
        // اگر ArFragment پیدا شد (null نبود)، آن را به Presenter بده.
        arFragment?.let { // اگر arFragment null نباشد (از safe call operator ?. استفاده شده)، کد داخل بلاک اجرا می‌شود.
            presenter.setArFragment(it) // تنظیم ArFragment در Presenter.
        }
    }

    /**
     * دیالوگ آموزشی را به کاربر نمایش می‌دهد.
     * اطمینان حاصل می‌کند که دیالوگ فقط در صورتی نمایش داده شود که قبلاً دیده نشده باشد.
     */
    private fun showTutorialDialog() { // تعریف متد خصوصی showTutorialDialog برای نمایش دیالوگ آموزشی.
        // اطمینان حاصل می‌کنیم که دیالوگ قبلاً نمایش داده نشده باشد.
        // این شرط از نمایش مکرر دیالوگ در onResume جلوگیری می‌کند.
        if (tutorialDialog == null && !isTutorialDialogShown) { // بررسی می‌کند که tutorialDialog هنوز ساخته نشده باشد و قبلاً هم نمایش داده نشده باشد.
            tutorialDialog = Dialog(this).apply { // ایجاد یک نمونه جدید از Dialog و مقداردهی اولیه tutorialDialog.
                requestWindowFeature(Window.FEATURE_NO_TITLE) // درخواست عدم نمایش عنوان پیش‌فرض پنجره.
                setCancelable(false) // تنظیم دیالوگ به گونه‌ای که با لمس بیرون یا دکمه برگشت بسته نشود.
                setContentView(R.layout.toturial_dialog) // تنظیم طرح‌بندی (layout) دیالوگ از فایل XML 'toturial_dialog'.

                val okButton = findViewById<Button>(R.id.btn_ok) // پیدا کردن دکمه "ok" از طرح‌بندی دیالوگ.
                okButton.setOnClickListener { // تنظیم شنونده کلیک برای دکمه "ok".
                    dismiss() // بستن دیالوگ.
                    tutorialDialog = null // مهم: ارجاع به دیالوگ را null می‌کنیم تا حافظه آزاد شود و از نشت پنجره جلوگیری شود.
                    isTutorialDialogShown = true // تنظیم این متغیر به true نشان می‌دهد که دیالوگ آموزشی نمایش داده شده است.
                    // Tapsell ads را بعد از بسته شدن دیالوگ با تاخیر راه‌اندازی می‌کنیم.
                    Handler(Looper.getMainLooper()).postDelayed({ // استفاده از Handler برای اجرای کد با تاخیر در UI thread.
                        TapsellApi(this@MainActivity).TapsellConfig() // راه‌اندازی Tapsell پس از تاخیر.
                    }, 800) // تاخیر 1000 میلی‌ثانیه (1 ثانیه).
                }
                show() // نمایش دیالوگ.
            }
        }
    }

    override fun onResume() { // متد onResume: زمانی که اکتیویتی به پیش‌زمینه بازمی‌گردد یا برای اولین بار نمایش داده می‌شود، فراخوانی می‌شود.
        super.onResume() // فراخوانی متد onResume از کلاس والد.
        // منطق بررسی مجوز و راه‌اندازی AR را ابتدا قرار می‌دهیم.
        if (checkCameraPermission()) { // اگر مجوز دوربین داده شده باشد.
            if (checkARSupport()) { // و اگر دستگاه از AR پشتیبانی کند.
                presenter.setupAR() // Presenter را برای راه‌اندازی AR فراخوانی کن.
                // **مهم:** دیالوگ آموزشی را با یک تاخیر کوچک نمایش می‌دهیم.
                // این تاخیر به سیستم اجازه می‌دهد تا دیالوگ‌های سیستمی (مانند درخواست مجوز) را به طور کامل از بین ببرد.
                Handler(Looper.getMainLooper()).postDelayed({ // استفاده از Handler برای اجرای کد با تاخیر.
                    showTutorialDialog() // نمایش دیالوگ آموزشی پس از تاخیر.
                }, 1000) // تاخیر 1000 میلی‌ثانیه (1 ثانیه).
            }
        } else {
            requestCameraPermission() // اگر مجوز دوربین داده نشده باشد، آن را درخواست کن.
            // این خط (requestCameraPermission) که قبلاً کامنت شده بود را از حالت کامنت خارج کردم.
            // اگر شما آن را به صورت کامنت نگه دارید، برنامه هرگز مجوز را درخواست نمی‌کند و وارد بخش AR نمی‌شود.
        }
    }

    override fun onPause() { // متد onPause: زمانی که اکتیویتی به پس‌زمینه می‌رود یا توسط اکتیویتی دیگری پوشانده می‌شود، فراخوانی می‌شود.
        super.onPause() // فراخوانی متد onPause از کلاس والد.
    }

    override fun onDestroy() { // متد onDestroy: زمانی که اکتیویتی به طور کامل از بین می‌رود، فراخوانی می‌شود.
        super.onDestroy() // فراخوانی متد onDestroy از کلاس والد.
        presenter.onDestroy() // فراخوانی onDestroy در Presenter برای پاکسازی منابع آن.

        // --- اصلاح اساسی برای ارور WindowLeaked ---
        // اگر دیالوگ آموزشی (tutorialDialog) null نیست و در حال حاضر نمایش داده شده است،
        // آن را ببند (dismiss) و سپس ارجاعش را null کن تا از نشت حافظه جلوگیری شود.
        tutorialDialog?.apply { // اگر tutorialDialog null نباشد، کد داخل بلاک اجرا می‌شود.
            if (isShowing) { // بررسی می‌کند که آیا دیالوگ در حال نمایش است یا خیر.
                dismiss() // دیالوگ را می‌بندد.
            }
            tutorialDialog = null // ارجاع به دیالوگ را آزاد می‌کنیم تا از نشت حافظه (WindowLeaked) جلوگیری شود.
        }
        // --- پایان اصلاح ---
    }

    // --- پیاده‌سازی متدهای View Contract (این متدها توسط Presenter فراخوانی می‌شوند) ---

    // نمایش فاصله محاسبه شده در UI.
    override fun showDistance(distance: Float) { // متد showDistance برای نمایش فاصله.
        runOnUiThread { // اطمینان حاصل می‌کند که کد در نخ اصلی UI اجرا می‌شود.
            try {
                // تبدیل فاصله از متر به سانتی‌متر و نمایش در فرمت‌های مختلف.
                val distanceMeters = (distance * 100).roundToInt() / 100f // محاسبه فاصله بر حسب متر.
                val distanceCm = (distance * 100).roundToInt() // محاسبه فاصله بر حسب سانتی‌متر.
                val distanceInch = (distance * 39.37f).roundToInt() // محاسبه فاصله بر حسب اینچ.

                distanceTextView.setOnLongClickListener(OnLongClickListener { // تنظیم شنونده کلیک طولانی برای distanceTextView.
                    distanceTextView.text = "فاصله: ${distanceInch} اینچ" // نمایش فاصله بر حسب اینچ.
                    false // نشان می‌دهد که رویداد مصرف نشده است (می‌تواند رویدادهای دیگر را فعال کند).
                })

                distanceTextView.setOnClickListener { // تنظیم شنونده کلیک برای distanceTextView.
                    distanceTextView.text = if (distanceMeters >= 1) { // اگر فاصله بیشتر یا مساوی 1 متر باشد.
                        "فاصله: ${distanceMeters} متر (${distanceCm} سانتی‌متر)" // نمایش بر حسب متر و سانتی‌متر.
                    } else {
                        "فاصله: ${distanceCm} سانتی‌متر" // در غیر این صورت، فقط بر حسب سانتی‌متر.
                    }
                }

                distanceTextView.text = if (distanceMeters >= 1) { // نمایش پیش‌فرض فاصله.
                    "فاصله: ${distanceMeters} متر (${distanceCm} سانتی‌متر)" // نمایش بر حسب متر و سانتی‌متر.
                } else {
                    "فاصله: ${distanceCm} سانتی‌متر" // در غیر این صورت، فقط بر حسب سانتی‌متر.
                }
            } catch (e: Exception) { // اگر خطایی در طول محاسبه یا نمایش فاصله رخ داد.
                Log.e("DistanceError", "خطا در نمایش فاصله", e) // ثبت خطا در Logcat.
                distanceTextView.text = "فاصله: --" // نمایش متن پیش‌فرض.
            }
        }
    }

    // نمایش پیام خطا به کاربر.
    override fun showError(message: String) { // متد showError برای نمایش پیام خطا.
        runOnUiThread { // اطمینان حاصل می‌کند که کد در نخ اصلی UI اجرا می‌شود.
            try {
                Toast.makeText(this, message, Toast.LENGTH_LONG).show() // ساخت و نمایش یک Toast message.
            } catch (e: Exception) { // اگر خطایی در نمایش Toast رخ داد.
                Log.e("ErrorDisplay", "خطا در نمایش پیام خطا", e) // ثبت خطا در Logcat.
            }
        }
    }


    // --- متدهای کمکی برای بررسی پشتیبانی AR و مجوزها ---

    // بررسی می‌کند که آیا دستگاه از ARCore پشتیبانی می‌کند (OpenGL ES 3.0 یا بالاتر).
    private fun checkARSupport(): Boolean { // متد checkARSupport برای بررسی پشتیبانی AR.
        // دسترسی به اطلاعات پیکربندی دستگاه برای بررسی نسخه OpenGL ES.
        val activityManager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager // دریافت سرویس ActivityManager.
        val openGlVersion = activityManager.deviceConfigurationInfo.glEsVersion // دریافت نسخه OpenGL ES.

        return if (openGlVersion.toDouble() < 3.0) { // اگر نسخه OpenGL کمتر از 3.0 باشد.
            Toast.makeText(this, "دستگاه شما از AR پشتیبانی نمی‌کند.", Toast.LENGTH_LONG).show() // نمایش پیام خطا.
            // به صورت ایمن ویوهای داخل دیالوگ را با استفاده از tutorialDialog به‌روزرسانی می‌کنیم.
            tutorialDialog?.findViewById<TextView>(R.id.tv_support_not)?.visibility = View.VISIBLE // اگر دیالوگ موجود است، متن "پشتیبانی نمی‌شود" را قابل مشاهده کن.
            false // دستگاه از AR پشتیبانی نمی‌کند.
        } else {
            runOnUiThread { // اطمینان حاصل می‌کند که کد در نخ اصلی UI اجرا می‌شود.
                // به صورت ایمن ویوهای داخل دیالوگ را با استفاده از tutorialDialog به‌روزرسانی می‌کنیم.
                tutorialDialog?.findViewById<TextView>(R.id.tv_support)?.visibility = View.VISIBLE // اگر دیالوگ موجود است، متن "پشتیبانی می‌شود" را قابل مشاهده کن.
            }
            true // دستگاه از AR پشتیبانی می‌کند.
        }
    }

    // بررسی می‌کند که آیا مجوز دوربین به برنامه داده شده است یا خیر.
    private fun checkCameraPermission(): Boolean { // متد checkCameraPermission برای بررسی وضعیت مجوز دوربین.
        return ContextCompat.checkSelfPermission( // بررسی می‌کند که آیا مجوز CAMERA اعطا شده است یا خیر.
            this,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED // اگر مجوز داده شده باشد، true برمی‌گرداند.
    }

    // درخواست مجوز دوربین از کاربر.
    private fun requestCameraPermission() { // متد requestCameraPermission برای درخواست مجوز دوربین.
        ActivityCompat.requestPermissions( // درخواست مجوزها از کاربر.
            this,
            arrayOf(Manifest.permission.CAMERA), // آرایه‌ای از مجوزهای مورد نیاز (فقط مجوز دوربین).
            CAMERA_PERMISSION_CODE // کد درخواستی برای شناسایی پاسخ در onRequestPermissionsResult.
        )
    }

    // دریافت نتیجه درخواست مجوزها از کاربر.
    override fun onRequestPermissionsResult( // متد onRequestPermissionsResult برای دریافت نتیجه درخواست مجوزها.
        requestCode: Int, // کد درخواستی که شما ارسال کرده‌اید (CAMERA_PERMISSION_CODE).
        permissions: Array<out String>, // آرایه‌ای از مجوزهایی که درخواست شده‌اند.
        grantResults: IntArray // آرایه‌ای از نتایج اعطا/رد مجوزها.
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults) // فراخوانی متد والد.
        // اگر نتیجه مربوط به درخواست مجوز دوربین ما باشد.
        if (requestCode == CAMERA_PERMISSION_CODE) { // بررسی می‌کند که آیا این پاسخ مربوط به درخواست مجوز دوربین ما است.
            // اگر مجوز داده شد.
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) { // بررسی می‌کند که نتایج خالی نباشند و مجوز دوربین اعطا شده باشد.
                // پس از دریافت موفقیت‌آمیز مجوز، AR را راه‌اندازی کن و سپس دیالوگ آموزشی را نمایش بده.
                if (checkARSupport()) { // اگر دستگاه از AR پشتیبانی کند.
                    presenter.setupAR() // راه‌اندازی AR توسط Presenter.
                    // دیالوگ آموزشی را با تاخیر نمایش می‌دهیم تا از تداخل جلوگیری کنیم.
                    Handler(Looper.getMainLooper()).postDelayed({ // استفاده از Handler برای اجرای کد با تاخیر.
                        showTutorialDialog() // نمایش دیالوگ آموزشی پس از تاخیر.
                    }, 300) // تاخیر 300 میلی‌ثانیه.
                }
            } else {
                // اگر مجوز رد شد، پیامی نمایش داده و برنامه را ببند.
                Toast.makeText(this, "مجوز دوربین برای قابلیت AR ضروری است.", Toast.LENGTH_LONG) // نمایش پیام به کاربر.
                    .show()
                finish() // بستن اکتیویتی اگر مجوز رد شود.
            }
        }
    }
}