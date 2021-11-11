package com.example.androidassignments;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.Xml;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import org.xmlpull.v1.XmlPullParser;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class WeatherForecast extends AppCompatActivity {

    private final String ACTIVITY_NAME = "WeatherForecastActivity";
    private final String baseUrl = "https://api.openweathermap.org/data/2.5/weather?q=";

    ProgressBar weatherApiProgressBar;
    ImageView weatherImageView;
    TextView currentTempView, maxTempView, minTempView;
    Spinner cityListSpinner;

    private class ForecastQuery extends AsyncTask<String, Integer, String> {
        private String currentTemp;
        private String maxTemp;
        private String minTemp;
        private Bitmap weatherImg;

        @Override
        protected String doInBackground(String... urls) {
            try {
                InputStream in = downloadUrl(urls[0]);
                try {
                    XmlPullParser parser = Xml.newPullParser();
                    parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
                    parser.setInput(in, null);

                    int type;
                    // While you're not at the end of the document:
                    while((type = parser.getEventType()) != XmlPullParser.END_DOCUMENT) {
                        // Are you currently at a Start Tag?
                        if(parser.getEventType() == XmlPullParser.START_TAG) {
                            if (parser.getName().equals("temperature")) {
                                currentTemp = parser.getAttributeValue(null, "value");
                                publishProgress(25);
                                minTemp = parser.getAttributeValue(null, "min");
                                publishProgress(50);
                                maxTemp = parser.getAttributeValue(null, "max");
                                publishProgress(75);
                            }
                            else if (parser.getName().equals("weather")) {
                                String iconName = parser.getAttributeValue(null, "icon");
                                String fileName = iconName + ".png";

                                Log.i(ACTIVITY_NAME, "Looking for file: " + fileName);
                                if (fileExistence(fileName)) {
                                    FileInputStream fis = null;
                                    try {
                                        fis = openFileInput(fileName);
                                    } catch (FileNotFoundException e) {
                                        e.printStackTrace();
                                    }
                                    Log.i(ACTIVITY_NAME,"Found the file locally");
                                    weatherImg = BitmapFactory.decodeStream(fis);
                                }
                                else {
                                    Log.i(ACTIVITY_NAME,"File not found locally, need to download file");
                                    String iconUrl = "https://openweathermap.org/img/w/" + fileName;
                                    weatherImg = getImage(new URL(iconUrl));

                                    FileOutputStream outputStream = openFileOutput(fileName, Context.MODE_PRIVATE);

                                    weatherImg.compress(Bitmap.CompressFormat.PNG, 80, outputStream);
                                    Log.i(ACTIVITY_NAME,"Downloaded the file from the Internet");
                                    outputStream.flush();
                                    outputStream.close();
                                }
                                publishProgress(100);
                            }
                        }
                        // Go to the next XML event
                        parser.next();
                    }
                } finally {
                    in.close();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return "";
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            weatherApiProgressBar.setVisibility(View.VISIBLE);
            weatherApiProgressBar.setProgress(values[0]);
        }

        @Override
        protected void onPostExecute(String a) {
            currentTempView.setText("Current: " + currentTemp + " \u2103");
            maxTempView.setText("Max: " + maxTemp + " \u2103");
            minTempView.setText("Min: " + minTemp + " \u2103");
            weatherImageView.setImageBitmap(weatherImg);
            weatherApiProgressBar.setVisibility(View.INVISIBLE);
        }

        private boolean fileExistence(String fileName){
            File file = getBaseContext().getFileStreamPath(fileName);
            return file.exists();
        }

        private Bitmap getImage(URL url) {
            HttpsURLConnection connection = null;
            try {
                connection = (HttpsURLConnection) url.openConnection();
                connection.connect();
                int responseCode = connection.getResponseCode();
                if (responseCode == 200) {
                    return BitmapFactory.decodeStream(connection.getInputStream());
                } else
                    return null;
            } catch (Exception e) {
                return null;
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
        }

        // Given a string representation of a URL, sets up a connection and gets
        // an input stream.
        private InputStream downloadUrl(String urlString) throws IOException {
            URL url = new URL(urlString);
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            // Starts the query
            conn.connect();
            return conn.getInputStream();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather_forecast);

        weatherImageView = (ImageView) findViewById(R.id.weather_image_view);
        currentTempView = (TextView) findViewById(R.id.current_temp_field);
        maxTempView = (TextView) findViewById(R.id.max_temp_field);
        minTempView = (TextView) findViewById(R.id.min_temp_field);

        cityListSpinner = (Spinner) findViewById(R.id.city_list_spinner);

        weatherApiProgressBar = (ProgressBar) findViewById(R.id.weather_api_progress);
        weatherApiProgressBar.setVisibility(View.VISIBLE);

        cityListSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                Log.i(ACTIVITY_NAME, "onSpinnerItemSelected()::" + parent.getItemAtPosition(pos));
                new ForecastQuery().execute(baseUrl + parent.getItemAtPosition(pos) + ",ca&APPID=79cecf493cb6e52d25bb7b7050ff723c&mode=xml&units=metric");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Another interface callback
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setResult(Activity.RESULT_CANCELED);
        finish();
    }
}