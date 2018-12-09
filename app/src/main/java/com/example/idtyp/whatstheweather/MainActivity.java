package com.example.idtyp.whatstheweather;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    EditText cityName;
    TextView cityDisplay;
    TextView resultTextView;


    public void findWeather(View view) {

        Log.i("City Name",cityName.getText().toString());

        InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);  // hides keyboard
        mgr.hideSoftInputFromWindow(cityName.getWindowToken(), 0);

        try {
            String encodedCityName = URLEncoder.encode(cityName.getText().toString(), "UTF-8");
            DownloadTask task = new DownloadTask();
            task.execute("http://api.openweathermap.org/data/2.5/weather?q="+encodedCityName+"&APPID=5c9d0c2a24367f21df7ad9e9725355e2").get();
            cityDisplay.setText(cityName.getText().toString());

        }
        catch (Exception e){
            Toast.makeText(getApplicationContext(),"Couldn't find Weather",Toast.LENGTH_LONG).show();
            resultTextView.setText("");
            e.printStackTrace();
        }



    }

    public class DownloadTask extends AsyncTask<String, Void, String>{

        @Override
        protected String doInBackground(String... urls ) {
            URL url;
            HttpURLConnection connection = null;
            String result = "";

            try {
                url = new URL(urls[0]);
                connection = (HttpURLConnection)url.openConnection();

                InputStream inputStream = connection.getInputStream();

                InputStreamReader reader = new InputStreamReader(inputStream);

                int data = reader.read();

                while(data != -1){
                    char current = (char) data;
                    result += current;
                    data = reader.read();
                }
                Log.i("Before Result passes:",result);
                return result;

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e){
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            try {
                JSONObject jsonObject = new JSONObject(result);

                String message = "";

                String weatherInfo = jsonObject.getString("weather");

                Log.i("Weather Content",weatherInfo);

                JSONArray arr = new JSONArray(weatherInfo);

                for(int i = 0;i<arr.length();i++){
                    JSONObject jsonPart = arr.getJSONObject(i);

                    String main = "";
                    String description = "";

                    main = jsonPart.getString("main");
                    description = jsonPart.getString("description");

                    Log.i("main",jsonPart.getString("main"));
                    Log.i("description",jsonPart.getString("description"));

                    if(main != "" && description != ""){

                        message += main +": " + description + "\r\n";

                    }

                    if(message != "")
                         resultTextView.setText(message);
                    else{
                        Toast.makeText(getApplicationContext(),"Couldn't find Weather",Toast.LENGTH_LONG).show();
                        resultTextView.setText("");
                    }

                }


            } catch (JSONException e) {
                Log.i("ONPost Exception","Json Exception");
                Toast.makeText(getApplicationContext(),"Couldn't find Weather",Toast.LENGTH_LONG).show();
                resultTextView.setText("");
                e.printStackTrace();
            } catch (Exception e){
                Log.i("ONPost Exception","Main Exception");
                Toast.makeText(getApplicationContext(),"Couldn't find Weather",Toast.LENGTH_LONG).show();
                resultTextView.setText("");
                e.printStackTrace();
            }

        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cityName = findViewById(R.id.cityName);

        cityDisplay = findViewById(R.id.cityDisplay);

        resultTextView = findViewById(R.id.resultTextView);


    }
}
