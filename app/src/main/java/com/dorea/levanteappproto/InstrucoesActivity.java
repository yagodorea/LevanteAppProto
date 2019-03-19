package com.dorea.levanteappproto;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class InstrucoesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instrucoes);

        try {
            WebView webView = findViewById(R.id.instrucoesWebView);
            webView.setWebViewClient(new WebViewClient());

            InputStream info = getResources().openRawResource(R.raw.instrucoes);
            BufferedReader br = new BufferedReader(new InputStreamReader(info, "utf-8"));
            StringBuilder stringBuilder = new StringBuilder();

            String line;
            while ((line = br.readLine()) != null) {
                stringBuilder.append(line);
            }

            webView.loadData(stringBuilder.toString(),"text/html","utf-8");
        } catch (IOException e) {
            Toast.makeText(this,"Não foi possível encontrar um arquivo,", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }

    }
}
