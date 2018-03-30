package com.example.nishu.tellme;

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

public class PumpActivity extends AppCompatActivity {

    EditText horsePower, time;
    TextView output;
    Spinner spinner;
    String record;
    Button calculate;
    Double ti;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pump);

        horsePower = findViewById(R.id.horsepower);
        time = findViewById(R.id.time);
        spinner = findViewById(R.id.timeunits);
        calculate = findViewById(R.id.calculate);
        output = findViewById(R.id.output);

        String[] units = {
                "min", "hrs"
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, units);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i){
                    case 0:
                        record = "min";
                        break;
                    case 1:
                        record = "hrs";
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        calculate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String hp, t;
                hp = horsePower.getText().toString();
                t = time.getText().toString();
                if (hp.equals("")||t.equals("")){
                    Toast.makeText(getApplicationContext(), "Empty Field", Toast.LENGTH_LONG).show();
                }
                else{
                    cal();
                }
            }
        });

    }

    public void cal() {
        String unit;
        unit = record;
        Double hp, t, amount;
        hp = Double.valueOf(horsePower.getText().toString());
        t = Double.valueOf(time.getText().toString());
        switch (unit){
            case "min":
                ti = t;
                break;
            case "hrs":
                ti = t*60;
        }
        amount = 50*hp*ti;

        output.setText("You have used " + amount.toString() + " Litres of water");

    }
}
