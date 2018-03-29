package com.example.nishu.tellme;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
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

public class RegistrationActivity extends AppCompatActivity {

    String URL;
    String mAadhar;
    OkHttpClient client;
    private Button getOTP, verify;
    private EditText aadhar;
    RelativeLayout popup;
    TextView again_login;
    private EditText otp;
    ImageView close;
    ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        again_login=findViewById(R.id.back_to_login);
        aadhar = findViewById(R.id.aadhar_register);
        getOTP = findViewById(R.id.getOTP);
        popup = findViewById(R.id.popup);
        otp = findViewById(R.id.enterotp);
        verify = findViewById(R.id.verify);
        close = findViewById(R.id.close);

        again_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent login=new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(login);
            }
        });
        setProgressBar();

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popup.setVisibility(View.INVISIBLE);
            }
        });

        popup.setVisibility(View.INVISIBLE);

        client = new OkHttpClient();

        SharedPreferences url = getSharedPreferences("URL", Context.MODE_PRIVATE);
        URL =  url.getString("mainURL", "");

        getOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mProgressDialog.show();
                getOTP();
            }
        });

        verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mProgressDialog.show();
                otpVerify();
            }
        });
    }

    public void setProgressBar(){
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("Loading Please Wait...");
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
    }

    public void getOTP(){
        final String aadharNo;
        aadharNo = aadhar.getText().toString().trim();
        Request request = new Request.Builder().url(URL + "/getOTP")
                .post(RequestBody.create(MediaType.parse("application/json"), "{\n" +
                        "\t\"aadharID\" : \"" + aadharNo + "\"\n" +
                        "}")).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mProgressDialog.dismiss();
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
                                mProgressDialog.dismiss();
                                String json = response.body().string();
                                JSONObject maiObj = new JSONObject(json);
                                String status = maiObj.getString("status");

                                if (status.equals("success")){
                                    Toast.makeText(getApplicationContext(), "OTP sent", Toast.LENGTH_LONG).show();
                                    popup.setVisibility(View.VISIBLE);
                                }
                                else{
                                    String message = maiObj.getString("message");
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

    public void otpVerify() {
        final String otpEnter, otpAadhar;
        otpAadhar = aadhar.getText().toString().trim();
        otpEnter = otp.getText().toString().trim();
        Request request = new Request.Builder()
                .url(URL+"/verifyOTP")
                .post(RequestBody.create(MediaType.parse("application/json"), "{\n" +
                        "\t\"aadharID\":\""+ otpAadhar +"\",\n" +
                        "\t\"OTP\" : \""+ otpEnter +"\"\n" +
                        "}"))
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mProgressDialog.dismiss();
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
                                mProgressDialog.dismiss();
                                String json = response.body().string();
                                JSONObject mainObj = new JSONObject(json);
                                String status = mainObj.getString("status");

                                if (status.equals("success")){
                                    Intent confReg = new Intent(getApplicationContext(), ConfRegActivity.class);
                                    confReg.putExtra("aadhar", otpAadhar);
                                    startActivity(confReg);
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
        Intent login=new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(login);
    }
}
