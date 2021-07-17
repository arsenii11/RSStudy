package com.example.finances

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager

class SplashScreenActivity : AppCompatActivity() {
    val APP_PREFERENCES = "FirstEnter"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        val iv_note: ImageView by lazy { findViewById(R.id.rediss) }
        iv_note.alpha = 0f
        iv_note.animate().setDuration(2000).alpha(1f).withEndAction(){


            //обращаемся к настройкам
            val getInfo = PreferenceManager.getDefaultSharedPreferences(this)
            val EnterNum = getInfo.getString("FirstEnter", "")

            if (EnterNum.equals( "NotFirst")) {
                    val i = Intent(this,MainActivity::class.java)
                    startActivity(i)
                    overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out)
                    finish()}
        else{
                val Enter1 = getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE)
                var editor = Enter1.edit()
                editor.putString(APP_PREFERENCES, "NotFirst").apply()
            val i = Intent(this,QuestionsActivity::class.java)
            startActivity(i)
            overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out)
            finish()
        }
    }}
}