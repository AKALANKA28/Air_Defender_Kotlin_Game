package com.example.airdefender

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Rect

class Bullet(res: Resources, private val screenRatioX: Float, private val screenRatioY: Float) {

    var x = 0
    var y = 0
    var width = 0
    var height = 0
    var bullet: Bitmap

    init {
        bullet = BitmapFactory.decodeResource(res, R.drawable.bullet)

        width = bullet.width
        height = bullet.height

        width /= 4
        height /= 4

        width = (width * screenRatioX).toInt()
        height = (height * screenRatioY).toInt()

        bullet = Bitmap.createScaledBitmap(bullet, width, height, false)
    }

    fun getCollisionShape(): Rect {
        return Rect(x, y, x + width, y + height)
    }
}
