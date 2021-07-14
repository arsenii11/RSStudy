package com.example.finances.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.widget.NestedScrollView;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.signature.ObjectKey;
import com.example.finances.MainActivity;
import com.example.finances.R;
import com.example.finances.database.Course;
import com.example.finances.database.DBHelper;
import com.example.finances.database.Lesson;
import com.example.finances.events.course.ClosedCourses;
import com.example.finances.events.course.CourseAdapter;
import com.example.finances.helpclasses.SquaredConstraintLayout;
import com.example.finances.toolbar.SettingsActivity;
import com.github.florent37.kotlin.pleaseanimate.PleaseAnim;
import com.github.florent37.kotlin.pleaseanimate.core.Expectations;
import com.github.okdroid.checkablechipview.CheckableChipView;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;
import maes.tech.intentanim.CustomIntent;
import nz.co.trademe.covert.Covert;

public class Account extends AppCompatActivity implements  Function2<CheckableChipView, Boolean, Unit> {

    private final int GALLERY_REQUEST = 1;
    public static final String APP_PREFERENCES_Path = "Nickname" ;
    public SharedPreferences profile;

    private final int PICK_IMAGE_REQUEST = 1;
    public View background;
    private Activity activityAccount;
    public View AnimDivider;
    public String FilePath ="";
    public ProgressBar simpleProgressBar;
    public ImageButton accountFullBt;
    public SquaredConstraintLayout lay_photo;
    public TextView progressText;
    public Uri selectedImageUri;
    public TextView Surname;
    public boolean flag;
    ImageButton Settings;
    ImageButton ClosedCourses;
    ImageButton Close;

    CircleImageView profileImage; //изображение профиля
    Button ViewAllBt;


    DBHelper dbHelper;
    CourseAdapter courseAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        flag = true;

        //exit button
        Close = findViewById(R.id.CloseAccount);


        //получаем адреса двух кнопок в ряду
        Settings = findViewById(R.id.settingsbtacc);
        ClosedCourses = findViewById(R.id.finishedCrsBt);

        //получаем адрес элементов из верхней части аккаунта
        profileImage = (CircleImageView) findViewById(R.id.ProfileImage);
        lay_photo = findViewById(R.id.ProfileImageLayout);
        // accountFullBt = view.findViewById(R.id.AccountFullBt);

        //устанавливаем изображение
        setProfileImage();

        ImageButton PhotoButton = findViewById(R.id.FirstPhotoButton);
        simpleProgressBar = findViewById(R.id.progressBar);
        ViewAllBt = findViewById(R.id.ViewAllBt);



        //устанавливаем никнейм
        SharedPreferences getInfo = PreferenceManager.getDefaultSharedPreferences(this);
        String nickname = getInfo.getString("Nickname", "");
        TextView nickname_1 = findViewById(R.id.nickname);
        nickname_1.setText(nickname);
        if (nickname.isEmpty()) {
            nickname_1.setText("@Nickname");
        }

        //устанавливаем email
        String email = getInfo.getString("Educational institution", "");
        TextView Email = findViewById(R.id.edutext);
        Email.setText(email);
        if (email.isEmpty()) {
            Email.setText("");
        }

        //устанавливаем имя
        String NameSur = getInfo.getString("Surname", "");
        Surname = findViewById(R.id.name);//имя фамилия
        Surname.setText(NameSur);
        if (NameSur.isEmpty()) {
            Surname.setText("Name Surname");
        }



        Close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Account.this, MainActivity.class);
                //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                CustomIntent.customType(Account.this,"fadein-to-fadeout");
                finish();
            }
        });

        Settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Account.this, SettingsActivity.class);
                //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                CustomIntent.customType(Account.this,"fadein-to-fadeout");
                finish();
            }
        });

        ClosedCourses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Account.this, ClosedCourses.class);
                //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                CustomIntent.customType(Account.this,"fadein-to-fadeout");
                finish();
            }
        });



        //кнопка выбора изображения
        PhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, GALLERY_REQUEST);
            }
        });


    }


    @Override
    public void onStart() {
        super.onStart();
        simpleProgressBar.setVisibility(View.INVISIBLE);
        //устанавливаю строку из SharedPreferences

    }

    @Override
    public void onResume(){
        super.onResume();
     /* CheckableChipView hide_courses = (CheckableChipView) view.findViewById(R.id.hide_courses);
        if(hide_courses.isChecked()){
            hide_courses.setOnCheckedChangeListener(AccountFragment.this);
            RecyclerView CoursesList = (RecyclerView) view.findViewById(R.id.list);
            CoursesList.setVisibility(View.INVISIBLE);
            Snackbar snackbar = Snackbar.make(view, "Hello Android", Snackbar.LENGTH_LONG);
            snackbar.show();
        }*/
    }




    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            CircleImageView profileImage =  findViewById(R.id.ProfileImage);
            profileImage.setImageResource(R.drawable.no_avatar);
            selectedImageUri = data.getData();
            Bitmap bitmap;
            //Сохраняем изображение в файл
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImageUri);
                new fileFromBitmap("ProfileFoto", bitmap, getApplicationContext()).execute();


            } catch (IOException e) {
                e.printStackTrace();
            }



        }
    }


    File f;

    @Override
    public Unit invoke(CheckableChipView checkableChipView, Boolean aBoolean) {
        return null;
    }


    public class fileFromBitmap extends AsyncTask<Void, Integer, String> {

        Context context;
        Bitmap bitmap;
        String fileNameToSave;

        public fileFromBitmap(String fileNameToSave,Bitmap bitmap,Context context) {
            this.bitmap = bitmap;
            this.context= context;
            this.fileNameToSave = fileNameToSave;

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // before executing doInBackground
            // update your UI
            // exp; make progressbar visible
        }



        @Override
        protected String doInBackground(Void... params) {
            f = new File(context.getFilesDir(), fileNameToSave+".jpg");
            try {
                f.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                //write the bytes in file
                FileOutputStream fos = new FileOutputStream(f);
                bitmap.compress(Bitmap.CompressFormat.JPEG,25 , fos);
                fos.flush();
                fos.close();
                FilePath = f.getPath();
                profile = getSharedPreferences(APP_PREFERENCES_Path,Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = profile.edit();
                editor.putString("key1", String.valueOf(FilePath)).apply();
            }catch (Exception e){
                e.printStackTrace();
            }

            for (int i = 0; i < 101; i += 1) {
                try {
                    Thread.sleep(10);
                    publishProgress(i);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            publishProgress(101);
            return null;
        }


        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            // back to main thread after finishing doInBackground
            // update your UI or take action after
            // exp; make progressbar gone
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            simpleProgressBar.setVisibility(View.INVISIBLE);
            CircleImageView profileImage = findViewById(R.id.ProfileImage);
            Glide.with(getApplicationContext()).load(selectedImageUri).into(profileImage);
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            if(flag){
                simpleProgressBar.setVisibility(View.VISIBLE);
            }
        }
    }
    public void setProfileImage(){
        SharedPreferences accountPhoto = getSharedPreferences(APP_PREFERENCES_Path, Context.MODE_PRIVATE);
        FilePath= accountPhoto.getString("key1", "");
        File Photo = new File(FilePath);
        if (Photo.exists()) {
            try {
                CircleImageView profileImage = findViewById(R.id.ProfileImage);
                Glide.with(this).load(Photo).signature(new ObjectKey(String.valueOf(System.currentTimeMillis()))).into(profileImage);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
