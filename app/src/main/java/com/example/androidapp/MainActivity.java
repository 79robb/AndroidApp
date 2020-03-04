package com.example.androidapp;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
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
        setContentView(R.layout.activity_main);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

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
                if(locationChooser.equals("Tobermory")){
                    locationInfo.setText(R.string.tobermory_info);
                    mullImage.setImageResource(R.drawable.mulltob);
                    Log.i("Change location", locationChooser + " selected");
                } else if(locationChooser.equals("Salen")){
                    locationInfo.setText(R.string.salen_info);
                    mullImage.setImageResource(R.drawable.mullsalen);
                    Log.i("Change location", locationChooser + " selected");
                }  else if(locationChooser.equals("Craignure")){
                    locationInfo.setText(R.string.craignure_info);
                    mullImage.setImageResource(R.drawable.mullcraignure);
                    Log.i("Change location", locationChooser + " selected");
                }  else if(locationChooser.equals("Iona")){
                    locationInfo.setText(R.string.iona_info);
                    mullImage.setImageResource(R.drawable.mulliona);
                    Log.i("Change location", locationChooser + " selected");
                }  else if(locationChooser.equals("Staffa")){
                    locationInfo.setText(R.string.staffa_info);
                    mullImage.setImageResource(R.drawable.mullstaffa);
                    Log.i("Change location", locationChooser + " selected");
                }  else if(locationChooser.equals("Calgary Bay")){
                    locationInfo.setText(R.string.calgary_info);
                    mullImage.setImageResource(R.drawable.mullcalgary);
                    Log.i("Change location", locationChooser + " selected");
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                locationInfo.setText("Select a location above to learn more...");
                mullImage.setImageResource(R.drawable.mull);
            }
        });
    }
}
