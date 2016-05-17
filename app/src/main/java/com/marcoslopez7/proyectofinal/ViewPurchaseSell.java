package com.marcoslopez7.proyectofinal;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ViewPurchaseSell extends AppCompatActivity {
    private String url = "";
    private String url_deliver = "";
    private TextView tv_pname, tv_buyer, tv_price, tv_method, tv_street, tv_city, tv_transport;

    private Button b_deliver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_purchase_sell);
        init();
        CrearConsulta(url);
    }
    private void init(){
        tv_buyer = (TextView)findViewById(R.id.tv_buyer_name_content);
        tv_pname = (TextView)findViewById(R.id.tv_name_content);
        tv_price = (TextView)findViewById(R.id.tv_price_content);
        tv_method = (TextView)findViewById(R.id.tv_method_content);
        tv_street = (TextView)findViewById(R.id.tv_street_content);
        tv_city = (TextView)findViewById(R.id.tv_city_content);
        tv_transport = (TextView)findViewById(R.id.tv_transport_content);
        b_deliver = (Button)findViewById(R.id.b_delivered);
        b_deliver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                llego();
            }
        });

    }

    private void CrearConsulta(String url){
        OkHttpClient client;
        client = new OkHttpClient();

        Request request = new Request.Builder().url(url).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String jsondata = response.body().string();
                if (response.isSuccessful()) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                setInfo(jsondata);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                } else {
                }
                /**/

            }
        });
    }

    private void setInfo(String json) throws JSONException{
        JSONObject obj = new JSONObject(json);
        tv_buyer.setText(obj.getString(""));
    }

    private void llego(){
        OkHttpClient client;
        client = new OkHttpClient();

        Request request = new Request.Builder().url(url_deliver).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String jsondata = response.body().string();
                if (response.isSuccessful()) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                                startActivity(intent);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                } else {
                }
                /**/

            }
        });

    }
}
