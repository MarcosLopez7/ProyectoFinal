package com.marcoslopez7.proyectofinal;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class ViewProduct extends AppCompatActivity {
    Button b_purchase;
    int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_product);
        b_purchase = (Button)findViewById(R.id.b_purchase);
        b_purchase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), CreatePurchase.class);
                intent.putExtra(getResources().getString(R.string.Id_producto), id);
                startActivity(intent);
            }
        });
    }
}
