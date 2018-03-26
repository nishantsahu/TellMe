package com.example.nishu.tellme;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CropDetailActivity extends AppCompatActivity {

    String aadhar;
    OkHttpClient client;
    String URL;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop_detail);

        SharedPreferences sharedPreferences = getSharedPreferences("MyData", Context.MODE_PRIVATE);
        aadhar = sharedPreferences.getString("aadharID", "");

        SharedPreferences url = getSharedPreferences("URL", Context.MODE_PRIVATE);
        URL = url.getString("mainURL", "");

        client = new OkHttpClient();

        getFarmList();
    }

    public void getFarmList() {
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
                                String json = response.body().string();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            }
        });
    }
}
