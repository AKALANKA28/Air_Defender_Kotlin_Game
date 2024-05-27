package com.example.airdefender

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Rect

class Missile(res: Resources) {

    var speed = 100
    var wasShot = true
    var x = 0
    var y: Int
    var width: Int
    var height: Int
    var birdCounter = 1
    var bird1: Bitmap
//    var bird2: Bitmap
//    var bird3: Bitmap
    var bird4: Bitmap


    init {
        bird1 = BitmapFactory.decodeResource(res, R.drawable.frame1)
//        bird2 = BitmapFactory.decodeResource(res, R.drawable.frame2)
//        bird3 = BitmapFactory.decodeResource(res, R.drawable.missile2)
        bird4 = BitmapFactory.decodeResource(res, R.drawable.frame2)

        var screenWidth = Resources.getSystem().displayMetrics.widthPixels
        var screenHeight = Resources.getSystem().displayMetrics.heightPixels

        width = bird1.width
        height = bird1.height

        width /= 8
        height /= 8

        val screenRatioX = screenWidth.toFloat() / 1920
        val screenRatioY = screenHeight.toFloat() / 1080

        width = (width * screenRatioX).toInt()
        height = (height * screenRatioY).toInt()

        bird1 = Bitmap.createScaledBitmap(bird1, width, height, false)
//        bird2 = Bitmap.createScaledBitmap(bird2, width, height, false)
//        bird3 = Bitmap.createScaledBitmap(bird3, width, height, false)
        bird4 = Bitmap.createScaledBitmap(bird4, width, height, false)

        y = -height
    }

    fun getBird(): Bitmap {
        when (birdCounter) {
            1 -> {
                birdCounter++
                return bird1
            }
//            2 -> {
//                birdCounter++
//                return bird2
//            }
//            3 -> {
//                birdCounter++
//                return bird3
//            }
            else -> {
                birdCounter = 1
                return bird4
            }
        }
    }

    fun getCollisionShape(): Rect {
        return Rect(x, y, x + width, y + height)
    }
}