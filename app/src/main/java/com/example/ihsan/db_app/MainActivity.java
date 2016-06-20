package com.example.ihsan.db_app;

import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.entity.StrictContentLengthStrategy;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.message.BufferedHeader;
import org.apache.http.params.HttpConnectionParams;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    EditText etName, etSurname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etName = (EditText) findViewById(R.id.etName);
        etSurname = (EditText) findViewById(R.id.etSurname);

        Button btnSubmit = (Button) findViewById(R.id.btnSubmit);

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                send(etName.getText().toString(),etSurname.getText().toString());
            }
        });
    }


    public void send(final String name, final String surname){

        Thread t = new Thread(){

            public void run(){

                Looper.prepare();

                HttpClient client = new DefaultHttpClient();
                HttpConnectionParams.setConnectionTimeout(client.getParams(),20000);
                HttpResponse response;

                try{

                    HttpPost post = new HttpPost("http://ihsanbasaran.info.preview.services/kaydet.php");
                    List<NameValuePair> dlist_submit = new ArrayList<NameValuePair>(2);

                    dlist_submit.add(new BasicNameValuePair("ad",name));
                    dlist_submit.add(new BasicNameValuePair("soyad",surname));

                    post.setEntity(new UrlEncodedFormEntity(dlist_submit,"UTF-8"));

                    String html = "";
                    response = client.execute(post);

                    if(response != null){

                        InputStream in = response.getEntity().getContent();
                        BufferedReader reader = new BufferedReader(new InputStreamReader(in));

                        StringBuilder sb = new StringBuilder();
                        String rows = null;

                        try {

                            while ((rows = reader.readLine()) != null){

                                sb.append(rows + "\n");

                            }

                        }catch (IOException e){
                            e.printStackTrace();
                        }finally {
                            try {
                                in.close();
                            }catch (IOException e){
                                e.printStackTrace();

                            }
                        }

                        html = sb.toString();

                        JSONArray split = new JSONArray(html);
                        if(split.length() > 0){

                            JSONObject obj = split.getJSONObject(0);

                            if(obj.getBoolean("islem") != true){
                                Toast.makeText(MainActivity.this, "Hata: Form gönderilemedi", Toast.LENGTH_SHORT).show();
                            }else {
                                Toast.makeText(MainActivity.this, "Başarıyla gönderildi", Toast.LENGTH_SHORT).show();
                            }
                        }

                    }


                }catch (Exception e){
                    Log.e("Hata: ", String.valueOf(e));
                }

                Looper.loop();
            }
        };

        t.start();
    }
}
