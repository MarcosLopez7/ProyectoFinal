package com.marcoslopez7.proyectofinal;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.MediaController;
import android.widget.VideoView;

public class VideoActivity extends AppCompatActivity {

    private VideoView videoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);
        videoView = (VideoView) findViewById(R.id.videoView);

        Uri uri = Uri.parse("http://ubiquitous.csf.itesm.mx/~daw-1020023/video/nike.mp4");
        videoView.setMediaController(new MediaController(this));
        videoView.setVideoURI(uri);
        videoView.requestFocus();
        videoView.start();

        new Sleeper().execute("");
    }

    private class Sleeper extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            try {
                Thread.sleep(20000);
            } catch (InterruptedException e) {
                Thread.interrupted();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
        }
    }
}
