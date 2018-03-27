package com.example.nishu.tellme;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class FarmDetailActivity extends AppCompatActivity {

    String URL;
    Spinner units, soils;
    EditText mArea, mFarmName, mGLevel;
    Double area;
    Button btn, getLocation;
    String record, city, dist, state;
    EditText locationText;
    String soilType;
    LocationManager locationManager;
    LocationListener locationListener;
    String postalCode;
    ProgressDialog progressDialog;
    double lat;
    double lng;
    OkHttpClient client;
    String aadhar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_farm_detail);

        getLocation = findViewById(R.id.getLocation);
        locationText = findViewById(R.id.locationText);
        mFarmName = findViewById(R.id.farmName);
        mGLevel = findViewById(R.id.wlevel);
        mArea = findViewById(R.id.area);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please Wait...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        SharedPreferences url = getSharedPreferences("URL", Context.MODE_PRIVATE);
        URL = url.getString("mainURL", "");

        btn = findViewById(R.id.submit);
        client = new OkHttpClient();

        //Meter
        units = findViewById(R.id.units);
        String[] unit = {
                "Acre", "Metre Sqr", "Hectare", "Yard"
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, unit);
        units.setAdapter(adapter);

        units.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i) {
                    case 0:
                        record = "acr";
                        break;
                    case 1:
                        record = "mtr";
                        break;
                    case 2:
                        record = "hec";
                        break;
                    case 3:
                        record = "yrd";
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog.show();
                submit();
            }
        });




        //soils
        soils = findViewById(R.id.soil);
        final String[] soil = {
                "Select Soil Type","Black Soil", "Red Soil", "Alluvial Soil", "Laterite Soil", "Others"
        };

        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, soil);
        soils.setAdapter(adapter1);

        soils.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i){
                    case 0:
                        soilType = "null";
                        break;
                    case 1:
                        soilType = "black";
                        break;
                    case 2:
                        soilType = "red";
                        break;
                    case 3:
                        soilType = "alluvial";
                        break;
                    case 4:
                        soilType = "laterite";
                        break;
                    case 5:
                        soilType = "others";
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        locationData();

    }

    public void locationData() {
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                lat = location.getLatitude();
                lng = location.getLongitude();
                try {
                    getPostal();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        };

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[] {
                    Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.INTERNET
            }, 10);
            return;
        }
        else{
            getLocationData();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (10){
            case 10:
                if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    getLocationData();
                }
        }
    }

    public void getLocationData(){
        getLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog.show();
                locationManager.requestLocationUpdates("gps", 1000, 10, locationListener);
            }
        });
    }

    public void getPostal() throws IOException {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
        postalCode = addresses.get(0).getPostalCode();
        city = addresses.get(0).getLocality();
        state = addresses.get(0).getAdminArea();
        locationText.setText(city+", "+state);
        getCity();
    }

    public void getCity() {
        Request request = new Request.Builder()
                .url("http://postalpincode.in/api/pincode/" + postalCode)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(), "Connection Error", Toast.LENGTH_LONG).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                if (response.isSuccessful()){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                progressDialog.dismiss();
                                String json = response.body().string();
                                JSONObject mainObj = new JSONObject(json);
                                JSONArray arr = mainObj.getJSONArray("PostOffice");
                                JSONObject distr = arr.getJSONObject(0);
                                dist = distr.getString("District");
                            } catch (IOException e) {
                                e.printStackTrace();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            }
        });
    }

    public void submit() {

        getLocationData();

        final String sArea, sUnit, sLocation, sSoil, sFarmName, sWaterLevel;

        sArea = mArea.getText().toString();
        sUnit = record;
        sSoil = soilType;
        sFarmName = mFarmName.getText().toString();
        sWaterLevel = mGLevel.getText().toString();
        sLocation = locationText.getText().toString();
        SharedPreferences sharedPreferences = getSharedPreferences("MyData", Context.MODE_PRIVATE);
        aadhar = sharedPreferences.getString("aadharID", "");

        if (sArea.equals("")||sUnit.equals("")||sSoil.equals("null")||sLocation.equals("")||sFarmName.equals("")||sWaterLevel.equals("")){
            progressDialog.dismiss();
            Toast.makeText(getApplicationContext(), "Empty field", Toast.LENGTH_LONG).show();
        }
        else{

            switch(sUnit){
                case "acr":
                    area = Double.valueOf(sArea)*4046.86;
                    break;
                case "mtr":
                    area = Double.valueOf(sArea);
                    break;
                case "hec":
                    area = Double.valueOf(sArea)*10000;
                    break;
                case "yrd":
                    area = Double.valueOf(sArea)*0.0836127;
                    break;
            }

            Request request = new Request.Builder()
                    .url(URL+"/feedFarmData")
                    .post(RequestBody.create(MediaType.parse("application/json"), "{\n" +
                            "\t\"aadharID\" : \""+aadhar+"\",\n" +
                            "\t\"latitude\" : "+lat+",\n" +
                            "\t\"longitude\" : "+lng+",\n" +
                            "\t\"farmName\" : \""+sFarmName+"\",\n" +
                            "\t\"state\" : \""+state+",\",\n" +
                            "\t\"district\" : \""+dist+"\",\n" +
                            "\t\"city\" : \""+city+"\",\n" +
                            "\t\"landArea\" : \""+sArea+"\",\n" +
                            "\t\"groundWaterLevel\" : \""+sWaterLevel+"\",\n" +
                            "\t\"soilType\":\""+soilType+"\"\n" +
                            "}")).build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), "Connection Error", Toast.LENGTH_LONG).show();
                        }
                    });
                }

                @Override
                public void onResponse(Call call, final Response response) throws IOException {
                    if (response.isSuccessful()){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    progressDialog.dismiss();
                                    String json = response.body().string();
                                    JSONObject mainObj = new JSONObject(json);
                                    String status = mainObj.getString("status");
                                    if (status.equals("success")){
                                        Toast.makeText(getApplicationContext(), "Your farm details are registered", Toast.LENGTH_LONG).show();
                                        Intent dashboard = new Intent(getApplicationContext(), DashboardActivity.class);
                                        startActivity(dashboard);
                                    }
                                    else{
                                        Toast.makeText(getApplicationContext(), "Something went wrong", Toast.LENGTH_LONG).show();
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                }
            });
        }
    }
}
