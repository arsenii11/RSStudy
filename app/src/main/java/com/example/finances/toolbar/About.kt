package com.example.finances.toolbar

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.finances.MainActivity
import com.example.finances.R
import maes.tech.intentanim.CustomIntent

class About : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayShowTitleEnabled(false)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)

        val toolbarImage = findViewById<ImageView>(R.id.toolbar_image)
        toolbarImage.visibility = View.INVISIBLE
        val settings = findViewById<ImageButton>(R.id.settings_bt)
        settings.visibility = View.INVISIBLE
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        menu.getItem(0).isVisible = false
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
            CustomIntent.customType(this, "fadein-to-fadeout")
            return true
        }
        if (id == R.id.item4) {
            try {
                val intent = Intent(this, SettingsActivity::class.java)
                startActivity(intent)
                CustomIntent.customType(this, "left-to-right")
                finish()
            } catch (E: Exception) {
                E.printStackTrace()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onPause() {
        super.onPause()
        finish()
    }
}