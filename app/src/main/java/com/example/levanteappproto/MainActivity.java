package com.example.levanteappproto;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final int COD_DENUNCIA = 1;

    private Button btnDenuncia;
    private Button btnDireitos;
    private Button btnGrupos;
    private Button btnNoticias;
    private Button btnInstrucoes;
    private Button btnSobre;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Atribuições dos botões
        btnDenuncia = (Button) findViewById(R.id.btnDenuncia);
        btnDireitos = (Button) findViewById(R.id.btnDireitos);
        btnGrupos = (Button) findViewById(R.id.btnGrupos);
        btnNoticias = (Button) findViewById(R.id.btnNoticias);
        btnInstrucoes = (Button) findViewById(R.id.btnInstrucoes);
        btnSobre = (Button) findViewById(R.id.btnSobre);

        // Ir para atividade DenunciaActivity
        btnDenuncia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this,DenunciaActivity.class);
                startActivityForResult(i,COD_DENUNCIA);
            }
        });

        btnDireitos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toastMessage("Saiba quais são os seus direitos.");
            }
        });

        btnGrupos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toastMessage("Contatos de grupos de Direitos Humanos.");
            }
        });

        btnNoticias.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toastMessage("Notícias sobre segurança pública.");
            }
        });

        btnInstrucoes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toastMessage("Como se proteger.");
            }
        });

        btnSobre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toastMessage("Sobre o aplicativo.");
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == COD_DENUNCIA) {
            if (resultCode == RESULT_OK) {
                toastMessage("Denúncia realizada com sucesso!");
            }
            if (resultCode == RESULT_CANCELED) {
                toastMessage("Denúncia cancelada.");
            }
        }
    }

    private void toastMessage(String message) {
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
    }
}
