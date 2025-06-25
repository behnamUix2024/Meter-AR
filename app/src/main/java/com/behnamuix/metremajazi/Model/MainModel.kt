package com.behnamuix.metremajazi.Model

import com.google.ar.sceneform.math.Vector3
import kotlin.math.sqrt

/**
 * کلاس MainModel (مدل اصلی) مسئول نگهداری منطق داده‌ها و عملیات‌های مربوط به آن‌هاست.
 * در الگوی معماری MVP، مدل از View و Presenter مستقل است و فقط داده‌ها و قوانین تجاری را مدیریت می‌کند.
 * در این برنامه، وظیفه اصلی آن محاسبه فاصله بین دو نقطه در فضای سه‌بعدی است.
 */
class MainModel {

        var TAPSELL_KEY="tdbrqtnhdnscsecenjnajtmeoagfcjjqtdjkgocodmpptlebhnifpbqakcnjrimtetrtmi"
        var ZONE_ID="685bd4f32973e423038e7149"

    /**
     * این تابع فاصله اقلیدسی (Euclidean distance) بین دو نقطه در فضای سه‌بعدی را محاسبه می‌کند.
     * این فاصله، کوتاه‌ترین خط مستقیم بین دو نقطه است.
     *
     * @param point1 اولین نقطه در فضای سه‌بعدی، از نوع Vector3 (که شامل مختصات x, y, z است).
     * @param point2 دومین نقطه در فضای سه‌بعدی، از نوع Vector3.
     * @return فاصله محاسبه شده بین دو نقطه از نوع Float.
     */
    fun calculateDistance(point1: Vector3, point2: Vector3): Float {
        // محاسبه اختلاف در مختصات x بین دو نقطه
        val dx = point1.x - point2.x
        // محاسبه اختلاف در مختصات y بین دو نقطه
        val dy = point1.y - point2.y
        // محاسبه اختلاف در مختصات z بین دو نقطه
        val dz = point1.z - point2.z

        // استفاده از فرمول فاصله اقلیدسی: sqrt(dx^2 + dy^2 + dz^2)
        // این فرمول بر اساس قضیه فیثاغورس در فضای سه‌بعدی عمل می‌کند.
        return sqrt(dx * dx + dy * dy + dz * dz)
    }


}
