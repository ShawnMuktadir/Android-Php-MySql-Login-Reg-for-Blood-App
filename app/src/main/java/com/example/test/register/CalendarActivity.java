package com.example.test.register;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.CalendarView;

import com.example.test.R;

public class CalendarActivity extends AppCompatActivity {
    private static final String TAG = "CalendarActivity";
    private CalendarView calendarView;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.calendar_layout);

        calendarView = (CalendarView)findViewById(R.id.calendarView);

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                String date = (month + 1) +"/"+ dayOfMonth +"/" + year;
                Log.d(TAG,"onSelectedDayChange: mm/dd/yy "+date);

                Intent intent = new Intent(CalendarActivity.this,RegistrationActivity.class);
                intent.putExtra("date",date);
                startActivity(intent);
            }
        });
    }
}
