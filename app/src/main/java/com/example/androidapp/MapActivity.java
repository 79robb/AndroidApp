package com.example.androidapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;

import androidx.annotation.Nullable;

public class MapActivity extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_activity);

        Button homeNav = findViewById(R.id.homeButton);
        homeNav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainActivity = new Intent(MapActivity.this, MainActivity.class);
                startActivity(mainActivity);
                Log.i("Switch Activity", "Opened Main Activity");
            }
        });

        Button weatherNav = findViewById(R.id.weatherButton);
        weatherNav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent weatherActivity = new Intent(MapActivity.this, WeatherActivity.class);
                startActivity(weatherActivity);
                Log.i("Switch Activity", "Opened Weather Activity");
            }
        });

        Button bearingNav = findViewById(R.id.bearingButton);
        bearingNav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent bearingActivity = new Intent(MapActivity.this, BearingActivity.class);
                startActivity(bearingActivity);
                Log.i("Switch Activity", "Opened Bearing Activity");
            }
        });

        Button settingsNav = findViewById(R.id.settingsButton);
        settingsNav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent settingActivity = new Intent(MapActivity.this, SettingsActivity.class);
                startActivity(settingActivity);
                Log.i("Switch Activity", "Opened Settings Activity");
            }
        });

        WebView chartView = findViewById(R.id.chartView);
        chartView.getSettings().setJavaScriptEnabled(true);
        chartView.loadUrl("https://webapp.navionics.com/#boating@7&key=_ulwIf%60nd%40");
    }
}
