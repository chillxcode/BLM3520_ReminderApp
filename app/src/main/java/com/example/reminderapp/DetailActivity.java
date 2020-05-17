package com.example.reminderapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.media.AudioAttributes;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;

import java.util.Calendar;

public class DetailActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private TextView startTimeDayTextView;
    private TextView startTimeHourTextView;
    private TextView endTimeDayTextView;
    private TextView endTimeHourTextView;
    private TextView locationTextView;

    private Spinner frequencySpinner;

    int PLACE_PICKER_REQUEST = 1;
    Calendar calendar;
    DatePickerDialog datePickerDialog;
    TimePickerDialog timePickerDialog;
    String[] frequencyArray = {"Day", "Week", "Month", "Year"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        createNotificationChannel("Emre's Birthday");
        setupView();

    }

    private void setupView() {
        calendar = Calendar.getInstance();

        startTimeDayTextView = findViewById(R.id.startTimeDayTextView);
        startTimeHourTextView = findViewById(R.id.startTimeHourTextView);
        endTimeDayTextView = findViewById(R.id.endTimeDayTextView);
        endTimeHourTextView = findViewById(R.id.endTimeHourTextView);
        locationTextView = findViewById(R.id.locationTextView);

        // Spinner
        frequencySpinner = findViewById(R.id.frequencySpinner);
        frequencySpinner.setOnItemSelectedListener(this);
        ArrayAdapter adapter = new ArrayAdapter(this,android.R.layout.simple_spinner_item, frequencyArray);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        frequencySpinner.setAdapter(adapter);
    }

    public void startTimeDayClick(View view) {
        datePickerDialog = new DatePickerDialog(DetailActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                startTimeDayTextView.setText(dayOfMonth + "/" + month + "/" + year);
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    public void startTimeHourClick(View view) {
        timePickerDialog = new TimePickerDialog(DetailActivity.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                startTimeHourTextView.setText(hourOfDay + ":" + minute);
            }
        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);
        timePickerDialog.show();
    }

    public void endTimeDayClick(View view) {
        datePickerDialog = new DatePickerDialog(DetailActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                endTimeDayTextView.setText(dayOfMonth + "/" + month + "/" + year);
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    public void endTimeHourClick(View view) {
        timePickerDialog = new TimePickerDialog(DetailActivity.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                endTimeHourTextView.setText(hourOfDay + ":" + minute);
            }
        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);
        timePickerDialog.show();
    }

    public void selectLocationClicked(View view) {
        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
        try {
            startActivityForResult(builder.build(DetailActivity.this), PLACE_PICKER_REQUEST);
        } catch (GooglePlayServicesRepairableException e) {
            e.printStackTrace();
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
    }

    public void addUpdateClicked(View view) {
        Intent intent = new Intent(DetailActivity.this, ReminderBroadcast.class);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(DetailActivity.this, 0, intent, 0);

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
                System.currentTimeMillis() + 5000,
                1,
                pendingIntent);
    }

    private void createNotificationChannel(String title){

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = title;
            String description = "Description";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("notify", name, importance);
            channel.setDescription(description);

            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .build();

            channel.setSound(Uri.parse("android.resource://" + "com.example.reminderapp" + "/" + R.raw.swiftly), audioAttributes);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);

        }

    }

    // Google Maps
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(this, data);

                String latitude = String.valueOf(place.getLatLng().latitude);
                String longitude = String.valueOf(place.getLatLng().longitude);

                locationTextView.setText(latitude + " , " + longitude);
            }
        }
    }

    // Frequency Adapter Funcs
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (parent.getId() == R.id.frequencySpinner) {
            String spinnerValue = parent.getItemAtPosition(position).toString();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    // DateTime Things
    private int daySeconds() {
        return 1000 * 60 * 60 * 24;
    }

    private int weekSeconds() {
        return 1000 * 60 * 60 * 24 * 7;
    }

    private int monthSeconds() {
        return 1000 * 60 * 60 * 24 * 30;
    }

    private int yearSeconds() {
        return 1000 * 60 * 60 * 24 * 365;
    }
}
