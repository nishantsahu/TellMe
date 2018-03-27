package com.example.nishu.tellme;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
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

public class LoginActivity extends AppCompatActivity {

    OkHttpClient client;
    public String URL = "https://quiet-sierra-48529.herokuapp.com/api";
    EditText mAadhar, mPassword;
    Button go, create;
    ProgressDialog mProgressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        SharedPreferences url = getSharedPreferences("URL", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = url.edit();
        editor.putString("mainURL" , URL);
        editor.commit();

        SharedPreferences sharedPreferences = getSharedPreferences("MyData", Context.MODE_PRIVATE);
        String adr = sharedPreferences.getString("aadharID", "");
        String pas = sharedPreferences.getString("password", "");
        if (adr.equals("")||pas.equals("")){

        }else{
            Intent dashbaord = new Intent(getApplicationContext(), DashboardActivity.class);
            startActivity(dashbaord);
        }

        setmProgressDialog();

        mAadhar = findViewById(R.id.aadhar);
        mPassword = findViewById(R.id.password);
        go = findViewById(R.id.login);

        go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mProgressDialog.show();
                login();
            }
        });

        create = findViewById(R.id.account);

        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent create = new Intent(getApplicationContext(), RegistrationActivity.class);
                startActivity(create);
            }
        });

        client = new OkHttpClient();

    }

    public void setmProgressDialog() {
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("Loading");
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
    }

    private void login() {

        String aadhar, password;
        aadhar = mAadhar.getText().toString();
        password = mPassword.getText().toString();
        Request request = new Request.Builder().url(URL+"/login")
                .post(RequestBody.create(MediaType.parse("application/json"), "{\n" +
                        "\t\"aadharID\":\""+ aadhar +"\",\n" +
                        "\t\"password\" : \""+ password +"\"\n" +
                        "}")).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mProgressDialog.dismiss();
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
                                    mProgressDialog.dismiss();
                                    String json = response.body().string();
                                    JSONObject mainObj = new JSONObject(json);
                                    String status = mainObj.getString("status");

                                    if (status.equals("success")){
                                        Intent dashboard = new Intent(getApplicationContext(), DashboardActivity.class);
                                        String name = mainObj.getString("name");
                                        SharedPreferences sharedPreferences = getSharedPreferences("MyData", Context.MODE_PRIVATE);
                                        SharedPreferences.Editor editor1 = sharedPreferences.edit();
                                        editor1.putString("aadharID", mAadhar.getText().toString());
                                        editor1.putString("password", mPassword.getText().toString());
                                        editor1.putString("name", name);
                                        editor1.commit();
                                        startActivity(dashboard);
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
}
