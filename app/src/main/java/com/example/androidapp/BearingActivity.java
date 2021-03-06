package com.example.androidapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceManager;

import java.text.DecimalFormat;

public class BearingActivity extends Activity implements SensorEventListener {

    private LocationManager locationManager;
    private LocationListener locationListener;
    private static final int ACCESS_REQUEST_LOCATION = 0;
    private double latitude;
    private double longitude;
    private double targetLatitude;
    private double targetLongitude;
    private String bearingLocation;
    int bearing = 0;
    double distance;

    private static DecimalFormat df2 = new DecimalFormat("#.##");

    ImageView compassImg;
    ImageView bearingIndicator;
    TextView locationTxt;
    TextView compassTxt;
    TextView bearingMarkText;
    TextView distanceMarkText;
    int mAzimuth;
    private SensorManager mSensorManager;
    private Sensor mRotationV, mAccelerometer, mMagnetometer;
    boolean haveSensor = false, haveSensor2 = false;
    float[] rMat = new float[9];
    float[] orientation = new float[3];
    private float[] mLastAccelerometer = new float[3];
    private float[] mLastMagnetometer = new float[3];
    private boolean mLastAccelerometerSet = false;
    private boolean mLastMagnetometerSet = false;

    boolean bearingIndicatorEnabled = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences themePreferences = PreferenceManager.getDefaultSharedPreferences(this);
        if(themePreferences.getBoolean("Dark Mode", false)) {
            setTheme(R.style.DarkTheme);
        } else {
            setTheme(R.style.AppTheme);
        }
        setContentView(R.layout.bearing_activity);

        locationTxt = findViewById(R.id.locationTxt);
        bearingIndicator = findViewById(R.id.bearingIndicator);
        bearingMarkText = findViewById(R.id.bearingMarker);
        distanceMarkText = findViewById(R.id.distanceMarker);

