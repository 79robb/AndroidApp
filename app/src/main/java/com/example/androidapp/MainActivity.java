package com.example.androidapp;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences themePreferences = PreferenceManager.getDefaultSharedPreferences(this);
        if(themePreferences.getBoolean("Dark Mode", false)) {
            setTheme(R.style.DarkTheme);
        } else {
            setTheme(R.style.AppTheme);
        }
        setContentView(R.layout.activity_main);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        //Navigation buttons

        Button weatherNav = findViewById(R.id.weatherButton);
        weatherNav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent weatherActivity = new Intent(MainActivity.this, WeatherActivity.class);
                startActivity(weatherActivity);
                Log.i("Switch Activity", "Opened Weather Activity");
            }
        });

        Button bearingNav = findViewById(R.id.bearingButton);
        bearingNav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent bearingActivity = new Intent(MainActivity.this, BearingActivity.class);
                startActivity(bearingActivity);
                Log.i("Switch Activity", "Opened Bearing Activity");
            }
        });

        Button settingsNav = findViewById(R.id.settingsButton);
        settingsNav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent settingActivity = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(settingActivity);
                Log.i("Switch Activity", "Opened Settings Activity");
            }
        });

        Button mapNav = findViewById(R.id.mapButton);
        mapNav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mapActivity = new Intent(MainActivity.this, MapActivity.class);
                startActivity(mapActivity);
                Log.i("Switch Activity", "Opened Settings Activity");
            }
        });

        //Spinner which contains all locations

        final Spinner locationSpinner = findViewById(R.id.locationSpinnerID);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.mainLocations, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        locationSpinner.setAdapter(adapter);

        //Listener which changes the info text when a new location is selected
        locationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            TextView locationInfo = findViewById(R.id.locationInfoText);
            ImageView mullImage = findViewById(R.id.mullPic);

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String locationChooser = locationSpinner.getSelectedItem().toString();

                //switch case for different locations, displays an image and text depending on what it selected

                switch (locationChooser){
                    case "Tobermory":
                        locationInfo.setText(R.string.tobermory_info);
                        mullImage.setImageResource(R.drawable.mulltob);
                        break;
                    case "Salen":
                        locationInfo.setText(R.string.salen_info);
                        mullImage.setImageResource(R.drawable.mullsalen);
                        break;
                    case "Craignure":
                        locationInfo.setText(R.string.craignure_info);
                        mullImage.setImageResource(R.drawable.mullcraignure);
                        break;
                    case "Iona":
                        locationInfo.setText(R.string.iona_info);
                        mullImage.setImageResource(R.drawable.mulliona);
                        break;
                    case "Staffa":
                        locationInfo.setText(R.string.staffa_info);
                        mullImage.setImageResource(R.drawable.mullstaffa);
                        break;
                    case "Calgary Bay":
                        locationInfo.setText(R.string.calgary_info);
                        mullImage.setImageResource(R.drawable.mullcalgary);
                        break;
                }
                Log.i("Change location", locationChooser + " selected");
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                locationInfo.setText("Select a location above to learn more...");
                mullImage.setImageResource(R.drawable.mull);
            }
        });
    }
}
