package com.example.levanteappproto;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.example.levanteappproto.utils.GenerateView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GruposActivity extends AppCompatActivity {

    private static final String TAG = "GruposActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grupos);

        String[] estados = getResources().getStringArray(R.array.estados);
        String[] defensorias = getResources().getStringArray(R.array.defensorias);

        LinearLayout defensoriasLayout = findViewById(R.id.defensoriasLayout);
        for (int i = 0;i < estados.length; i++) {
            defensoriasLayout.addView(GenerateView.generateTitleDescription(estados[i],defensorias[i], this));
        }

        ListView entidadesList= findViewById(R.id.entidadesList);
        List<String> entidades = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.entidades)));
        Log.d(TAG, "onCreate: entidades " + entidades);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1, entidades);
        entidadesList.setAdapter(adapter);
        entidadesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String[] links = getResources().getStringArray(R.array.entidades_links);
                try {
                    Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(links[position]));
                    startActivity(myIntent);
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(getApplicationContext(), "Sem browser para abrir o link.",  Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            }
        });
    }
}
