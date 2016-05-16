package com.marcoslopez7.proyectofinal;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

public class ListUsersActivity extends AppCompatActivity {

    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_users);
        listView = (ListView) findViewById(R.id.listView);
        Intent intent = getIntent();
        String search_value = intent.getStringExtra(
                getResources().getString(R.string.user_search_value));

    }
}
