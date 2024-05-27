package com.example.airdefender

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {
    private var isMute = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        findViewById<View>(R.id.play).setOnClickListener {
            startActivity(Intent(this@MainActivity, GameActivity::class.java))
        }


        val highScoreTxt = findViewById<TextView>(R.id.highScoreTxt)

        val prefs = getSharedPreferences("game", MODE_PRIVATE)
        highScoreTxt.text = "HighScore: " + prefs.getInt("highscore", 0)

        isMute = prefs.getBoolean("isMute", false)

        val volumeCtrl = findViewById<ImageView>(R.id.volumeCtrl)

        if (isMute) volumeCtrl.setImageResource(R.drawable.baseline_volume_off_24) else volumeCtrl.setImageResource(
            R.drawable.baseline_volume_up_24
        )

        volumeCtrl.setOnClickListener {
            isMute = !isMute
            if (isMute) volumeCtrl.setImageResource(R.drawable.baseline_volume_off_24) else volumeCtrl.setImageResource(
                R.drawable.baseline_volume_up_24
            )
            val editor = prefs.edit()
            editor.putBoolean("isMute", isMute)
            editor.apply()
        }
    }
}
