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

public class ListUsersActivity extends AppCompatActivity {

    private ListView listView;
    private String url_u = "http://159.203.166.99:8000/usuarios/";
    private String url_b = "http://159.203.166.99:8000/products/usuarionombre/?search=";
    private String[] items;
    private static final String TAG = ListUsersActivity.class.getSimpleName();
    JSONArray arreglo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_users);
        listView = (ListView) findViewById(R.id.listView);
        Intent intent = getIntent();
        String search_value = intent.getStringExtra(
                getResources().getString(R.string.user_search_value));
        CrearConsulta(url_b+search_value);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent2 = new Intent(getApplicationContext(), Purchase_Sells.class);
                try{
                    intent2.putExtra(getResources().getString(R.string.Id_Usuario), (int)arreglo.getJSONObject(position).getInt("pk"));
                    Log.d(TAG, "idu: "+arreglo.getJSONObject(position).getInt("pk"));
                    Log.d(TAG, "entro");
                    startActivity(intent2);
                }
                catch (Exception e){
                    e.printStackTrace();
                }

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
        listView.setAdapter(adapter);
        arreglo = categorias;
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
}
