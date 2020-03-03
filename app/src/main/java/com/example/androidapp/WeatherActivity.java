package com.example.androidapp;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.DecimalFormat;

public class WeatherActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weather_activity);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);

        TextView weatherInfo = findViewById(R.id.weatherInfo);
        weatherInfo.setTextColor(Color.WHITE);
        weatherInfo.setText(R.string.weather_info);

        Button homeNav = findViewById(R.id.homeButton);
        homeNav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainActivity = new Intent(WeatherActivity.this, MainActivity.class);
                startActivity(mainActivity);
                Log.i("Switch Activity", "Opened Main Activity");
            }
        });

        final TableLayout weatherTable = findViewById(R.id.weatherTable);

        Button load24 = findViewById(R.id.load24);
        load24.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int rowCount = weatherTable.getChildCount();
                for(int i=1; i<rowCount; i++){
                    weatherTable.removeViewAt(1);
                }
                createWeather(8, 1);
            }
        });

        Button load72 = findViewById(R.id.load72);
        load72.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int rowCount = weatherTable.getChildCount();
                for(int i=1; i<rowCount; i++){
                    weatherTable.removeViewAt(1);
                }
                createWeather(24, 2);
            }
        });
    }

    private void createWeather(int time, int space){
        try {
            JSONObject weatherjson = readJsonFromUrl("https://api.openweathermap.org/data/2.5/forecast?id=2641108&appid=85ce4dba63ea014d404782678b49dc33");
            if(weatherjson.getInt("cod") == 200) {
                try {
                    DecimalFormat df = new DecimalFormat("0.00");

                    TableLayout weatherTable = findViewById(R.id.weatherTable);

                    String jsonString = weatherjson.getString("list");
                    JSONArray weatherData = new JSONArray(jsonString);
                    for(int i=0; i<time; i=i+space) {
                        JSONObject weatherPrimary = weatherData.getJSONObject(i);
                        JSONObject weatherMain = weatherPrimary.getJSONObject("main");
                        JSONArray weatherSecondaryTemporary = weatherPrimary.getJSONArray("weather");
                        JSONObject weatherSecondary = weatherSecondaryTemporary.getJSONObject(0);
                        JSONObject weatherWind = weatherPrimary.getJSONObject("wind");
                        String weatherTime = weatherPrimary.getString("dt_txt");
                        JSONObject weatherRain;
                        if(weatherSecondary.getString("main").equals("Rain"))
                        {
                            weatherRain = weatherPrimary.getJSONObject("rain");
                        } else if(weatherSecondary.getString("main").equals("Snow"))
                        {
                            weatherRain = weatherPrimary.getJSONObject("snow");
                        } else
                        {
                            weatherRain = null;
                        }

                        TableRow weatherRow = new TableRow(getApplicationContext());

                        TextView dateText = new TextView(getApplicationContext());
                        TextView windSpeedText = new TextView(getApplicationContext());
                        TextView windDirText = new TextView(getApplicationContext());
                        TextView precipitationText = new TextView(getApplicationContext());
                        TextView tempText = new TextView(getApplicationContext());

                        dateText.setText(weatherTime);
                        double windSpeed = Double.parseDouble(weatherWind.getString("speed"));
                        windSpeedText.setText(df.format(windSpeed*1.944));
                        windDirText.setText(weatherWind.getString("deg"));
                        if(weatherRain != null) {
                            precipitationText.setText(weatherSecondary.getString("description") + ", " + weatherRain.getString("3h") + "mm");
                        } else {
                            precipitationText.setText("None");
                        }
                        String tempKelvin = weatherMain.getString("temp").substring(0, 3);
                        double tempTemp = Double.parseDouble(tempKelvin) - 273;
                        tempText.setText(Long.toString(Math.round(tempTemp)));

                        TableRow.LayoutParams lp = new TableRow.LayoutParams(
                                TableRow.LayoutParams.WRAP_CONTENT,
                                TableRow.LayoutParams.MATCH_PARENT,
                                0.2f
                        );

                        dateText.setLayoutParams(lp);
                        windSpeedText.setLayoutParams(lp);
                        windDirText.setLayoutParams(lp);
                        precipitationText.setLayoutParams(lp);
                        tempText.setLayoutParams(lp);

                        dateText.setTextColor(Color.WHITE);
                        windSpeedText.setTextColor(Color.WHITE);
                        windDirText.setTextColor(Color.WHITE);
                        precipitationText.setTextColor(Color.WHITE);
                        tempText.setTextColor(Color.WHITE);

                        weatherRow.addView(dateText);
                        weatherRow.addView(windSpeedText);
                        weatherRow.addView(windDirText);
                        weatherRow.addView(precipitationText);
                        weatherRow.addView(tempText);

                        weatherTable.addView(weatherRow);
                    }
                } catch(JSONException e) {
                    Log.e("Error converting data", e.toString());
                }
            } else {
                Log.e("Error connecting to API", weatherjson.getString("cod"));
            }
        } catch(IOException | JSONException e) {
            Log.e("Error getting weather", e.toString());
        }
    }

    private static String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }

    public static JSONObject readJsonFromUrl(String url) throws IOException, JSONException {
        InputStream is = new URL(url).openStream();
        try {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            String jsonText = readAll(rd);
            JSONObject json = new JSONObject(jsonText);
            return json;
        } finally {
            is.close();
        }
    }
}


