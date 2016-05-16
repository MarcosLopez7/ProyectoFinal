package com.marcoslopez7.proyectofinal;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class HomeActivity extends AppCompatActivity {

    private Button b_buscar_categoria, b_buscar, b_validar, b_crear_categoria, b_vender, b_perfil,
            b_users;
    private final String url = "http://159.203.166.99:8000/products/categorias/";
    private EditText et_buscar, et_users;
    private static final String TAG = HomeActivity.class.getSimpleName();
    private JSONArray arr;
    Spinner s_categoria;
    String[] items;
    Intent intent;
    int Resp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        init();
        Log.d(TAG, "Session id: " + SessionHelper.id_user);
        s_categoria.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                try {
                    Resp = arr.getJSONObject(arg2).getInt("pk");
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub
                try {
                    Resp = arr.getJSONObject(0).getInt("pk");
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }

        });
        /*ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, items);
        s_categoria.setAdapter(adapter);
        s_categoria.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                Resp = arg0.getItemAtPosition(arg2).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub
                Resp = arg0.getItemAtPosition(0).toString();
            }

        });*/
        if(SessionHelper.admin_user){
            b_crear_categoria.setVisibility(View.VISIBLE);
            b_validar.setVisibility(View.VISIBLE);
            b_crear_categoria.setClickable(true);
            b_crear_categoria.setClickable(true);
        }
        b_buscar_categoria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(getApplicationContext(), ResultsActivity.class);
                intent.putExtra(getResources().getString(R.string.Valor_tipo_busqueda), getResources().getString(R.string.Busqueda_categoria));
                intent.putExtra(getResources().getString(R.string.Valor_busqueda), Resp);
                startActivity(intent);
            }
        });
        b_buscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(getApplicationContext(), ResultsActivity.class);
                intent.putExtra(getResources().getString(R.string.Valor_tipo_busqueda), getResources().getString(R.string.Busqueda_palabra));
                intent.putExtra(getResources().getString(R.string.Valor_busqueda), ""+et_buscar.getText());
                startActivity(intent);
            }
        });
        b_crear_categoria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(getApplicationContext(), CreateCategory.class);
                startActivity(intent);
            }
        });
        b_validar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(getApplicationContext(), ValidateActivity.class);
                startActivity(intent);
            }
        });
        b_perfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(getApplicationContext(), ViewProfileActivity.class);
                startActivity(intent);
            }
        });
        b_vender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(getApplicationContext(), CreateProduct.class);
                startActivity(intent);
            }
        });
    }

    void init(){
        b_buscar = (Button) findViewById(R.id.b_search);
        b_buscar_categoria = (Button)findViewById(R.id.b_search_2);
        b_crear_categoria = (Button)findViewById(R.id.b_category);
        b_perfil = (Button)findViewById(R.id.b_profile);
        b_validar = (Button)findViewById(R.id.b_validate);
        b_vender = (Button)findViewById(R.id.b_sell);
        s_categoria = (Spinner)findViewById(R.id.s_category);
        et_buscar = (EditText)findViewById(R.id.et_buscar);
        b_users = (Button) findViewById(R.id.b_users);
        et_users = (EditText) findViewById(R.id.et_search_user);
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
    private void setList(String js)throws JSONException{
        JSONArray categorias = new JSONArray(js);
        Log.d(TAG,"length: " + categorias.length());
        items = new String[categorias.length()];
        for(int i = 0; i < categorias.length(); ++i){
            items[i] = categorias.getJSONObject(i).getString("nombre");
            Log.d(TAG, "nombre: " + items[i]);
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, items);
        s_categoria.setAdapter(adapter);
        arr = categorias;
    }
}
