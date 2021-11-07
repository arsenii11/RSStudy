package com.example.finances.toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceFragmentCompat;

import com.example.finances.MainActivity;
import com.example.finances.R;
import com.example.finances.ui.account.AccountActivity;

import maes.tech.intentanim.CustomIntent;

public class SettingsActivity extends AppCompatActivity {

    private String ACTIVITY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);

        ACTIVITY = getIntent().getStringExtra("ACTIVITY");

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.settings, new SettingsFragment())
                    .commit();
        }
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        ImageView toolbarImage = findViewById(R.id.toolbar_image);
        toolbarImage.setVisibility(View.INVISIBLE);
        ImageButton settings = findViewById(R.id.settings_bt);
        settings.setVisibility(View.INVISIBLE);
    }


    public static class SettingsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        menu.getItem(1).setVisible(false);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        Intent finalIntent;
        switch (ACTIVITY){
            case "MAIN": finalIntent = new Intent(this, MainActivity.class); break;
            case "ACCOUNT": finalIntent = new Intent(this, AccountActivity.class); break;
            default: finalIntent = new Intent(this, MainActivity.class); break;
        }

        if (id == android.R.id.home) {
            finalIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(finalIntent);
            CustomIntent.customType(this,"fadein-to-fadeout");
            finish();
            return true;
        }

        if(id == R.id.item2){
            try{
                Intent intent = new Intent(this, About.class);
                startActivity(intent);
                CustomIntent.customType(this,"right-to-left");
                finish();
            }

            catch (Exception E){

            }
        }
        return super.onOptionsItemSelected(item);


    }

    @Override
    protected void onPause(){
        super.onPause();

    }

    protected void onStop(){
        super.onStop();

    }


}