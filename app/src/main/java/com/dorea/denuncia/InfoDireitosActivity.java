package com.dorea.denuncia;

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

public class InfoDireitosActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_direitos);

        try {
            WebView webView = findViewById(R.id.infoDireitosWebview);
            webView.setWebViewClient(new WebViewClient());

            InputStream info = getResources().openRawResource(R.raw.infodireitos);
            BufferedReader br = new BufferedReader(new InputStreamReader(info, "utf-8"));
            StringBuilder stringBuilder = new StringBuilder();

            String line;
            while ((line = br.readLine()) != null) {
                stringBuilder.append(line);
            }

            webView.getSettings().setDefaultTextEncodingName("utf-8");
            webView.loadData(stringBuilder.toString(),"text/html","utf-8");
        } catch (IOException e) {
            Toast.makeText(this,"Não foi possível encontrar um arquivo,", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }

    }
}
