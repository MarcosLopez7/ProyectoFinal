package com.marcoslopez7.proyectofinal;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ViewProduct extends AppCompatActivity {
    Button b_purchase, b_edit, b_delete;
    private static final String TAG = ViewProduct.class.getSimpleName();
    TextView tv_nombre,tv_descripcion, tv_precio;
    ImageView iv;
    int id;
    private String url = "http://159.203.166.99:8000/products/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_product);
        Intent esta = getIntent();
        id = esta.getIntExtra(getResources().getString(R.string.Id_producto), 0);
        Log.d(TAG, "id: " + id);
        init();
        b_purchase = (Button)findViewById(R.id.b_purchase);
        b_purchase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), CreatePurchase.class);
                intent.putExtra(getResources().getString(R.string.Id_producto), (int)id);
                startActivity(intent);
            }
        });
        b_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), CreateProduct.class);
                intent.putExtra(getResources().getString(R.string.Owner), true);
                intent.putExtra(getResources().getString(R.string.Id_producto), id);
                startActivity(intent);
            }
        });
        b_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                delete();
            }
        });
    }

    private void init(){
        tv_nombre = (TextView)findViewById(R.id.tv_title);
        tv_descripcion = (TextView)findViewById(R.id.tv_description_content);
        tv_precio = (TextView)findViewById(R.id.tv_price_content);
        iv = (ImageView)findViewById(R.id.iv_photo_content);
        b_edit = (Button)findViewById(R.id.b_edit);
        b_delete = (Button)findViewById(R.id.b_delete);
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(url+id).build();

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

    private void setInfo(String json)throws JSONException{
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        Log.d(TAG,"entro");
        JSONObject obj = new JSONObject(json);
        tv_nombre.setText(obj.getString("nombre"));
        tv_descripcion.setText(obj.getString("descripcion"));
        tv_precio.setText(obj.getString("precio"));
        String s;
        s = obj.getString("foto").substring(43);
        Log.d(TAG,"S: "+ s);
        while(s.charAt(0) != 'i'){
            s = s.substring(1);
        }
        Log.d(TAG,"S: "+ s);
        InputStream is;
        Drawable d;
        try {
            is = (InputStream) new URL("http://159.203.166.99:8000/static/" + s).getContent();
            d = Drawable.createFromStream(is, "profile pic");
            iv.setImageDrawable(d);

        }
        catch (Exception e){
            e.printStackTrace();
        }
        if(obj.getInt("usuario") == SessionHelper.id_user){
            b_edit.setVisibility(View.VISIBLE);
            b_edit.setClickable(true);
            b_delete.setClickable(true);
            b_delete.setVisibility(View.VISIBLE);
        }
    }
    private void delete(){
        OkHttpClient client2;

        client2 = new OkHttpClient();

        Request request = new Request.Builder().url(url+"/delete/"+id+"/").delete().build();

        client2.newCall(request).enqueue(new Callback() {
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

