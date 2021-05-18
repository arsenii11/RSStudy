package com.example.finances

import android.content.Intent
import android.media.Image
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView

class SplashScreenActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        val iv_note: ImageView by lazy { findViewById(R.id.rediss) }
        iv_note.alpha = 0f
        iv_note.animate().setDuration(2000).alpha(1f).withEndAction(){



        val i = Intent(this,MainActivity::class.java)
        startActivity(i)
        overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out)
        finish()
        }
    }
}