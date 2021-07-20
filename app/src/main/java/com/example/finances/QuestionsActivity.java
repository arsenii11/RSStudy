package com.example.finances;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import maes.tech.intentanim.CustomIntent;

public class QuestionsActivity extends AppCompatActivity {

   AppCompatButton clear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questions);
        clear = findViewById(R.id.clear_bt);

        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(QuestionsActivity.this, MainActivity.class);
                startActivity(intent);
                CustomIntent.customType(QuestionsActivity.this,"right-to-left");
                finish();
            }
        });
    }
}