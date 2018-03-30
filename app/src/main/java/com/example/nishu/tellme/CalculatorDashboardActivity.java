package com.example.nishu.tellme;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.Button;

public class CalculatorDashboardActivity extends AppCompatActivity {

    CardView canal;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculator_dashboard);

        canal = findViewById(R.id.canelcard);

        canal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), CanalActivity.class);
                startActivity(i);
            }
        });

    }
}
