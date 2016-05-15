package com.marcoslopez7.proyectofinal;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;


public class CreatePurchase extends FragmentActivity implements OnMapReadyCallback {

    private static  double LATTITUDE = 19.431056899875358;
    private static  double LONGITUDE = -99.21455908566713;;
    private GoogleMap mMap;
    LatLng sydney = new LatLng(-34, 151);
    LatLng latLng = new LatLng(LATTITUDE, LONGITUDE);
    Marker marc;
    private static final String TAG = CreatePurchase.class.getSimpleName();
    private String opcionPago = "";
    private Button b_next;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_purchase);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        b_next = (Button) findViewById(R.id.b_next);
        b_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), CreatePurchase2.class);
                intent.putExtra(getResources().getString(R.string.Valor_Latitud) ,marc.getPosition().latitude);
                intent.putExtra(getResources().getString(R.string.Valor_Longitud) ,marc.getPosition().longitude);
                intent.putExtra(getResources().getString(R.string.Valor_Pago) ,opcionPago);
                startActivity(intent);
            }
        });
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        final android.content.Context context = this;
        final Marker marcador = mMap.addMarker(new MarkerOptions()
                .position(latLng)
                .draggable(true));
        marc = marcador;

        // Add a marker in Sydney and move the camera
        //mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng pos) {
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(pos, 10));
                marcador.setPosition(pos);
                imprimircoordenadas();
            }
        });
    }

    public void imprimircoordenadas(){
        LatLng pos = marc.getPosition();
        Log.d(TAG, pos.toString());
    }

    public void onRadioButtonClick(View v){
        boolean checked = ((RadioButton) v).isChecked();
        switch (v.getId()){
            case R.id.rb_cash:
                if(checked){
                    opcionPago = "Cash";
                }
                break;
            case R.id.rb_credit:
                if(checked)
                    opcionPago = "Credit";
                break;
            case  R.id.rb_deposit:
                if(checked)
                    opcionPago = "Deposit";
                break;
        }
    }

}
