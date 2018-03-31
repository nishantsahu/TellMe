package com.example.nishu.tellme;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
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

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class IrrigationDetailActivity extends AppCompatActivity {

    JSONArray array;
    EditText water, time;
    Button submit;
    String aadhar, URL;
    OkHttpClient client;
    TextView list1;
    String src;
    String amount;
    Spinner spinner, source;
    ProgressDialog progressDialog;
    CardView canal, pump;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_irrigation_detail);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading Please wait...");
        progressDialog.setProgressStyle(progressDialog.STYLE_SPINNER);

        canal = findViewById(R.id.canal);
        pump = findViewById(R.id.pumpcard);

        feed();

        canal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), CanalActivity.class);
                startActivity(i);
            }
        });

        pump.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), PumpActivity.class);
                startActivity(i);
            }
        });

        list1 = findViewById(R.id.list1);
        source = findViewById(R.id.source);
        spinner = findViewById(R.id.cropList);
        water = findViewById(R.id.waterUsed);
        submit = findViewById(R.id.submit);
        SharedPreferences sharedPreferences = getSharedPreferences("MyData", Context.MODE_PRIVATE);
        aadhar = sharedPreferences.getString("aadharID", "");

        SharedPreferences url = getSharedPreferences("URL", Context.MODE_PRIVATE);
        URL = url.getString("mainURL", "");
        client = new OkHttpClient();

        SharedPreferences cropList = getSharedPreferences("cropList", Context.MODE_PRIVATE);
        String json = cropList.getString("cList", "");
        JSONObject mainObj = null;
        try {
            mainObj = new JSONObject(json);
            String data = mainObj.getString("data");
            array=mainObj.getJSONArray("data");

            final String[] frm = new String[array.length()];
            String[] fname = new String[array.length()];
            for(int i=0; i<array.length(); i++){
                String jfarm = array.getString(i);
                JSONObject mObj = new JSONObject(jfarm);
                frm[i] = mObj.getString("cropName");
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, frm);
            spinner.setAdapter(adapter);


        } catch (JSONException e) {
            e.printStackTrace();
        }

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, final int i, long l) {
                final String[] fID = new String[array.length()];
                for(int j=0; j<array.length(); j++){
                    String jfarm = null;
                    try {
                        jfarm = array.getString(i);
                        JSONObject mObj = new JSONObject(jfarm);
                        fID[i] = mObj.getString("cropID");
                        list1.setText(fID[i]);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        String[] sources = {
                "Canal", "Pump"
        };

        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, sources);
        source.setAdapter(adapter1);

        source.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i){
                    case 0:
                        src = "canal";
                        break;
                    case 1:
                        src = "pump";
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String a;
                a = water.getText().toString();
                if (a.equals("")){
                    Toast.makeText(getApplicationContext(), "Empty Field", Toast.LENGTH_LONG).show();
                }
                else{
                    feedData();
                }
            }
        });

    }

    public void feedData() {
        String swater, crop, source;
        swater = water.getText().toString();
        crop = list1.getText().toString().trim();
        source = src;

        progressDialog.show();

        Request request = new Request.Builder()
                .url(URL+"/feedIrrigationData")
                .post(RequestBody.create(MediaType.parse("application/json"), "{\n" +
                        "\t\"aadharID\" : \""+aadhar+"\",\n" +
                        "\t\"cropID\" : \""+crop+"\",\n" +
                        "\t\"waterAmount\" : \""+swater+"\",\n" +
                        "\t\"waterSource\" : \""+source+"\"\n" +
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
                                    Toast.makeText(getApplicationContext(), "Successfully Submitted", Toast.LENGTH_LONG).show();
                                    Intent dash = new Intent(getApplicationContext(), DashboardActivity.class);
                                    startActivity(dash);
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

    public void feed(){

    }
}
