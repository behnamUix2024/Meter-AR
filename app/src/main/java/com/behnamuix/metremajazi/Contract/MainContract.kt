package com.behnamuix.metremajazi.Contract

import com.google.ar.core.HitResult
import com.google.ar.core.Plane

interface MainContract {
    interface View {
        fun showDistance(distance: Float)
        fun showError(message: String)
    }

    interface Presenter {
        fun setupAR()
        fun onTapPlane(hitResult: HitResult, plane: Plane)
        fun onDestroy()
    }
}