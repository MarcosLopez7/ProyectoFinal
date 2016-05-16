package com.marcoslopez7.proyectofinal;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Purchase_Sells extends AppCompatActivity {


    private String url_purchase = "";
    private String url_sells = "";
    private ListView lv_sells, lv_purchases;
    private static final String TAG = Purchase_Sells.class.getSimpleName();
    String[] items;
    String[] items2;
    JSONArray compras, ventas;
    int idu, idp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchase__sells);
        Intent esta = getIntent();
        esta.getIntExtra(getResources().getString(R.string.Id_Usuario), 0);
        Log.d(TAG, "id_u: " + idu);
        lv_sells = (ListView)findViewById(R.id.lv_sells);
        lv_purchases = (ListView)findViewById(R.id.lv_purchase);
        lv_sells.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent2 = new Intent(getApplicationContext(), Purchase_Sells.class);
                try{
                    intent2.putExtra(getResources().getString(R.string.Id_Usuario), (int)ventas.getJSONObject(position).getInt("pk"));
                    Log.d(TAG, "entro");
                }
                catch (Exception e){
                    e.printStackTrace();
                }
                startActivity(intent2);
            }
        });
        lv_purchases.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent2 = new Intent(getApplicationContext(), Purchase_Sells.class);
                try{
                    intent2.putExtra(getResources().getString(R.string.Id_Usuario), (int)compras.getJSONObject(position).getInt("pk"));
                    Log.d(TAG, "entro");
                }
                catch (Exception e){
                    e.printStackTrace();
                }
                startActivity(intent2);
            }
        });
    }

    private void CrearConsultas(String url, String url2){
        OkHttpClient client, client2;
        client = new OkHttpClient();
        client2 = new OkHttpClient();

        Request request = new Request.Builder().url(url).build();
        Request request2 = new Request.Builder().url(url2).build();

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
                                setList(jsondata);
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

        client2.newCall(request).enqueue(new Callback() {
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
                                setList2(jsondata);
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
    private void setList(String js)throws JSONException {
        JSONArray categorias = new JSONArray(js);
        items = new String[categorias.length()];
        for(int i = 0; i < categorias.length(); ++i){
            items[i] = categorias.getJSONObject(i).getString("nombre");
            //Log.d(TAG, "nombre: " + items[i]);
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, items);
        lv_purchases.setAdapter(adapter);
        compras = categorias;
    }
    private void setList2(String js)throws JSONException {
        JSONArray categorias = new JSONArray(js);
        items = new String[categorias.length()];
        for(int i = 0; i < categorias.length(); ++i){
            items[i] = categorias.getJSONObject(i).getString("nombre");
            //Log.d(TAG, "nombre: " + items[i]);
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, items);
        lv_sells.setAdapter(adapter);
        ventas = categorias;
    }
}