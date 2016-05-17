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
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ResultsActivity extends AppCompatActivity {
    private String tipob, text_b;
    private String url_b_categoria = "http://159.203.166.99:8000/products/productobycategoria/?categoria=";
    private String url_b_text = "http://159.203.166.99:8000/products/productobytext/?search=";
    private int cat_id;
    private ListView lv;
    String[] items;
    JSONArray arreglo;
    private static final String TAG = ResultsActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);
        lv = (ListView)findViewById(R.id.listView);

        Intent intent = getIntent();
        tipob = intent.getStringExtra(getResources().getString(R.string.Valor_tipo_busqueda));
        if (tipob.equals(getResources().getString(R.string.Busqueda_categoria))){
            cat_id = intent.getIntExtra(getResources().getString(R.string.Valor_busqueda), 0);
            CrearConsulta(url_b_categoria+cat_id);
            Log.d(TAG,"cat");
        }
        else if (tipob.equals(getResources().getString(R.string.Busqueda_palabra))){
            text_b = "" + intent.getStringExtra(getResources().getString(R.string.Valor_busqueda));
            CrearConsulta(url_b_text+text_b);
            Log.d(TAG,"tipob");
        }
        else{Log.d(TAG,tipob);}
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent2 = new Intent(getApplicationContext(), ViewProduct.class);
                try {
                    intent2.putExtra(getResources().getString(R.string.Id_producto), (int)arreglo.getJSONObject(position).getInt("pk"));
                }
                catch (Exception e){
                    e.printStackTrace();
                }
                startActivity(intent2);
            }
        });
    }
    private void setList(String js)throws JSONException{
        JSONArray categorias = new JSONArray(js);
        if (categorias.length() == 0){
            ((TextView)findViewById(R.id.tv_noresuults)).setVisibility(View.VISIBLE);
        }
        items = new String[categorias.length()];
        for(int i = 0; i < categorias.length(); ++i){
            items[i] = categorias.getJSONObject(i).getString("nombre");
            //Log.d(TAG, "nombre: " + items[i]);
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, items);
        lv.setAdapter(adapter);
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
                    ((TextView)findViewById(R.id.tv_noresuults)).setVisibility(View.VISIBLE);
                }
                /**/

            }
        });
    }
}
