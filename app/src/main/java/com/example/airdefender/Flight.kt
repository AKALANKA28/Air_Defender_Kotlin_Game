package com.example.airdefender;

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Rect

class Flight(private val gameView: GameView, screenY: Int, res: Resources, private val screenRatioX: Float, private val screenRatioY: Float) {

    var toShoot = 0
    var isGoingUp = false
    var x = 0
    var y: Int
    var width: Int
    var height: Int
    private var wingCounter = 0
    private var shootCounter = 1
    private var flight1: Bitmap
    private var flight2: Bitmap
    private var shoot1: Bitmap
    private var shoot2: Bitmap
    private var shoot3: Bitmap
    private var shoot4: Bitmap
    private var shoot5: Bitmap
    private var dead: Bitmap

    init {
        flight1 = BitmapFactory.decodeResource(res, R.drawable.fly1)
        flight2 = BitmapFactory.decodeResource(res, R.drawable.fly2)

        width = flight1.width
        height = flight1.height

        width /= 4
        height /= 4

        width = (width * screenRatioX).toInt()
        height = (height * screenRatioY).toInt()

        flight1 = Bitmap.createScaledBitmap(flight1, width, height, false)
        flight2 = Bitmap.createScaledBitmap(flight2, width, height, false)

        shoot1 = BitmapFactory.decodeResource(res, R.drawable.shoot1)
        shoot2 = BitmapFactory.decodeResource(res, R.drawable.shoot2)
        shoot3 = BitmapFactory.decodeResource(res, R.drawable.shoot3)
        shoot4 = BitmapFactory.decodeResource(res, R.drawable.shoot4)
        shoot5 = BitmapFactory.decodeResource(res, R.drawable.shoot5)

        shoot1 = Bitmap.createScaledBitmap(shoot1, width, height, false)
        shoot2 = Bitmap.createScaledBitmap(shoot2, width, height, false)
        shoot3 = Bitmap.createScaledBitmap(shoot3, width, height, false)
        shoot4 = Bitmap.createScaledBitmap(shoot4, width, height, false)
        shoot5 = Bitmap.createScaledBitmap(shoot5, width, height, false)

        dead = BitmapFactory.decodeResource(res, R.drawable.dead)
        dead = Bitmap.createScaledBitmap(dead, width, height, false)

        y = screenY / 2
        x = (64 * screenRatioX).toInt()
    }

    fun getFlight(): Bitmap {
        if (toShoot != 0) {
            if (shootCounter == 1) {
                shootCounter++
                return shoot1
            }
            if (shootCounter == 2) {
                shootCounter++
                return shoot2
            }
            if (shootCounter == 3) {
                shootCounter++
                return shoot3
            }
            if (shootCounter == 4) {
                shootCounter++
                return shoot4
            }
            shootCounter = 1
            toShoot--
            gameView.newBullet()
            return shoot5
        }
        if (wingCounter == 0) {
            wingCounter++
            return flight1
        }
        wingCounter--
        return flight2
    }

    fun getCollisionShape(): Rect {
        return Rect(x, y, x + width, y + height)
    }

    fun getDead(): Bitmap {
        return dead
    }
}
