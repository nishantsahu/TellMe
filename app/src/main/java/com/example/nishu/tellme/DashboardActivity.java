package com.example.nishu.tellme;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class DashboardActivity extends AppCompatActivity {

    String aadhar, pass, URL;
    CardView farmDetails, cropDetails, irrigationDetails;
    Button logout;
    String name;
    OkHttpClient client;
    TextView mName, reward;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        client = new OkHttpClient();
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading Please wait...");
        progressDialog.setProgressStyle(progressDialog.STYLE_SPINNER);

        mName = findViewById(R.id.name);
        reward = findViewById(R.id.rewardpoint);
        reward.setText("200 Points");
        irrigationDetails = findViewById(R.id.IrrigationDetails);

        irrigationDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                irrigationData();
            }
        });

        SharedPreferences url = getSharedPreferences("URL", Context.MODE_PRIVATE);
        URL = url.getString("mainURL", "");

        SharedPreferences sharedPreferences = getSharedPreferences("MyData", Context.MODE_PRIVATE);
        aadhar = sharedPreferences.getString("aadharID", "");
        pass = sharedPreferences.getString("password", "");
        name = sharedPreferences.getString("name", "");
        mName.setText("Welcome " + name);

        notificationCheck();

        farmDetails = findViewById(R.id.farmDetails);
        cropDetails = findViewById(R.id.CropDetails);
        logout = findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences sharedPreferences = getSharedPreferences("MyData", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.clear();
                editor.commit();
                Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(i);
            }
        });
        farmDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), FarmDetailActivity.class);
                startActivity(i);
            }
        });
        cropDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                farmList();
            }
        });

    }
    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this).setIcon(android.R.drawable.ic_dialog_alert).setTitle("Exit")
                .setMessage("Are you sure you want to exit?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Intent.ACTION_MAIN);
                        intent.addCategory(Intent.CATEGORY_HOME);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                }).setNegativeButton("No", null).show();
    }


    public void farmList(){
        progressDialog.show();
        Request request = new Request.Builder()
                .url(URL+"/getFarmList")
                .post(RequestBody.create(MediaType.parse("application/json"), "{\n" +
                        "\t\"aadharID\" : \""+aadhar+"\"\n" +
                        "}"))
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
                                SharedPreferences farmList = getSharedPreferences("farmList", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = farmList.edit();
                                editor.putString("json", json);
                                editor.commit();
                                Intent crop = new Intent(getApplicationContext(), CropDetailActivity.class);
                                startActivity(crop);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            }
        });
    }

    public void irrigationData(){
        progressDialog.show();
        Request request = new Request.Builder()
                .url(URL+"/getCropList")
                .post(RequestBody.create(MediaType.parse("application/json"), "{\n" +
                        "\t\"aadharID\" : \""+aadhar+"\"\n" +
                        "}"))
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
                            progressDialog.dismiss();
                            try {
                                String json = response.body().string();
                                SharedPreferences cropList = getSharedPreferences("cropList", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = cropList.edit();
                                editor.putString("cList", json);
                                editor.commit();
                                Intent irrigation = new Intent(getApplicationContext(), IrrigationDetailActivity.class);
                                startActivity(irrigation);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }

            }
        });
    }

    public void notificationCheck() {
        
    }

}