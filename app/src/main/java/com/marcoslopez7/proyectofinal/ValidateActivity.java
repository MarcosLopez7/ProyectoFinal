package com.marcoslopez7.proyectofinal;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ValidateActivity extends AppCompatActivity {
    private String url = "http://159.203.166.99:8000/products/productosnotapproved/";
    private String url2 = "http://159.203.166.99:8000/products/aprobar/";
    private String[] items;
    private ListView lv;
    private JSONArray arreglo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_validate);
        lv = (ListView)findViewById(R.id.lv_validate);
        CrearConsulta(url);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    validar(arreglo.getJSONObject(position).getInt("pk"));
                    //Log.d(TAG, "entro");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                //startActivity(intent2);
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
    }
    private void setList(String js)throws JSONException {
        JSONArray categorias = new JSONArray(js);
        items = new String[categorias.length()];
        if (categorias.length() == 0){
            ((TextView)findViewById(R.id.tv_noresuults)).setVisibility(View.VISIBLE);
        }
        for(int i = 0; i < categorias.length(); ++i){
            items[i] = categorias.getJSONObject(i).getString("nombre");
            //Log.d(TAG, "nombre: " + items[i]);
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, items);
        lv.setAdapter(adapter);
        arreglo = categorias;
    }
    private void validar(int pos){
        OkHttpClient client2 = new OkHttpClient();
        RequestBody requestBody2 = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("ultima_modificacion","admin")
                .addFormDataPart("aprobado", "true")
                .build();

        Request request2 = new Request.Builder()
                .url(url2+pos+"/")
                .put(requestBody2)
                .build();

        client2.newCall(request2).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                startActivity(intent);
            }
        });
    }
}
