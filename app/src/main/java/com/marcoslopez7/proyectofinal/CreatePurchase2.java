package com.marcoslopez7.proyectofinal;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CreatePurchase2 extends AppCompatActivity {
    TextView tv_street, tv_district, tv_city;
    Button b_confirmar;
    Geocoder geo;
    List<Address> addresses;
    double lat, longi;
    String opc;
    String[] items;// = new String[]{"In Person","DHL","FEDEX"};
    private static final String TAG = CreatePurchase2.class.getSimpleName();
    private String url_fletes = "http://159.203.166.99:8000/products/fletes/";
    private String url_prod = "http://159.203.166.99:8000/products/";
    private String url = "http://159.203.166.99:8000/products/createcompra/";
    private String url2 = "http://159.203.166.99:8000/products/venta/";

    Spinner spin;
    JSONArray arr;
    double price;
    int idp, idf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_purchase2);
        tv_street = (TextView) findViewById(R.id.tv_street_content);
        tv_city = (TextView) findViewById(R.id.tv_city_content);
        tv_district = (TextView)findViewById(R.id.tv_postal_content);
        Intent esta = getIntent();
        lat = esta.getDoubleExtra(getResources().getString(R.string.Valor_Latitud), 0);
        longi = esta.getDoubleExtra(getResources().getString(R.string.Valor_Longitud), 0);
        opc = esta.getStringExtra(getResources().getString(R.string.Valor_Pago));
        idp = esta.getIntExtra(getResources().getString(R.string.Id_producto), 0);
        crearLista();;
        //ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, items);
        spin = (Spinner)findViewById(R.id.s_delivery);

        geo = new Geocoder(this, Locale.getDefault());
        try {
            addresses = geo.getFromLocation(lat, longi, 1);
        }
        catch (Exception e){

        }
        tv_street.setText(addresses.get(0).getAddressLine(0));
        tv_city.setText(addresses.get(0).getLocality());
        tv_district.setText(addresses.get(0).getAdminArea());

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(url_prod+idp).build();

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
                                getInfo(jsondata);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                } else {

                }


            }
        });
        spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                Log.d(TAG, arg0.getItemAtPosition(arg2).toString());
                try {
                    price = price + Double.parseDouble(arr.getJSONObject(arg2).getString("precio"));
                    idf = arr.getJSONObject(arg2).getInt("pk");
                    Log.d(TAG, "precio: " + price);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub

            }

        });

        b_confirmar = (Button)findViewById(R.id.b_confirm);
        b_confirmar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presionar();
            }
        });

    }
    private void crearLista(){
        OkHttpClient client;

        client = new OkHttpClient();

        Request request = new Request.Builder().url(url_fletes).build();

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
        Log.d(TAG,"length: " + categorias.length());
        items = new String[categorias.length()];
        for(int i = 0; i < categorias.length(); ++i){
            items[i] = categorias.getJSONObject(i).getString("nombre");
            Log.d(TAG, "nombre: " + items[i]);
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, items);
        spin.setAdapter(adapter);
        arr = categorias;
    }
    private void getInfo(String js) throws JSONException{
        JSONObject obj = new JSONObject(js);
        price =  Double.parseDouble(obj.getString("precio"));
        Log.d(TAG, "Recibo:" + price);
    }

    private void presionar(){
        final String[] plop = new String[]{"Confirmar", "Cancelar"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.select_dialog_item, plop);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirmar orden");
        //builder.setMessage("Precio Total = " + price);
        builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    confirmar();
                    dialog.cancel();
                } else {
                    dialog.cancel();
                }
            }
        });
        final AlertDialog dialog = builder.create();
        dialog.show();
    }
    private void confirmar(){
        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("producto",""+idp)
                .addFormDataPart("cliente", ""+SessionHelper.id_user)
                .addFormDataPart("entregado", "false")
                .addFormDataPart("precio_total", "" + price)
                .addFormDataPart("metodo_pago", opc)
                .addFormDataPart("coordenadasX", "" + lat)
                .addFormDataPart("coordenadasY", "" + longi)
                .addFormDataPart("flete", ""+idf)
                .build();

        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.d(TAG, "idp: " + idp);
                Log.d(TAG, "flete:" + idf);
                Log.d(TAG, "response:" + response);
                Log.d(TAG, "coo" + lat);
                Log.d(TAG, "metodo"+ opc);
                Log.d(TAG, "cooy"+longi);
                OkHttpClient client2 = new OkHttpClient();
                RequestBody requestBody2 = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("ultima_modificacion",""+idp)
                        .addFormDataPart("vendido", "true")
                        .build();

                Request request2 = new Request.Builder()
                        .url(url2+idp+"/")
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
        });



    }
}
