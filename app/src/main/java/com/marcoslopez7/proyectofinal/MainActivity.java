package com.marcoslopez7.proyectofinal;

import android.app.DownloadManager;
import android.app.VoiceInteractor;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private EditText emailField;
    private EditText passwordField;
    private Button loginButton;
    private Button createButton;
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private OkHttpClient client;
    private static final String url = "http://159.203.166.99:8000/products/login/";
    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        emailField = (EditText) findViewById(R.id.emailField);
        passwordField = (EditText) findViewById(R.id.passwordField);
        loginButton = (Button) findViewById(R.id.loginButton);
        createButton = (Button) findViewById(R.id.createButton);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });
        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), CreateUserActivity.class);
                startActivity(intent);
            }
        });
    }


    private void login() {
        //Intent intent = new Intent(getApplicationContext(), CreatePurchase.class);
        //startActivity(intent);
        client = new OkHttpClient();
        RequestBody body = new FormBody.Builder().add("email", emailField.getText().toString()).
                add("contrasena", passwordField.getText().toString()).build();

        Request request = new Request.Builder().url(url).post(body).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String jsondata = response.body().string();
                if (response.isSuccessful()) {
                    if (jsondata != "invalid")
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                                startActivity(intent);
                            }
                        });

                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "User o password invalids",
                                    Toast.LENGTH_LONG).show();
                        }
                    });

                }
                /**/

            }
        });

        /*try {

            Response response = client.newCall(request).execute();

            if(!response.isSuccessful()) throw new IOException("Error en conexion " + response);

            Log.d(TAG, response.body().string());
        } catch (IOException e) {
            e.printStackTrace();
        }*/
    }

}