        //NAVIGATION BUTTONS
        Button homeNav = findViewById(R.id.homeButton);
        homeNav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainActivity = new Intent(BearingActivity.this, MainActivity.class);
                startActivity(mainActivity);
                Log.i("Switch Activity", "Opened Main Activity");
            }
        });

        Button weatherNav = findViewById(R.id.weatherButton);
        weatherNav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent weatherActivity = new Intent(BearingActivity.this, WeatherActivity.class);
                startActivity(weatherActivity);
                Log.i("Switch Activity", "Opened Weather Activity");
            }
        });

        Button settingsNav = findViewById(R.id.settingsButton);
        settingsNav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent settingActivity = new Intent(BearingActivity.this, SettingsActivity.class);
                startActivity(settingActivity);
                Log.i("Switch Activity", "Opened Settings Activity");
            }
        });

        Button mapNav = findViewById(R.id.mapButton);
        mapNav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mapActivity = new Intent(BearingActivity.this, MapActivity.class);
                startActivity(mapActivity);
                Log.i("Switch Activity", "Opened Settings Activity");
            }
        });

        //SET UP FOR SPINNER
        final Spinner bearingSpinner = findViewById(R.id.bearingSpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.bearingLocations, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        bearingSpinner.setAdapter(adapter);

        bearingSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                bearingLocation = bearingSpinner.getSelectedItem().toString();
                if(bearingLocation.equals("none")){
                    bearingIndicatorEnabled = false;
                    bearingIndicator.setVisibility(View.INVISIBLE);
                    bearingMarkText.setVisibility(View.GONE);
                    distanceMarkText.setVisibility(View.GONE);
                } else {
                    bearingIndicatorEnabled = true;
                    bearingIndicator.setVisibility(View.VISIBLE);
                    bearingMarkText.setVisibility(View.VISIBLE);
                    distanceMarkText.setVisibility(View.VISIBLE);

                    if(bearingIndicatorEnabled) {
                        if (bearingLocation.equals("Oban")) {
                            targetLatitude = 56.412;
                            targetLongitude = -5.472;
                        } else if (bearingLocation.equals("Tobermory")){
                            targetLatitude = 56.62;
                            targetLongitude = -6.07;
                        } else if(bearingLocation.equals("Iona")){
                            targetLatitude = 56.33;
                            targetLongitude = -6.42;
                        } else if(bearingLocation.equals("Staffa")){
                            targetLatitude = 56.43;
                            targetLongitude = -6.33;
                        }
                        bearing = getBearing(latitude, longitude, targetLatitude, targetLongitude);
                        distance = Math.round(getDistance(latitude, longitude, targetLatitude, targetLongitude));
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                bearingIndicatorEnabled = false;
            }
        });

        //ALL LOCATION SET UP
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                if(location != null) {
                    //ASSIGN LATITUDE AND LONGITUDE
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();

                    if(bearingIndicatorEnabled) {
                        if (bearingLocation.equals("Oban")) {
                            targetLatitude = 56.412;
                            targetLongitude = -5.472;
                        } else if (bearingLocation.equals("Tobermory")){
                            targetLatitude = 56.62;
                            targetLongitude = -6.07;
                        } else if(bearingLocation.equals("Iona")){
                            targetLatitude = 56.33;
                            targetLongitude = -6.42;
                        } else if(bearingLocation.equals("Staffa")){
                            targetLatitude = 56.43;
                            targetLongitude = -6.33;
                        }
                        bearing = getBearing(latitude, longitude, targetLatitude, targetLongitude);
                        distance = Math.round(getDistance(latitude, longitude, targetLatitude, targetLongitude));
                        bearingIndicator.setRotation(bearing - mAzimuth);
                    }
                }
            }

            //NOT USED
            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {}
            @Override
            public void onProviderEnabled(String provider) {}
            @Override
            public void onProviderDisabled(String provider) {}
        };

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            Log.e("No location permission", "Requesting permission");
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, ACCESS_REQUEST_LOCATION);
        } else {
            setLocationUpdateFunction();
        }

        //SET UP FOR COMPASS IMAGE AND BEARING TEXT
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        compassImg = findViewById(R.id.compass);
        compassTxt = findViewById(R.id.heading);

        start();
    }

    public int getBearing(double latx, double longx, double laty, double longy){
        double x = Math.sin(longy - longx) * Math.cos(laty);
        double y = Math.cos(latx) * Math.sin(laty) - Math.sin(latx) * Math.cos(laty) * Math.cos(longy - longx);
        return (int) ((Math.toDegrees(Math.atan2(x, y))) + 360) % 360;
    }

    public double getDistance(double latx, double longx, double laty, double longy){
        return Math.acos(Math.sin(Math.toRadians(latx))*Math.sin(Math.toRadians(laty)) + Math.cos(Math.toRadians(latx))*Math.cos(Math.toRadians(laty))*Math.cos(Math.toRadians(longy)-Math.toRadians(longx))) * 6371e3;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case ACCESS_REQUEST_LOCATION: {
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    setLocationUpdateFunction();
                } else {
                    Log.e("No Location", "Location permission denied");
                }
                return;
            }
        }
    }

    // The minimum distance to change Updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters
    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 1000; // 1 second
    //    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1; // 1 minute


    @SuppressLint("MissingPermission")
    private void setLocationUpdateFunction(){
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, locationListener);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if(event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR){
            SensorManager.getRotationMatrixFromVector(rMat, event.values);
            mAzimuth = (int) (Math.toDegrees(SensorManager.getOrientation(rMat, orientation)[0]) + 360) % 360;
        }

        if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER){
            System.arraycopy(event.values, 0, mLastAccelerometer, 0, event.values.length);
            mLastAccelerometerSet = true;
        } else if(event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD){
            System.arraycopy(event.values, 0, mLastMagnetometer, 0, event.values.length);
            mLastMagnetometerSet = true;
        }

        if(mLastAccelerometerSet && mLastMagnetometerSet){
            SensorManager.getRotationMatrix(rMat, null, mLastAccelerometer, mLastMagnetometer);
            SensorManager.getOrientation(rMat, orientation);
            mAzimuth = (int) (Math.toDegrees(SensorManager.getOrientation(rMat, orientation)[0]) + 360) % 360;
        }

        mAzimuth = Math.round(mAzimuth);

        compassImg.setRotation(-mAzimuth);
        bearingIndicator.setRotation(bearing - mAzimuth);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String distanceUnit = preferences.getString("Distance units", "Nautical miles");

        //CODE FROM HERE USED FOR BEARING INDICATOR
        if(bearingIndicatorEnabled) {
            locationTxt.setText("Lat: " + df2.format(latitude) + ". Long: " + df2.format(longitude));
            compassTxt.setText(mAzimuth + "°");
            bearingMarkText.setText("Bearing to target: " + bearing + "°");
            if(distanceUnit.equals("Nautical miles")){
                distanceMarkText.setText("Distance to target: "  + df2.format((distance/1000)/1.852) + "nm");
            } else if(distanceUnit.equals("Imperial")){
                distanceMarkText.setText("Distance to target: "  + df2.format((distance/1000)/1.609) + "mi");
            } else {
                distanceMarkText.setText("Distance to target: "  + df2.format(distance/1000) + "km");
            }
        } else {
            locationTxt.setText("Lat: " + df2.format(latitude) + ". Long: " + df2.format(longitude));
            compassTxt.setText(mAzimuth + "°");
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        //NOT USED
    }

    public void start() {
        if(mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR) == null){
            if((mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) == null) || (mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD) == null)){
                noSensorsAlert();
            } else{
                mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
                mMagnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
                haveSensor = mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_UI);
                haveSensor2 = mSensorManager.registerListener(this, mMagnetometer, SensorManager.SENSOR_DELAY_UI);
            }
        } else{
            mRotationV = mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
            haveSensor = mSensorManager.registerListener(this, mRotationV, SensorManager.SENSOR_DELAY_UI);
        }
    }

    public void noSensorsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setMessage("Your device does not support the compass.")
            .setCancelable(false)
            .setNegativeButton("close", new DialogInterface.OnClickListener(){
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
        });
        alertDialog.show();
    }

    public void stop() {
        if(haveSensor) {
            mSensorManager.unregisterListener(this, mRotationV);
        } else {
            mSensorManager.unregisterListener(this, mAccelerometer);
            mSensorManager.unregisterListener(this, mMagnetometer);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        stop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        start();
    }
}
