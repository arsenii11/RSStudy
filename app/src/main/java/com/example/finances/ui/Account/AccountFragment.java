package com.example.finances.ui.Account;

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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.signature.MediaStoreSignature;
import com.bumptech.glide.signature.ObjectKey;
import com.example.finances.R;
import com.example.finances.events.course.CourseAdapter;
import com.example.finances.events.course.CourseListActivity;
import com.example.finances.events.course.CourseName;
import com.example.finances.database.Course;
import com.example.finances.database.DBHelper;
import com.example.finances.database.Lesson;
import com.example.finances.helpclasses.SquaredConstraintLayout;
import com.github.florent37.kotlin.pleaseanimate.PleaseAnim;
import com.github.florent37.kotlin.pleaseanimate.core.Expectations;
import com.github.okdroid.checkablechipview.CheckableChipView;
import com.google.android.material.snackbar.Snackbar;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.Signature;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import jp.wasabeef.picasso.transformations.CropSquareTransformation;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;
import maes.tech.intentanim.CustomIntent;
import nz.co.trademe.covert.Covert;

import static android.app.Activity.RESULT_OK;

public class AccountFragment extends Fragment implements CompoundButton.OnCheckedChangeListener, Function2<CheckableChipView, Boolean, Unit>  {

    private final int GALLERY_REQUEST = 1;
    public static final String APP_PREFERENCES_Path = "Nickname" ;
    public SharedPreferences profile;
    private String imagePath;
    private final int PICK_IMAGE_REQUEST = 1;
    private View view;
    public View background;
    private Activity activityAccount;
    public View AnimDivider;
    public String FilePath ="";
    public ProgressBar simpleProgressBar;
    public ImageButton accountFullBt;
    public SquaredConstraintLayout lay_photo;
    public TextView progressText;
    public Uri selectedImageUri;
    public View backgroundColorTint;
    public TextView Surname;
    public boolean flag;

    ArrayList<Course> courses = new ArrayList<Course>();
    ArrayList<Lesson> lessons = new ArrayList<Lesson>();
    Button newCourse;
    CircleImageView profileImage; //изображение профиля
    Button ViewAllBt;

    Covert.Config config = new Covert.Config(R.drawable.ic_cancel_grey_24dp, R.color.white, R.color.ErrorText);
    Covert covert;

