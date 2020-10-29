package com.example.firestoredatabase;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.Calendar;

public class CalendarActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        if (ContextCompat.checkSelfPermission(CalendarActivity.this,
                Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) CalendarActivity.this,
                    new String[]{Manifest.permission.WRITE_CALENDAR},
                    1);
        } else {
            Calendar beginTime = Calendar.getInstance();
            Calendar endTime = beginTime;
            //beginTime.add(Calendar.DAY_OF_MONTH, -1);
            int year = beginTime.get(Calendar.YEAR);
            int month = beginTime.get(Calendar.MONTH);
            int day = beginTime.get(Calendar.DAY_OF_MONTH);
            //Toast.makeText(getContext(), String.valueOf(year) + String.valueOf(month) + String.valueOf(day), Toast.LENGTH_LONG).show();
            beginTime.set(year, month, day, 12, 53);
            endTime.set(year, month, day, 12, 53);
            Toast.makeText(CalendarActivity.this, beginTime.toString() + endTime.toString(), Toast.LENGTH_LONG).show();

            ContentValues l_event = new ContentValues();
            l_event.put("calendar_id", 1);
            l_event.put("title", "ForLife - Rezervare " + Common.simpleFormatDate.format(beginTime.getTime()));
            l_event.put("description", "Rezervare mâine de la ora " + Common.converTimeSlotToString(Common.currentTimeSlot));
            l_event.put("eventLocation", "Romania");
            l_event.put("dtstart", beginTime.getTimeInMillis());
            l_event.put("dtend", endTime.getTimeInMillis());
            l_event.put("hasAlarm", 1);
            l_event.put("allDay", 0);
            // status: 0~ tentative; 1~ confirmed; 2~ canceled
            // l_event.put("eventStatus", 1);

            l_event.put("eventTimezone", "Romania");
            Uri l_eventUri;
            if (Build.VERSION.SDK_INT >= 8) {
                l_eventUri = Uri.parse("content://com.android.calendar/events");

                Toast.makeText(CalendarActivity.this, "Eveniment adăugat în calendar.", Toast.LENGTH_SHORT).show();
            } else {
                l_eventUri = Uri.parse("content://calendar/events");
                Toast.makeText(CalendarActivity.this, "Evenimentul nu a fost adăugat în calendar.", Toast.LENGTH_SHORT).show();
            }
            Uri l_uri = CalendarActivity.this.getContentResolver()
                    .insert(l_eventUri, l_event);
            if(l_uri != null){
                long eventID = Long.parseLong(l_uri.getLastPathSegment());

                ContentValues reminderValues = new ContentValues();
                reminderValues.put("event_id", eventID);
                reminderValues.put("method", 1);// will alert the user with a reminder notification
                reminderValues.put("minutes", 0);// number of minutes before the start time of the event to fire a reminder

                Uri reminder_eventUri;
                if (Build.VERSION.SDK_INT >= 8) {
                    reminder_eventUri = Uri.parse("content://com.android.calendar/reminders");
                } else {
                    reminder_eventUri = Uri.parse("content://calendar/reminders");
                }
                Uri r_uri = CalendarActivity.this.getContentResolver().insert(reminder_eventUri, reminderValues);
                if(r_uri != null)
                {
                    long reminderID = Long.parseLong(r_uri.getLastPathSegment());
                    Toast.makeText(getApplicationContext(), "Event Created Successfully", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Calendar beginTime = Calendar.getInstance();
                    Calendar endTime = beginTime;
                    //beginTime.add(Calendar.DAY_OF_MONTH, -1);
                    int year = beginTime.get(Calendar.YEAR);
                    int month = beginTime.get(Calendar.MONTH);
                    int day = beginTime.get(Calendar.DAY_OF_MONTH);
                    //Toast.makeText(getContext(), String.valueOf(year) + String.valueOf(month) + String.valueOf(day), Toast.LENGTH_LONG).show();
                    beginTime.set(year, month, day, 21, 50);
                    endTime.set(year, month, day, 21, 50);
                    Toast.makeText(CalendarActivity.this, beginTime.toString() + endTime.toString(), Toast.LENGTH_LONG).show();
                    ContentValues l_event = new ContentValues();
                    l_event.put("calendar_id", 1);
                    l_event.put("title", "ForLife - Rezervare " + Common.simpleFormatDate.format(beginTime.getTime()));
                    l_event.put("description", "Rezervare mâine de la ora " + Common.converTimeSlotToString(Common.currentTimeSlot));
                    l_event.put("eventLocation", "Romania");
                    l_event.put("dtstart", beginTime.getTimeInMillis());
                    l_event.put("dtend", endTime.getTimeInMillis());
                    l_event.put("hasAlarm", 1);
                    l_event.put("allDay", 0);

                    l_event.put("eventTimezone", "Romania");
                    Uri l_eventUri;
                    if (Build.VERSION.SDK_INT >= 8) {
                        l_eventUri = Uri.parse("content://com.android.calendar/events");
                        Toast.makeText(CalendarActivity.this, "Eveniment adăugat în calendar.", Toast.LENGTH_SHORT).show();
                    } else {
                        l_eventUri = Uri.parse("content://calendar/events");
                        Toast.makeText(CalendarActivity.this, "Evenimentul nu a fost adăugat în calendar.", Toast.LENGTH_SHORT).show();
                    }
                    Uri l_uri = CalendarActivity.this.getContentResolver()
                            .insert(l_eventUri, l_event);

                } else {

                    Toast.makeText(CalendarActivity.this, "Nu s-a permis accesul pentru calendar!", Toast.LENGTH_SHORT).show();
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
        }
    }
}
