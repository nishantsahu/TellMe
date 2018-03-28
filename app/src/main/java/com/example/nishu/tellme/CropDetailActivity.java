package com.example.nishu.tellme;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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
    TextView list;
    OkHttpClient client;
    String URL;
    ProgressDialog progressDialog;
    Spinner spinner;
    String[] frm = new String[1000];
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop_detail);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading..");
        progressDialog.setProgressStyle(progressDialog.STYLE_SPINNER);

        spinner = findViewById(R.id.farmList);


        list = findViewById(R.id.list);

        SharedPreferences sharedPreferences = getSharedPreferences("MyData", Context.MODE_PRIVATE);
        aadhar = sharedPreferences.getString("aadharID", "");

        SharedPreferences url = getSharedPreferences("URL", Context.MODE_PRIVATE);
        URL = url.getString("mainURL", "");

        client = new OkHttpClient();

        getFarmList();

    }

    public void getFarmList() {
        progressDialog.show();
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
                                String data = mainObj.getString("data");
                                JSONArray array=mainObj.getJSONArray("data");

                                for(int i=0; i<array.length(); i++){
                                    frm[i] = array.getString(i);
                                }

                                for(int i=0; i<array.length(); i++){
                                    list.append(frm[i]+", ");
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
