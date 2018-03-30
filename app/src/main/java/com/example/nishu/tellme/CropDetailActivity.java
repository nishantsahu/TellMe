package com.example.nishu.tellme;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
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

public class CropDetailActivity extends AppCompatActivity {

    String aadhar;
    TextView list, list1;
    OkHttpClient client;
    Button submit;
    String URL, record;
    Double area;
    ProgressDialog progressDialog;
    Spinner spinner, units;
    JSONArray array;
    EditText cropName, seedID, cropArea;
    OkHttpClient okHttpClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop_detail);

        client = new OkHttpClient();
        submit = findViewById(R.id.submitBtn);
        cropName = findViewById(R.id.cname);
        seedID = findViewById(R.id.seedid);
        cropArea = findViewById(R.id.areaseeded);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading..");
        progressDialog.setProgressStyle(progressDialog.STYLE_SPINNER);

        spinner = findViewById(R.id.farmList);


        list = findViewById(R.id.list);
        list1 = findViewById(R.id.list1);

        SharedPreferences sharedPreferences = getSharedPreferences("MyData", Context.MODE_PRIVATE);
        aadhar = sharedPreferences.getString("aadharID", "");

        SharedPreferences url = getSharedPreferences("URL", Context.MODE_PRIVATE);
        URL = url.getString("mainURL", "");
        client = new OkHttpClient();

        SharedPreferences farmList = getSharedPreferences("farmList", Context.MODE_PRIVATE);
        String json = farmList.getString("json", "");
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
                frm[i] = mObj.getString("farmName");
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
                        fID[i] = mObj.getString("farmID");
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

        units = findViewById(R.id.units);
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

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog.show();
                String cropnm;
                String seed;
                String frm;
                String csas;
                String sUnit;
                cropnm = cropName.getText().toString();
                seed = seedID.getText().toString();
                frm = list1.getText().toString().trim();
                csas = String.valueOf(cropArea.getText());
                sUnit = record;

                if (cropnm.equals("")||seed.equals("")||frm.equals("")||csas.equals("")){
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(),"Empty Fields", Toast.LENGTH_LONG).show();
                }
                else{
                    switch(sUnit){
                        case "acr":
                            area = Double.valueOf(csas)*4046.86;
                            break;
                        case "mtr":
                            area = Double.valueOf(csas);
                            break;
                        case "hec":
                            area = Double.valueOf(csas)*10000;
                            break;
                        case "yrd":
                            area = Double.valueOf(csas)*0.0836127;
                            break;
                    }

                    Request request = new Request.Builder()
                            .url(URL+"/feedCropData")
                            .post(RequestBody.create(MediaType.parse("application/json"), "{\n" +
                                    "\t\"aadharID\" : \""+aadhar+"\",\n" +
                                    "\t\"cropName\" : \""+cropnm+"\",\n" +
                                    "\t\"seedID\" : \""+seed+"\",\n" +
                                    "\t\"farmID\" : \""+frm+"\",\n" +
                                    "\t\"cropSeededAreaSize\" : \""+area.toString()+"\"\n" +
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
                                            JSONObject mainObj = new JSONObject(json);
                                            String status = mainObj.getString("status");
                                            if (status.equals("success")){
                                                Toast.makeText(getApplicationContext(), "You crop details are submitted", Toast.LENGTH_LONG).show();
                                                Intent i = new Intent(getApplicationContext(), DashboardActivity.class);
                                                startActivity(i);
                                            }
                                            else{
                                                Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_LONG).show();
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
        });

    }

//    public void getFarmList() {
//        progressDialog.show();
//        Request request = new Request.Builder()
//                .url(URL+"/getFarmList")
//                .post(RequestBody.create(MediaType.parse("application/json"), "{\n" +
//                        "\t\"aadharID\" : \""+aadhar+"\"\n" +
//                        "}"))
//                .build();
//        client.newCall(request).enqueue(new Callback() {
//            @Override
//            public void onFailure(Call call, IOException e) {
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        progressDialog.dismiss();
//                        Toast.makeText(getApplicationContext(), "Connection Error", Toast.LENGTH_LONG).show();
//                    }
//                });
//            }
//
//            @Override
//            public void onResponse(Call call, final Response response) throws IOException {
//                if (response.isSuccessful()){
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            try {
//                                progressDialog.dismiss();
//                                String json = response.body().string();
//                                JSONObject mainObj = new JSONObject(json);
//                                String data = mainObj.getString("data");
//                                array=mainObj.getJSONArray("data");
//
//
//                                for(int i=0; i<array.length(); i++){
//                                    frm[i] = new String();
//                                    frm[i] = array.getString(i);
//                                }
//
//                                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, frm);
//                                spinner.setAdapter(adapter);
//
//                            } catch (IOException e) {
//                                e.printStackTrace();
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                            }
//                        }
//                    });
//                }
//            }
//        });
//    }
}
