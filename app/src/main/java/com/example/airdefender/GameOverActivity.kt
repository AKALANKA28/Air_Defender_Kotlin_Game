package com.example.airdefender


import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class GameOverActivity : AppCompatActivity() {

    private var isMute = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_over)

        val score = intent.getIntExtra("SCORE", 0)
        val highScore = intent.getIntExtra("HIGHSCORE", 0)

        val scoreTextView: TextView = findViewById(R.id.scoreTextView)
        scoreTextView.text = "Score: $score"

        val highScoreTxt = findViewById<TextView>(R.id.highScoreTxt)

        val prefs = getSharedPreferences("game", MODE_PRIVATE)
        highScoreTxt.text = "HighScore: " + prefs.getInt("highscore", 0)

        findViewById<View>(R.id.playAgain).setOnClickListener {
        startActivity(Intent(this, GameActivity::class.java))
            finish()
        }

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
