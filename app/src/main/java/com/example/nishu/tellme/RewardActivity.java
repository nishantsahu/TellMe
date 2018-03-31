package com.example.nishu.tellme;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
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

public class RewardActivity extends AppCompatActivity {

    TextView reward;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reward);

        reward = findViewById(R.id.points);

        SharedPreferences rewardPoint = getSharedPreferences("Reward", Context.MODE_PRIVATE);
        String rewardPoints = rewardPoint.getString("rewards", "");

        reward.setText(rewardPoints + "\n Points");


    }
}
