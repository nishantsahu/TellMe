package com.example.nishu.tellme;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

public class ConfRegActivity extends AppCompatActivity {

    EditText pass, cpass;
    String URL;
    Button submit;
    OkHttpClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conf_reg);

        pass = findViewById(R.id.password);
        cpass = findViewById(R.id.cpassword);
        submit = findViewById(R.id.submit);

        SharedPreferences url = getSharedPreferences("URL", Context.MODE_PRIVATE);
        URL = url.getString("mainURL", "");

        client = new OkHttpClient();

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registration();
            }
        });

    }

    public void registration() {

        String pass1, aadhar;
        aadhar = getIntent().getExtras().getString("aadhar");
        pass1 = pass.getText().toString();

        Request request = new Request.Builder()
                .url(URL+"/register")
                .post(RequestBody.create(MediaType.parse("application/json"), "{\n" +
                        "\t\"aadharID\":\""+aadhar+"\",\n" +
                        "\t\"password\" : \""+pass1+"\"\n" +
                        "}")).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "Connection error", Toast.LENGTH_LONG).show();
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
                                JSONObject mainObj = new JSONObject(json);
                                String status = mainObj.getString("status");

                                if (status.equals("success")){
                                    Toast.makeText(getApplicationContext(), "You are now registered", Toast.LENGTH_LONG).show();
                                    Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                                    startActivity(i);
                                }
                                else{
                                    String message = mainObj.getString("message");
                                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
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
