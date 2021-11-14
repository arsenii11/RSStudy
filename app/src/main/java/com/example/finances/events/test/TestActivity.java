package com.example.finances.events.test;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.finances.MainActivity;
import com.example.finances.R;
import com.example.finances.database.DBHelper;
import com.example.finances.ui.settings.SettingsActivity;

import maes.tech.intentanim.CustomIntent;

public class TestActivity extends AppCompatActivity {

    TextView testLabel; //TextView названия теста
    Button rescheduleTest; //Кнопка перенести тест

    private int TEST_ID; //ID теста

    DBHelper dbHelper; //Обработчик запросов к БД

    @Override
    protected void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_info);

        testLabel = findViewById(R.id.LabelTestName); //Ищем в view TextView, предназначенный для названия теста
        rescheduleTest = findViewById(R.id.rescheduleTest); //Ищем в view Button, предназначенную для переноса теста

        ImageView toolbarImage = findViewById(R.id.toolbar_image);
        toolbarImage.setVisibility(View.INVISIBLE);

        TEST_ID = getIntent().getIntExtra("TEST_ID", -1); //Получаем значение ID из переданных данных вызванного намерения

        dbHelper = new DBHelper(this); //Создаем новый обработчик запросов к БД

        setInitialData(); //Вызываем функцию установки значений из БД

        //Устанавливаем функцию при нажатии на кнопку перести тест
        rescheduleTest.setOnClickListener(v -> {
            Intent intent = new Intent(TestActivity.this, RescheduleTestActivity.class); //Создаем намерение перехода на активность с переносом текущего теста
            intent.putExtra("TEST_ID", TEST_ID); //Передаем в намерение id теста
            startActivity(intent); //Запускаем намерение
            CustomIntent.customType(TestActivity.this,"left-to-right"); //Добавляем анимацию к переходу
            finish();
        });
    }

    private void setInitialData(){
        testLabel.setText(dbHelper.getTest(TEST_ID).getName()); //Устанавливаем название теста
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        menu.getItem(0).setVisible(false);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.settingsMenuItem) {
            try {
                Intent intent = new Intent(TestActivity.this, SettingsActivity.class);
                startActivity(intent);
                CustomIntent.customType(this,"fadein-to-fadeout");


            } catch (Exception E) {

            }
        }
        if (id == android.R.id.home) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            CustomIntent.customType(this, "fadein-to-fadeout");
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
