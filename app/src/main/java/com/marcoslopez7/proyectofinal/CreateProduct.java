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
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.File;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CreateProduct extends AppCompatActivity {

    private Button b_select_pic, b_submit;
    private EditText name_et, description_et, price_et;
    private TextView photo_tv;
    private Spinner spin;
    private Uri imageURL;
    private static final int PICK_FROM_CAMERA = 1, PICK_FROM_FILE = 2;
    private static final MediaType MEDIA_TYPE_IMAGE = MediaType.parse("image/*");
    private static final String url = "http://159.203.166.99:8000/products/createproduct/", TAG = CreateUserActivity.class.getSimpleName();
    private static final String url2 = "http://159.203.166.99:8000/products/categorias/";
    private static final String url_put = "http://159.203.166.99:8000/products/edit/";
    private int id_p;

    private String path = "";
    private String[] items;
    JSONArray arr;
    int Resp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_product);
        init();
        Intent esta = getIntent();

        if(esta.getBooleanExtra(getResources().getString(R.string.Owner), false)){
            b_submit.setText("Update");
            b_submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    update();
                }
            });
            id_p = esta.getIntExtra(getResources().getString(R.string.Id_producto),0);
        }
        else{
            b_submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    submit();
                }
            });
        }

        final String[] plop = new String[]{"Camara", "Galeria"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.select_dialog_item, plop);

        spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                try {
                    Resp = arr.getJSONObject(arg2).getInt("pk");
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub
                try {
                    Resp = arr.getJSONObject(0).getInt("pk");
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }

        });
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
        b_select_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show();
            }
        });


    }

    private void init(){
        b_select_pic = (Button) findViewById(R.id.b_picture);
        b_submit = (Button) findViewById(R.id.b_submit);
        name_et = (EditText) findViewById(R.id.et_name);
        description_et = (EditText) findViewById(R.id.et_description);
        price_et = (EditText) findViewById(R.id.et_price);
        photo_tv = (TextView) findViewById(R.id.tv_picture);
        spin = (Spinner)findViewById(R.id.s_category);
        OkHttpClient client2;

        client2 = new OkHttpClient();

        Request request = new Request.Builder().url(url2).build();

        client2.newCall(request).enqueue(new Callback() {
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
        }  else
            path = imageURL.getPath();

        photo_tv.setText(path);
    }

    private String getRealImagePath(Uri content) {
        String[] data = {MediaStore.Images.Media.DATA};
        Cursor cursor = managedQuery(content, data, null, null, null);
        if (cursor == null)
            return null;
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    private void submit() {
        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("nombre", name_et.getText().toString())
                .addFormDataPart("descripcion", description_et.getText().toString())
                .addFormDataPart("precio", price_et.getText().toString())
                .addFormDataPart("foto", imageURL.toString(),
                        RequestBody.create(MEDIA_TYPE_IMAGE, new File(path))
                )
                .addFormDataPart("ultima_modificacion", "yo")
                .addFormDataPart("aprobado", "false")
                .addFormDataPart("vendido", "false")
                .addFormDataPart("usuario", ""+SessionHelper.id_user)
                .addFormDataPart("categoria", ""+Resp)
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
                Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                startActivity(intent);
            }
        });

    }

    private void update() {
        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("nombre", name_et.getText().toString())
                .addFormDataPart("descripcion", description_et.getText().toString())
                .addFormDataPart("precio", price_et.getText().toString())
                .addFormDataPart("foto", imageURL.toString(),
                        RequestBody.create(MEDIA_TYPE_IMAGE, new File(path))
                )
                .addFormDataPart("ultima_modificacion", "yo")
                .addFormDataPart("aprobado", "false")
                .addFormDataPart("vendido", "false")
                .addFormDataPart("usuario", ""+SessionHelper.id_user)
                .addFormDataPart("categoria", ""+Resp)
                .build();

        Request request = new Request.Builder()
                .url(url_put+id_p+"/")
                .put(requestBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
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
}
