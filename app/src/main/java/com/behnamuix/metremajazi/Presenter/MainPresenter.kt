package com.behnamuix.metremajazi.Presenter

import android.content.Context
import android.hardware.camera2.CameraManager
import android.widget.Toast // این ایمپورت را نگه می‌داریم اما از آن در Presenter استفاده نمی‌کنیم
import com.behnamuix.metremajazi.Ads.TapsellApi
import com.behnamuix.metremajazi.Contract.MainContract
import com.behnamuix.metremajazi.Model.MainModel
import com.behnamuix.metremajazi.R
import com.google.ar.core.HitResult
import com.google.ar.core.Plane
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.Node
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.rendering.ViewRenderable
import com.google.ar.sceneform.ux.ArFragment

class MainPresenter(
    private val view: MainContract.View,
    private val model: MainModel,
    private val ctx: Context // Context معتبر که از MainActivity دریافت می‌شود
) : MainContract.Presenter {


    private lateinit var arFragment: ArFragment
    private var anchorNode: AnchorNode? = null
    private var measurementNode: Node? = null

    override fun setupAR() {
        // پیاده‌سازی منطق ARCore
        // اطمینان حاصل می‌کنیم که Renderable قبل از نیاز به آن ساخته شده باشد
        buildDistanceRenderable()
        setupTapListener()
    }

    // این تابع Renderable را برای نمایش فاصله می‌سازد
    private fun buildDistanceRenderable() {
        ViewRenderable.builder()
            .setView(ctx, R.layout.distance_view) // از 'ctx' به جای 'view as Context' استفاده شد
            .build()
            .thenAccept { renderable ->
                measurementNode = Node().apply { this.renderable = renderable }
                // می‌توانید اینجا پیامی بدهید که Renderable آماده است
            }
            .exceptionally { throwable ->
                view.showError("خطا در ساخت رندرابل: ${throwable.message}")
                // Toast مستقیم از Presenter حذف شد
                null
            }
    }

    // این تابع شنونده لمس روی سطح AR را تنظیم می‌کند
    private fun setupTapListener() {
        arFragment.setOnTapArPlaneListener { hitResult: HitResult, plane: Plane, _ ->
            // اگر measurementNode هنوز آماده نیست، به کاربر اطلاع دهید
            if (measurementNode == null) {
                view.showError("در حال آماده‌سازی UI اندازه‌گیری... لطفاً صبر کنید.")
                // Toast مستقیم از Presenter حذف شد
                return@setOnTapArPlaneListener
            }

            // اگر سطح شناسایی شده عمودی نبود، پیام خطا نمایش دهید
            if (plane.type != Plane.Type.VERTICAL) {
                view.showError("لطفاً یک دیوار (سطح عمودی) را انتخاب کنید.")
                // Toast مستقیم از Presenter حذف شد
                return@setOnTapArPlaneListener
            }

            onTapPlane(hitResult, plane)
        }
    }

    // این تابع پس از لمس یک سطح در AR فراخوانی می‌شود
    override fun onTapPlane(hitResult: HitResult, plane: Plane) {
        // اگر انکر قبلی وجود دارد، آن را از صحنه حذف کرده و detach کنید
        anchorNode?.let {
            arFragment.arSceneView.scene.removeChild(it)
            it.anchor?.detach() // جدا کردن انکر از ARCore
        }

        val anchor = hitResult.createAnchor() // یک انکر جدید در نقطه لمس ایجاد کنید
        anchorNode = AnchorNode(anchor).apply {
            // دریافت موقعیت دوربین و انکر در فضای جهانی
            val cameraPosition = arFragment.arSceneView.scene.camera.worldPosition
            val anchorPosition = this.worldPosition // این Node (AnchorNode) موقعیت خود را از anchor می‌گیرد

            // محاسبه فاصله و نمایش آن در View
            val distance = model.calculateDistance(cameraPosition, anchorPosition)
            view.showDistance(distance)

            // AnchorNode را به صحنه AR اضافه کنید
            arFragment.arSceneView.scene.addChild(this)

            // measurementNode (نمایشگر فاصله) را به عنوان فرزند AnchorNode قرار دهید
            measurementNode?.let { node ->
                node.setParent(this)
                // !!! مهم !!! موقعیت نمایشگر فاصله را اصلاح کنید
                // 100f بسیار بزرگ است. یک مقدار کوچک برای کمی آفست استفاده کنید
                // مثلاً 0.0f برای قرار دادن دقیقا روی انکر یا 0.1f برای کمی بالاتر/جلوتر
                // ممکن است بخواهید آن را کمی جلوتر از دیوار قرار دهید تا دید بهتری داشته باشد.
                // Vector3(0.0f, 0.0f, -0.05f) می‌تواند آن را 5 سانتی‌متر جلوتر در محور Z (دور از دیوار) قرار دهد.
                node.localPosition = Vector3(0.0f, 0.05f, 0.0f) // مثلا 5 سانتی‌متر بالاتر از انکر
            }
        }
    }

    override fun onDestroy() {
        // ARFragment به طور خودکار بیشتر منابع ARCore را مدیریت می‌کند.
        // arFragment.arSceneView?.destroy() را بهتر است در Activity در DisposableEffect انجام دهید
        // یا مطمئن شوید که Presenter lifecycle آن را به درستی مدیریت می‌کند.
        // اگر این کد در Presenter است و تنها یک بار فراخوانی می‌شود، می‌تواند مشکل ایجاد کند.
        // برای حالت XML و Fragment Manager، ArFragment خودش در onDestroyView/onDestroy فرگمنت مدیریت می‌شود.
        // پس این خط را می‌توان حذف کرد اگر ArFragment خودش مسئول مدیریت منابع است.
        // اگر حذف نکنیم، ممکن است خطای null-pointer یا IllegalStateException بدهد اگر view از بین رفته باشد.
        // بهتر است Presenter از view.onDestroy() از Activity صدا زده شود.
        // این خط در این نسخه Presenter حذف شد.
    }

    fun setArFragment(fragment: ArFragment) {
        this.arFragment = fragment
    }


}

