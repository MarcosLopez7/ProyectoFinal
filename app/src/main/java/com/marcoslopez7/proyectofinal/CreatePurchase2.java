package com.marcoslopez7.proyectofinal;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.nfc.Tag;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class CreatePurchase2 extends AppCompatActivity {
    TextView tv_street, tv_district, tv_city;
    Geocoder geo;
    List<Address> addresses;
    double lat, longi;
    String opc;
    String[] items = new String[]{"In Person","DHL","FEDEX"};
    private static final String TAG = CreatePurchase2.class.getSimpleName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_purchase2);
        tv_street = (TextView) findViewById(R.id.tv_street_content);
        tv_city = (TextView) findViewById(R.id.tv_city_content);
        tv_district = (TextView)findViewById(R.id.tv_postal_content);
        Intent intent = getIntent();
        lat = intent.getDoubleExtra(getResources().getString(R.string.Valor_Latitud),0);
        longi = intent.getDoubleExtra(getResources().getString(R.string.Valor_Longitud), 0);
        opc = intent.getStringExtra(getResources().getString(R.string.Valor_Pago));
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, items);
        Spinner spin = (Spinner)findViewById(R.id.s_delivery);
        spin.setAdapter(adapter);
        spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                Log.d(TAG, arg0.getItemAtPosition(arg2).toString());
            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub

            }

        });

        geo = new Geocoder(this, Locale.getDefault());
        try {
            addresses = geo.getFromLocation(lat, longi, 1);
        }
        catch (Exception e){

        }
        tv_street.setText(addresses.get(0).getAddressLine(0));
        tv_city.setText(addresses.get(0).getLocality());
        tv_district.setText(addresses.get(0).getAdminArea());
    }
}
