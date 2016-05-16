package com.marcoslopez7.proyectofinal;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.media.session.MediaController;
import android.media.session.MediaSession;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ViewProfileActivity extends AppCompatActivity {
    private static final String url = "http://159.203.166.99:8000/products/usuario/" + SessionHelper.id_user;
    private OkHttpClient client;
    private TextView tv_name, tv_email,tv_phone;
    private static final String TAG = ViewProfileActivity.class.getSimpleName();
    private ImageView iv;
    private VideoView video;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_profile);
        Button b_editar = (Button)findViewById(R.id.b_edit);
        b_editar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent lol = new Intent(getApplicationContext(), CreateUserActivity.class);
                lol.putExtra(getResources().getString(R.string.Owner), true);
                startActivity(lol);
            }
        });

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
                                init(jsondata);
                            }
                            catch (Exception e){
                                e.printStackTrace();
                            }

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
    }

    private void init(String jsona)throws JSONException{
        String s;
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        JSONObject user = new JSONObject(jsona);
        tv_name = (TextView)findViewById(R.id.tv_name_content);
        tv_email = (TextView)findViewById(R.id.tv_email_content);
        tv_phone = (TextView)findViewById(R.id.tv_phone_content);
        tv_name.setText(user.getString("nombre") + " " + user.getString("apellidos"));
        tv_email.setText(user.getString("email"));
        tv_phone.setText(user.getString("telefono"));
        iv = (ImageView)findViewById(R.id.iv_imagen);
        video = (VideoView) findViewById(R.id.video);
        s = user.getString("foto").substring(53);
        while(s.charAt(0) != 'i'){
            s = s.substring(1);
        }
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

        /*try {
            MediaController mediaController = new MediaController(this, MediaSession.Token.CREATOR);

        } catch (Exception e){
            e.printStackTrace();
        }*/


        Log.d(TAG, "Sub: " + user.getString("foto").substring(54));
    }
}
