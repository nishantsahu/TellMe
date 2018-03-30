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

public class CanalActivity extends AppCompatActivity {

    Spinner units, units1;
    String record, record1;
    Button cal;
    TextView output;
    double areas, dp;
    EditText area, depth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_canal);

        cal = findViewById(R.id.calculate);
        output = findViewById(R.id.output);
        area = findViewById(R.id.area);
        depth = findViewById(R.id.depth);

        units = findViewById(R.id.units);
        units1 = findViewById(R.id.depthunits);

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

        String[] unit1 = {
                "cm", "m", "inch"
        };

        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, unit1);
        units1.setAdapter(adapter1);

        units1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i) {
                    case 0:
                        record1 = "cm";
                        break;
                    case 1:
                        record1 = "m";
                        break;
                    case 2:
                        record1 = "inch";
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        cal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String a, d;
                a = area.getText().toString();
                d = depth.getText().toString();
                if (a.equals("")||d.equals("")){
                    Toast.makeText(getApplicationContext(), "Empty Field", Toast.LENGTH_LONG).show();
                }
                else{
                    calculate();
                }
            }
        });

    }

    public void calculate(){
        String sUnit;
        double sDepth;
        sDepth = Double.parseDouble(depth.getText().toString().trim());
        String ar;
        String Units1 = record1;
        sUnit = record;
        ar = area.getText().toString();

        switch(sUnit){
            case "acr":
                areas = Double.valueOf(ar)*4046.86;
                break;
            case "mtr":
                areas = Double.valueOf(ar);
                break;
            case "hec":
                areas = Double.valueOf(ar)*10000;
                break;
            case "yrd":
                areas = Double.valueOf(ar)*0.0836127;
                break;
        }
        switch (Units1){
            case "cm":
                dp = sDepth*0.001;
                break;
            case "m":
                dp = sDepth;
                break;
            case "inch":
                dp = sDepth*0.0254;
                break;
        }

        Double vol, used;
        vol = areas*dp;
        used = vol*1000;
        output.setText("You have used " + used.toString() + " Litres of water");

    }
}
