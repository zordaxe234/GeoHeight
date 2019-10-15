package com.example.geoheight;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.provider.ContactsContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

public class DataStorage {
    private List<Location> locationList = new ArrayList<>();    //for location
    private List<Float> sensorReadingList = new ArrayList<>();  //for pressure
    private List<Long> timeList = new ArrayList<>();            //timestamp

    public void logData(Location location, Float pressure){
        locationList.add(location);
        sensorReadingList.add(pressure);
        timeList.add(System.currentTimeMillis());
    }

    public void saveToFile(Context context){
        File file = new File(context.getFilesDir(), "data.json");
        try {
            FileWriter fileWriter = new FileWriter(file.getAbsolutePath());
            JSONArray jsonArray = new JSONArray();
            for (int i = 0; i < locationList.size(); i++ ) {
                JSONObject singleReading = new JSONObject();
                try {
                    singleReading.put("Longitude", locationList.get(i).getLongitude());
                    singleReading.put("Latitude",locationList.get(i).getLatitude());
                    singleReading.put("Altitude",locationList.get(i).getAltitude());
                    singleReading.put("Pressure", sensorReadingList.get(i));
                    singleReading.put("Time", timeList.get(i));

                    jsonArray.put(singleReading);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
            fileWriter.write(jsonArray.toString());
            fileWriter.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void clearData(){

    }
}
