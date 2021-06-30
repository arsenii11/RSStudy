package com.example.finances.events;

import android.content.Context;
import android.content.Intent;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finances.R;
import com.example.finances.database.Event;
import com.example.finances.events.course.CourseActivity;
import com.example.finances.events.lesson.LessonActivity;
import com.example.finances.events.test.TestActivity;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Calendar;

import maes.tech.intentanim.CustomIntent;
import nz.co.trademe.covert.Covert;

public class MainAdaptor extends RecyclerView.Adapter<MainAdaptor.ViewHolder> {

    private final LayoutInflater inflater;
    private final Context context;
    private final ArrayList<Event> events;
    private final boolean dateShow; //Флаг показывать ли дату
    private final boolean swipeEnabled; //Флаг включены ли свайпы
    private final Covert covert;


    public MainAdaptor(Context context, ArrayList<Event> events, boolean dateShow, boolean swipeEnabled, Covert covert)  {
        this.inflater = LayoutInflater.from(context);
        this.context = context;
        this.events = events;
        this.dateShow = dateShow;
        this.swipeEnabled = swipeEnabled;
        this.covert = covert;
    }


    @NonNull
    @NotNull
    @Override
    public MainAdaptor.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.list_event_item, parent, false);
        return new MainAdaptor.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {

        if(swipeEnabled)
            covert.drawCornerFlag(holder);

        Event event = events.get(position); //Получаем текущий экземпляр события
        Event.EventType eventType = event.getEventType(); //Получаем тип события

        Calendar calendar = Calendar.getInstance(); //Создаем экземпляр календаря
        calendar.setTimeInMillis(event.getDate()*1000); //Устанавливаем в календарь дату и время начала события

        String name = event.getName().split(", ")[0]; //Создаем строку с названием события

        //Создаем строку со временем начала события
        String fromTo = DateUtils.formatDateTime(context,
                calendar.getTimeInMillis(),
                DateUtils.FORMAT_SHOW_TIME);

        //Создаем строку с датой начала события
        String date = "";
        if(dateShow) date = DateUtils.formatDateTime(context,
                calendar.getTimeInMillis(),
                DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_YEAR);

        //В зависимсоти от типа события меняем значения строк
        if(eventType == Event.EventType.Lesson) {
            name += " lesson"; //Оставляем только название курса и приписываем слово lesson в конец

            calendar.setTimeInMillis(calendar.getTimeInMillis() + (long) (event.getDuration()*3600000)); //Устанавливаем в календарь дату и время конца урока

            //Создаем строку со временем конца урока
            String to = DateUtils.formatDateTime(context,
                    calendar.getTimeInMillis(),
                    DateUtils.FORMAT_SHOW_TIME);

            fromTo += " – " + to; //Добавляем ко времени начала события время уонца
        }


        holder.nameView.setText(name); //Устанавливаем в View название события
        holder.eventId.setText(String.valueOf(event.getEventId())); //Устанавливаем в View id события
        holder.eventType.setText(eventType.name().toUpperCase()); //Устанавливаем в View тип события
        holder.duration.setText(fromTo); //Устанавливаем в View время начала и конца (для урока)
        holder.date.setText(date); //Устанавливаем в View дату начала события

        //Создаем функцию при нажатии на элемент списка
        holder.itemView.setOnClickListener(v -> {
            Intent intent; //Создаем новое намерение

            //В зависимости от типа события создаем намерение перехода на новую активность
            if(eventType == Event.EventType.Lesson){
                intent = new Intent(v.getContext(), LessonActivity.class); //Переход на активность с информацией об уроке
                intent.putExtra("LESSON_ID", event.getId()); //Передаем в намерение id урока
            }
            else {
                intent = new Intent(v.getContext(), TestActivity.class); //Переход на активность с информацией о тесте
                intent.putExtra("TEST_ID", event.getId()); //Предаем в намерение id теста
            }
            v.getContext().startActivity(intent); //Запускаем намерение
            CustomIntent.customType(v.getContext(),"left-to-right"); //Добавляем анимацию перехода
        });
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView nameView; //View с названием события
        final TextView eventId; //View с id события (скрыто от пользователя)
        final TextView eventType; //View с типом события (скрыто от пользователя)
        final TextView duration; //View со временем начала и конца (для урока)
        final TextView date; //View с датой начала события

        ViewHolder(View view){
            super(view);
            nameView = (TextView) view.findViewById(R.id.LessonItem);
            eventId = view.findViewById(R.id.EventID);
            eventType = view.findViewById(R.id.EventType);
            duration = view.findViewById(R.id.duration);
            date = view.findViewById(R.id.date);
        }
    }

}
