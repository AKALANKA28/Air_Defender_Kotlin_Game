package com.example.airdefender

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.SoundPool
import android.os.Build
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView
import kotlin.random.Random


class GameView(private val activity: GameActivity, private val screenX: Int, private val screenY: Int) : SurfaceView(activity), Runnable {
    private var thread: Thread? = null
    private var isPlaying:kotlin.Boolean = false
    private var isGameOver:kotlin.Boolean = false
//    private val screenX = 0
//    private  var screenY:Int = 0
    private  var score:Int = 0

    private var paint = Paint()
    private var screenRatioX: Float = 0f
    private var screenRatioY: Float = 0f
    private var flight: Flight? = null
    private var bullets: MutableList<Bullet>? = null // Change to MutableList
//    private var activity: GameActivity? = null
    private var soundPool: SoundPool? = null
    private var sound = 0

    private var background1: Background
    private var background2: Background
    private val missiles: Array<Missile>
    private val random: Random = Random

    private var prefs: SharedPreferences? = null


    init{

        prefs = activity.getSharedPreferences("game", Context.MODE_PRIVATE);



        soundPool = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val audioAttributes = AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .setUsage(AudioAttributes.USAGE_GAME)
                .build()
            SoundPool.Builder()
                .setAudioAttributes(audioAttributes)
                .build()
        } else SoundPool(1, AudioManager.STREAM_MUSIC, 0)

        sound = soundPool!!.load(activity, R.raw.shoot, 1)

        screenRatioX = 1920f / screenX.toFloat()
        screenRatioY = 1080f / screenY.toFloat()



        background1 = Background(screenX, screenY, resources)
        background2 = Background(screenX, screenY, resources)

        flight = Flight(this, screenY, resources, screenRatioX, screenRatioY)

        bullets = ArrayList()

        background2.x = screenX
        paint = Paint()
        paint.setTextSize(128F);
        paint.setColor(Color.WHITE);

        missiles = Array(4) { Missile(resources) }


        for (i in 0..3) {
            val missile = Missile(resources)
            missiles[i] = missile
        }
    }

    override fun run() {
        while (isPlaying) {
            update()
            draw()
            sleep()
        }
    }

    private fun update() {
        background1.x -= (10 * screenRatioX).toInt()
        background2.x -= (10 * screenRatioX).toInt()

        if (background1.x + background1.background.width < 0) {
            background1.x = screenX
        }
        if (background2.x + background2.background.width < 0) {
            background2.x = screenX
        }

        if (flight!!.isGoingUp)
            flight!!.y -= (30 * screenRatioY).toInt()
        else
            flight!!.y += (30 * screenRatioY).toInt()

        if (flight!!.y < 0)
            flight!!.y = 0

        if (flight!!.y >= screenY - flight!!.height)
            flight!!.y = screenY - flight!!.height

        val trash = ArrayList<Bullet>()
        bullets?.forEach { bullet ->
            if (bullet.x > screenX)
                trash.add(bullet)
            bullet.x += (50 * screenRatioX).toInt()


            for (bird in missiles) {
                if (Rect.intersects(
                        bird.getCollisionShape(),
                        bullet.getCollisionShape()
                    )
                ) {
                    score++;
                    bird.x = -500
                    bullet.x = screenX + 500
                    bird.wasShot = true
                }
            }
        }



        bullets?.removeAll(trash)

        bullets?.forEach { bullet ->
            bullet.x += (50 * screenRatioX).toInt()
        }



        for (bird in missiles) {
            bird.x -= bird.speed

            if (bird.x + bird.width < 0) {

                if (!bird.wasShot) {
                    isGameOver = true
                    return
                }
                val bound = (30 * screenRatioX).toInt()
                bird.speed = random.nextInt(bound)
                if (bird.speed < 10 * screenRatioX) bird.speed = (10 * screenRatioX).toInt()
                bird.x = screenX
                bird.y = random.nextInt(screenY - bird.height)
                bird.wasShot = false
            }
            if (Rect.intersects(bird.getCollisionShape(), flight!!.getCollisionShape())) {
                isGameOver = true
                return
            }
        }

    }



    private fun draw() {
        val holder: SurfaceHolder = holder
        if (holder.surface.isValid) {
            val canvas: Canvas = holder.lockCanvas()
            canvas.drawBitmap(background1.background, background1.x.toFloat(), background1.y.toFloat(), paint)
            canvas.drawBitmap(background2.background, background2.x.toFloat(), background2.y.toFloat(), paint)

            for (bird in missiles) {
                val birdRect = Rect(bird.x, bird.y, bird.x + bird.width, bird.y + bird.height)
                canvas.drawBitmap(bird.getBird(), null, birdRect, paint)
            }
            canvas.drawText(score.toString(), screenX / 2f, 164f, paint)

            if (isGameOver) {
                isPlaying = false;
                flight?.getDead()?.let { deadBitmap ->
                    canvas.drawBitmap(deadBitmap, flight?.x?.toFloat() ?: 0f, flight?.y?.toFloat() ?: 0f, paint)
                }
                getHolder().unlockCanvasAndPost(canvas);

                saveIfHighScore();
                waitBeforeExiting ();

                return;
            }
            // Safe call to getFlight() method
            flight?.let {
                canvas.drawBitmap(it.getFlight(), it.x.toFloat(), it.y.toFloat(), paint)
            }

            bullets?.forEach { bullet ->
                canvas.drawBitmap(bullet.bullet, bullet.x.toFloat(), bullet.y.toFloat(), paint)
            }
            holder.unlockCanvasAndPost(canvas)
        }
    }

    private fun saveIfHighScore() {
        if (prefs!!.getInt("highscore", 0) < score) {
            val editor = prefs!!.edit()
            editor.putInt("highscore", score)
            editor.apply()
        }
    }

    private fun waitBeforeExiting() {
        try {
            Thread.sleep(3000)
            val intent = Intent(activity, GameOverActivity::class.java)
            intent.putExtra("SCORE", score) // Pass the current score
            intent.putExtra("HIGHSCORE", prefs!!.getInt("highscore", 0)) // Pass the high score
            activity.startActivity(intent)
            activity.finish()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }



    private fun sleep() {
        try {
            Thread.sleep(17)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }

    fun resume() {
        isPlaying = true
        thread = Thread(this)
        thread?.start()
    }

    fun pause() {
        try {
            isPlaying = false
            thread?.join()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> if (event.x < screenX / 2) {
                flight!!.isGoingUp = true
            }

            MotionEvent.ACTION_UP -> {
                flight!!.isGoingUp = false
                if (event.x > screenX / 2)
                    flight!!.toShoot++

            }
        }
        return true
    }

    fun newBullet() {
        if (prefs?.getBoolean("isMute", false) == false) {
            soundPool?.play(sound, 1F, 1F, 0, 0, 1F)
        }
        val bullet = Bullet(resources, screenRatioX, screenRatioY) // Pass screenRatioX and screenRatioY
        bullet.x = flight!!.x + flight!!.width
        bullet.y = flight!!.y + flight!!.height / 2
        bullets?.add(bullet)
    }

}
