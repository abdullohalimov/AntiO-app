package com.example.testapp2;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.savvi.rangedatepicker.CalendarPickerView;

import java.sql.SQLInput;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Locale;


public class CalendarFragment extends Fragment {

    //lists for calendar
    Collection<Date> highlited = new ArrayList<>();
    Collection<Date> selected = new ArrayList<>();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_calendar2, container, false);
        CalendarPickerView calendar = view.findViewById(R.id.calendar_view);
        SQLiteDatabase db = getActivity().openOrCreateDatabase("days.db", Context.MODE_PRIVATE, null);
        db.execSQL("CREATE TABLE IF NOT EXISTS highlited(hdays INTEGER UNIQUE);");





        //get start of data vozderjaniya and current date to set on range picker and get all days between
        Calendar start = Calendar.getInstance();
        SharedPreferences spref = getActivity().getPreferences(Context.MODE_PRIVATE);
        long start2 = spref.getLong("Start date", 0);
        start.setTimeInMillis(start2);
        selected.add(start.getTime());
        selected.add(Calendar.getInstance().getTime());

        //dates for initialize calendar
        Calendar lastYear = Calendar.getInstance();
        lastYear.set(2010, 1, 1);
        Calendar nextYear = Calendar.getInstance();
        nextYear.set(2030, 1, 1);
        //on click listeners
        calendar.setOnDateSelectedListener(new CalendarPickerView.OnDateSelectedListener() {
            final Calendar cday = Calendar.getInstance();
            @Override
            public void onDateSelected(Date date){
                if(cday.getTimeInMillis() < date.getTime()){
                    Toast.makeText(getActivity(), "Будущее покрыто мраком", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onDateUnselected(Date date) {
                ContentValues cv = new ContentValues();
                cv.put("hdays", date.getTime());

                if(cday.getTimeInMillis() < date.getTime()){
                    Toast.makeText(getActivity(), "Будущее покрыто мраком", Toast.LENGTH_SHORT).show();
                }else {
                highlited.add(date);
                calendar.highlightDates(highlited);
                addToBase(db, cv);
            }}
        });


        // initialize range calendar for get all days in range
        calendar.init(lastYear.getTime(), nextYear.getTime()).inMode(CalendarPickerView.SelectionMode.RANGE)
        .withSelectedDates(selected);

        loadFromBase(db);

        //set all clear days
        Collection<Date> selected2;
        selected2 = calendar.getSelectedDates();
        calendar.init(lastYear.getTime(), nextYear.getTime(), new SimpleDateFormat("MMMM, yyyy", Locale.getDefault()))
                .inMode(CalendarPickerView.SelectionMode.MULTIPLE)
                .withSelectedDates(selected2)
                .withHighlightedDates(highlited);
        calendar.scrollToDate(new Date());


        return view;
    }

    private void addToBase(SQLiteDatabase db, ContentValues cv) {
        db.insert("highlited", null, cv);
    }

    private void loadFromBase(SQLiteDatabase db){
        Cursor cur;
        cur = db.rawQuery("SELECT * FROM highlited;", null);
        while (cur.moveToNext()){
            long day = cur.getLong(0);
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(day);
            highlited.add(cal.getTime());

        }
        cur.close();

    }


}