    DBHelper dbHelper;
    CourseAdapter courseAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        flag = true;


    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_account, container, false);
        Context context = getContext();


        //получаем адрес элементов из верхней части аккаунта
        profileImage = (CircleImageView) view.findViewById(R.id.ProfileImage);
        background = view.findViewById(R.id.background);
        lay_photo = view.findViewById(R.id.ProfileImageLayout);
        AnimDivider = view.findViewById(R.id.divideranim);
       // accountFullBt = view.findViewById(R.id.AccountFullBt);

        //устанавливаем изображение
        setProfileImage();



        //Список
        setInitialData();
        RecyclerView CoursesList = (RecyclerView) view.findViewById(R.id.list);
        // RecyclerView LessonsList = (RecyclerView) view.findViewById(R.id.Lessonlist);

        //свайпы для удаления курса из списка
        /*covert = Covert.with(config).setIsActiveCallback(viewHolder -> false).doOnSwipe((viewHolder, swipeDirection) -> {
            TextView textView = viewHolder.itemView.findViewById(R.id.CourseID);
            int id = Integer.parseInt(textView.getText().toString());
            dbHelper.deleteCourse(id);
            setInitialData();
            courseAdapter = new CourseAdapter(context, courses, CourseAdapter.AdapterMode.OpenCourse, false, null);
            CoursesList.setAdapter(courseAdapter);
            return null;
        }).attachTo(CoursesList);*/

        courseAdapter = new CourseAdapter(context, courses, CourseAdapter.AdapterMode.OpenCourse, false, null);

        // устанавливаем для списка адаптер
        CoursesList.setAdapter(courseAdapter);


        ImageButton PhotoButton = view.findViewById(R.id.FirstPhotoButton);
        simpleProgressBar = view.findViewById(R.id.progressBar);
        progressText = view.findViewById(R.id.progressText);
        ViewAllBt = view.findViewById(R.id.ViewAllBt);


        //Получаем activity
        activityAccount = getActivity();

        //устанавливаем никнейм
        SharedPreferences getInfo = PreferenceManager.getDefaultSharedPreferences(this.getContext());
        String nickname = getInfo.getString("Nickname", "");
        TextView nickname_1 = view.findViewById(R.id.nickname);
        nickname_1.setText(nickname);
        if (nickname.isEmpty()) {
            nickname_1.setText("@Nickname");
        }

        //устанавливаем email
        String email = getInfo.getString("E-mail", "");
        TextView Email = view.findViewById(R.id.email);
        Email.setText(email);
        if (email.isEmpty()) {
            Email.setText("nickname@email.com");
        }

        //устанавливаем имя
        String NameSur = getInfo.getString("Surname", "");
        Surname = view.findViewById(R.id.name);//имя фамилия
        Surname.setText(NameSur);
        if (NameSur.isEmpty()) {
            Surname.setText("Name Surname");
        }


        PleaseAnim pleaseAnim = new PleaseAnim();                                               //--------------------------------kotlin animation-------------------------
        pleaseAnim.animate(lay_photo, 1000f, new Function1<Expectations, Unit>() {
            @Override
            public Unit invoke(Expectations expectations) {
                expectations.topOfItsParent(10f,null);
                expectations.leftOfItsParent(20f, null);
                expectations.scale(0.72f,0.72f);
                return null;
            }
        });
        pleaseAnim.animate(profileImage, 1000f, new Function1<Expectations, Unit>() {
            @Override
            public Unit invoke(Expectations expectations) {
                expectations.topOfItsParent(10f,null);
                expectations.leftOfItsParent(20f, null);
                expectations.scale(0.72f,0.72f);
                return null;
            }
        });
        pleaseAnim.animate(Surname, 10f, new Function1<Expectations, Unit>() {
            @Override
            public Unit invoke(Expectations expectations) {
                expectations.centerHorizontalInParent();
                expectations.topOfItsParent(31f,null);
                expectations.alpha(1f);
                expectations.scale(0.85f,0.85f);
                return null;
            }
        });
        pleaseAnim.animate(Email, 10f, new Function1<Expectations, Unit>() {
            @Override
            public Unit invoke(Expectations expectations) {
             expectations.rightOfItsParent(20f,null);
             expectations.sameCenterVerticalAs(profileImage);
             expectations.invisible();
                return null;
            }
        });
        pleaseAnim.animate(nickname_1, 10f, new Function1<Expectations, Unit>() {
            @Override
            public Unit invoke(Expectations expectations) {
                expectations.rightOfItsParent(20f,null);
                expectations.belowOf(Email,10f, null);
                expectations.invisible();
                return null;
            }
        });
        pleaseAnim.animate(background, 10f, new Function1<Expectations, Unit>() {
            @Override
            public Unit invoke(Expectations expectations) {
            expectations.height(100, Gravity.LEFT, Gravity.TOP,false,true);

                return null;
            }
        });
        pleaseAnim.animate(AnimDivider, 10f, new Function1<Expectations, Unit>() {
            @Override
            public Unit invoke(Expectations expectations) {
                expectations.height(5,null,Gravity.BOTTOM,false, true);
                return null;
            }
        });

        pleaseAnim.animate(simpleProgressBar, 10f, new Function1<Expectations, Unit>() {
            @Override
            public Unit invoke(Expectations expectations) {
                expectations.invisible();
                flag = false;
                return null;
            }
        });

        NestedScrollView nestedscroll =  view.findViewById(R.id.nestedscrollAccount);
        nestedscroll.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
            float percents = scrollY * 2f / v.getMaxScrollAmount();
            pleaseAnim.setPercent(percents);

            }
        });

       // pleaseAnim.animate(view,100,);




        //Кнопка нового курса
        newCourse = view.findViewById(R.id.courseBt);
        newCourse.setClipToOutline(true);
        newCourse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AccountFragment.this.getActivity(), CourseName.class);
                startActivity(intent);
                CustomIntent.customType(getContext(),"left-to-right");
                getActivity().finish();
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

        //Кнопка раскрывающая список курсов
        ViewAllBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent ListCoursesList = new Intent(AccountFragment.this.getActivity(), CourseListActivity.class);
                ListCoursesList.putExtra("ADAPTER_MODE", "OPEN_COURSE");
                startActivity(ListCoursesList);
                CustomIntent.customType(getContext(),"fadein-to-fadeout");
                getActivity().finish();
            }
        });

        return view;
    }


    //добавляем значения
    private void setInitialData() {
        try {
            dbHelper = new DBHelper(this.getContext());
            courses = dbHelper.getAllCourses();
            lessons = dbHelper.getAllLessons();
        }
        catch (Exception e){
            e.printStackTrace();
        }
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
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        RecyclerView CoursesList = (RecyclerView) view.findViewById(R.id.list);
        CoursesList.setVisibility(View.INVISIBLE);
        Snackbar snackbar = Snackbar.make(view, "Hello Android", Snackbar.LENGTH_LONG);
        snackbar.show();
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            CircleImageView profileImage =  getActivity().findViewById(R.id.ProfileImage);
            profileImage.setImageResource(R.drawable.no_avatar);
            selectedImageUri = data.getData();
            Context c = getContext();
            Bitmap bitmap;
            //Сохраняем изображение в файл
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), selectedImageUri);
                new fileFromBitmap("ProfileFoto", bitmap, c).execute();


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
                profile = getActivity().getSharedPreferences(APP_PREFERENCES_Path,Context.MODE_PRIVATE);
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
                progressText.setText("Выполнено : " + 100 + "/100");
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            simpleProgressBar.setVisibility(View.INVISIBLE);

            progressText.setVisibility(View.INVISIBLE);
            CircleImageView profileImage = view.findViewById(R.id.ProfileImage);
            Glide.with(getContext()).load(selectedImageUri).into(profileImage);

        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            if(flag){
            progressText.setVisibility(View.VISIBLE);
            simpleProgressBar.setVisibility(View.VISIBLE);}
            progressText.setText("Выполнено : " + values[0] + "/100");
            progressText.clearComposingText();
        }
    }
    public void setProfileImage(){
        SharedPreferences accountPhoto = getActivity().getSharedPreferences(APP_PREFERENCES_Path, Context.MODE_PRIVATE);
        FilePath= accountPhoto.getString("key1", "");
        File Photo = new File(FilePath);
        if (Photo.exists()) {
            try {
                CircleImageView profileImage = view.findViewById(R.id.ProfileImage);
                Glide.with(this).load(Photo).signature(new ObjectKey(String.valueOf(System.currentTimeMillis()))).into(profileImage);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}


