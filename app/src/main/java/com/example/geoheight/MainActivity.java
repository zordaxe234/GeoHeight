package com.example.geoheight;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    LocationManager locationManager;
    LocationListener locationListener;
    SensorManager sensorManager;
    private LinearLayout linearLayout;

    float currentPressure = Float.NaN;
    DataStorage dataStorage = new DataStorage();


    /**
     * creates a border background for layouts
     *
     * @return a GradientDrawable that looks like a border. use "cell".setBackground(drawable)
     */
    private GradientDrawable createBorder() {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setShape(GradientDrawable.RECTANGLE);
        drawable.setStroke(5, Color.DKGRAY);
        drawable.setCornerRadius(8);
        drawable.setColor(Color.LTGRAY);
        return drawable;
    }

    private LinearLayout createVerticalLayout() {
        LinearLayout linearLayout = new LinearLayout(MainActivity.this);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        linearLayout.setLayoutParams(params);
        linearLayout.setPadding(25, 10, 10, 10);
        params.setMargins(10, 10, 10, 10);
        return linearLayout;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        ViewGroup viewGroup = findViewById(R.id.layout);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        linearLayout = createVerticalLayout();
        linearLayout.setBackground(createBorder());
        viewGroup.addView(linearLayout);
        Button button = new Button(this);
        button.setText("Save");
        button.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dataStorage.saveToFile(getApplicationContext());
                    }
                }
        );
        viewGroup.addView(button);

        initGPS();

    }

    private void initGPS() {
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{"android.permission.ACCESS_FINE_LOCATION"}, 1);
            return;
        }

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                if (!Float.isNaN(currentPressure)) { //if pressure has already been read, we start recording.
                    dataStorage.logData(location, currentPressure);
                    Toast.makeText(getApplicationContext(), "Altitude: " + location.getAltitude()
                            + "\n" + "Pressure: " + currentPressure, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "pressure not read yet", Toast.LENGTH_SHORT).show();

                }


            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };

    }

    @Override
    protected void onResume() {
        super.onResume();

        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        }

        sensorManager.registerListener((SensorEventListener) this, sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE), SensorManager.SENSOR_DELAY_NORMAL);


    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float value = event.values[0];
        currentPressure = value;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

}
