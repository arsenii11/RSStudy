package com.example.finances.events.lesson;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.finances.MainActivity;
import com.example.finances.R;
import com.example.finances.database.DBHelper;
import com.example.finances.database.LessonOptions;
import com.example.finances.ui.settings.SettingsActivity;

import maes.tech.intentanim.CustomIntent;

public class LessonActivity extends AppCompatActivity {

    private final String ACTIVITY = "LESSON";

    TextView lessonLabel; //TextView названия урока
    Button rescheduleLesson; //Кнопка перенести урок
    EditText lessonDescription; //EditText для заметок

    private int LESSON_ID; //ID урока

    DBHelper dbHelper; //Обработчик запросов к БД

    @Override
    protected void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lesson_info);

        lessonLabel = findViewById(R.id.LabelLessonName); //Ищем в view TextView, предназначенный для названия урока
        rescheduleLesson = findViewById(R.id.rescheduleLesson); //Ищем в view Button, предназначенную для переноса урока
        lessonDescription = findViewById(R.id.lessonDescription); //Ищем в view EditText, предназначенный для ввода и вывода заметок урока

        LESSON_ID = getIntent().getIntExtra("LESSON_ID", -1); //Получаем значение ID из переданных данных вызванного намерения

        dbHelper = new DBHelper(this); //Создаем новый обработчик запросов к БД

        setInitialData(); //Вызываем функцию установки значений из БД


        //Верхний тулбар
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar4);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        //скрываем изображение на toolbar
        ImageView toolbarImage = findViewById(R.id.toolbar_image);
        toolbarImage.setVisibility(View.INVISIBLE);
        ImageButton settings = findViewById(R.id.settings_bt);
        settings.setVisibility(View.INVISIBLE);
        TextView titleshadow = toolbar.findViewById(R.id.toolbar_shadowtext);
        titleshadow.setVisibility(View.INVISIBLE);

        //Устанавливаем функцию при нажатии на кнопку перести урок
        rescheduleLesson.setOnClickListener(v -> {
            Intent intent = new Intent(LessonActivity.this, RescheduleLessonActivity.class); //Создаем намерение перехода на активность с переносом текущего теста
            intent.putExtra("LESSON_ID", LESSON_ID); //Передаем в намерение id теста
            intent.putExtra("ACTIVITY", ACTIVITY);
            startActivity(intent); //Запускаем намерение
            CustomIntent.customType(LessonActivity.this,"left-to-right"); //Добавляем анимацию к переходу
            finish();
        });
    }

    private void setInitialData(){
        lessonLabel.setText(dbHelper.getLesson(LESSON_ID).getName()); //Устанавливаем название урока
        lessonDescription.setText(dbHelper.getLessonOptions(LESSON_ID).getDescription()); //Устанавливаем заметки к уроку
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
                Intent intent = new Intent(LessonActivity.this, SettingsActivity.class);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();

        LessonOptions lessonOptions = dbHelper.getLessonOptions(LESSON_ID);
        lessonOptions.setDescription(lessonDescription.getText().toString());
        dbHelper.updateLessonOptions(lessonOptions.getId(), lessonOptions);
    }
}
