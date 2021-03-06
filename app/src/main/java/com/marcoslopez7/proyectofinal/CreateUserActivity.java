package com.marcoslopez7.proyectofinal;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CreateUserActivity extends AppCompatActivity {

    private EditText nameField;
    private EditText lastNameField;
    private EditText emailField;
    private EditText passwordField;
    private EditText phoneField;
    private TextView fileRoute;
    private TextView videoRoute;
    private Button photoButton;
    private Button videoButton;
    private Button submitButton;
    private Uri imageURL;
    private Uri videoURL;
    private String path = "";
    private String pathV = "";
    private static final int PICK_FROM_CAMERA = 1;
    private static final int PICK_FROM_FILE = 2;
    private static final int VIDEO_FROM_FILE = 3;
    private static final MediaType MEDIA_TYPE_IMAGE = MediaType.parse("image/*");
    private static final MediaType MEDIA_TYPE_VIDEO = MediaType.parse("video/*");
    private static final String url = "http://159.203.166.99:8000/products/create/";
    private static final String url_update = "http://159.203.166.99:8000/products/updateusuario/" + SessionHelper.id_user + "/";
    private static final String url_llenar = "http://159.203.166.99:8000/products/usuario/" + SessionHelper.id_user;
    private static final String TAG = CreateUserActivity.class.getSimpleName();
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_user);
        Intent esta = getIntent();

        nameField = (EditText) findViewById(R.id.et_name);
        lastNameField = (EditText) findViewById(R.id.et_last);
        emailField = (EditText) findViewById(R.id.et_email);
        passwordField = (EditText) findViewById(R.id.et_password);
        phoneField = (EditText) findViewById(R.id.et_phone);
        photoButton = (Button) findViewById(R.id.b_photo);
        videoButton = (Button) findViewById(R.id.b_video);
        submitButton = (Button) findViewById(R.id.b_submit);
        fileRoute = (TextView) findViewById(R.id.tv_photo);
        videoRoute = (TextView) findViewById(R.id.tv_video);

        final String[] items = new String[]{"Camara", "Galeria"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this,
                android.R.layout.select_dialog_item,
                items
        );

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Selecciona una imagen");
        builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    File file = new File(Environment.getExternalStorageDirectory(),
                            "tpm" + String.valueOf(System.currentTimeMillis()) + ".jpg");
                    imageURL = Uri.fromFile(file);

                    try {
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageURL);
                        intent.putExtra("return data", true);
                        startActivityForResult(intent, PICK_FROM_CAMERA);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    dialog.cancel();
                } else {
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent, "Complete action using"),
                            PICK_FROM_FILE);
                }
            }
        });
        final AlertDialog dialog = builder.create();
        photoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show();
            }
        });

        videoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("video/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Complete action using"),
                        VIDEO_FROM_FILE);
            }
        });
        if(esta.getBooleanExtra(getResources().getString(R.string.Owner), false)){
            submitButton.setText("Update");
            llenar();
            submitButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(is_valid()){
                        update();
                    }else {
                        Toast t = Toast.makeText(getApplicationContext(), "Invalid email", Toast.LENGTH_SHORT);
                        t.show();
                        return;
                    }

                }
            });
        }
        else{
            submitButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(Patterns.EMAIL_ADDRESS.matcher(emailField.getText().toString()).matches()){
                        submit();
                    }else {
                        Toast t = Toast.makeText(getApplicationContext(), "Invalid email", Toast.LENGTH_SHORT);
                        t.show();
                        return;
                    }
                }
            });
        }

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client2 = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK)
            return;
        if (requestCode == PICK_FROM_FILE) {
            imageURL = data.getData();
            path = getRealImagePath(imageURL);
            //if (path != null)
            //    path = imageURL.getPath();
        } else if (requestCode == VIDEO_FROM_FILE) {
            videoURL = data.getData();
            pathV = getRealVideoPath(videoURL);
            //if (pathV != null)
            //    pathV = videoURL.getPath();
            videoRoute.setText(pathV);
        } else
            path = imageURL.getPath();

        fileRoute.setText(path);
    }

    private String getRealVideoPath(Uri content) {
        String[] data = {MediaStore.Video.Media.DATA};
        Cursor cursor = getContentResolver().query(content, data, null, null, null);
        if (cursor == null)
            return null;
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    private String getRealImagePath(Uri content) {
        String[] data = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(content, data, null, null, null);
        if (cursor == null)
            return null;
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    private void submit() {
        if(!validate()){
            Toast t = Toast.makeText(getApplicationContext(), "Please fill all the camps", Toast.LENGTH_SHORT);
            t.show();
            return;
        }
        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("nombre", nameField.getText().toString())
                .addFormDataPart("apellidos", lastNameField.getText().toString())
                .addFormDataPart("email", emailField.getText().toString())
                .addFormDataPart("contrasena", passwordField.getText().toString())
                .addFormDataPart("telefono", phoneField.getText().toString())
                .addFormDataPart("foto", imageURL.toString(),
                        RequestBody.create(MEDIA_TYPE_IMAGE, new File(path))
                )
                .addFormDataPart("administrador", "false")
                .addFormDataPart("video", videoURL.toString(),
                        RequestBody.create(MEDIA_TYPE_VIDEO, new File(pathV))
                )
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
                Log.d(TAG, response.body().string());
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);

            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client2.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "CreateUser Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.marcoslopez7.proyectofinal/http/host/path")
        );
        AppIndex.AppIndexApi.start(client2, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "CreateUser Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.marcoslopez7.proyectofinal/http/host/path")
        );
        AppIndex.AppIndexApi.end(client2, viewAction);
        client2.disconnect();
    }

    public void update(){
        if(!validate()){
            Toast t = Toast.makeText(getApplicationContext(), "Please fill all the camps", Toast.LENGTH_SHORT);
            t.show();
            return;
        }
        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody;
        if (path.equals("") && pathV.equals("")) {
            requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("nombre", nameField.getText().toString())
                    .addFormDataPart("apellidos", lastNameField.getText().toString())
                    .addFormDataPart("email", emailField.getText().toString())
                    .addFormDataPart("contrasena", passwordField.getText().toString())
                    .addFormDataPart("telefono", phoneField.getText().toString())
                    .addFormDataPart("administrador", "false")
                    .build();
        } else if (pathV.equals("")){
            requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("nombre", nameField.getText().toString())
                    .addFormDataPart("apellidos", lastNameField.getText().toString())
                    .addFormDataPart("email", emailField.getText().toString())
                    .addFormDataPart("contrasena", passwordField.getText().toString())
                    .addFormDataPart("telefono", phoneField.getText().toString())
                    .addFormDataPart("foto", imageURL.toString(),
                            RequestBody.create(MEDIA_TYPE_IMAGE, new File(path))
                    )
                    .addFormDataPart("administrador", "false")
                    .build();
        } else {
            requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("nombre", nameField.getText().toString())
                    .addFormDataPart("apellidos", lastNameField.getText().toString())
                    .addFormDataPart("email", emailField.getText().toString())
                    .addFormDataPart("contrasena", passwordField.getText().toString())
                    .addFormDataPart("telefono", phoneField.getText().toString())
                    .addFormDataPart("foto", imageURL.toString(),
                            RequestBody.create(MEDIA_TYPE_IMAGE, new File(path))
                    )
                    .addFormDataPart("administrador", "false")
                    .addFormDataPart("video", videoURL.toString(),
                            RequestBody.create(MEDIA_TYPE_VIDEO, new File(pathV))
                    )
                    .build();
        }
        Request request = new Request.Builder()
                .url(url_update)
                .put(requestBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.d(TAG, response.body().string());
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);

            }
        });
    }

    private boolean is_valid(){
        return Patterns.EMAIL_ADDRESS.matcher(emailField.getText().toString()).matches();
    }
    private boolean validate(){
        if(nameField.getText().toString().equals("") || lastNameField.getText().toString().equals("") | emailField.getText().toString().equals("") || passwordField.getText().toString().equals("") || phoneField.getText().toString().equals("") || imageURL.toString().equals("") || videoURL.toString().equals("")){
            return false;
        }
        else{
            return true;
        }
    }

    private void llenar(){
        if (SessionHelper.id_user > 0){
            OkHttpClient client = new OkHttpClient();

            Request request = new Request.Builder().url(url_llenar).build();

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
                                    llenarcampos(jsondata);
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
                                //Toast.makeText(getApplicationContext(), "User o password invalids",
                                //Toast.LENGTH_LONG).show();
                            }
                        });

                    }
                /**/

                }
            });
        }
    }
    private void llenarcampos (String s) throws JSONException{
        JSONObject js = new JSONObject(s);
        nameField.setText(js.getString("nombre"));
        lastNameField.setText(js.getString("apellidos"));
        emailField.setText(js.getString("email"));
        phoneField.setText(js.getString("telefono"));
    }
}
